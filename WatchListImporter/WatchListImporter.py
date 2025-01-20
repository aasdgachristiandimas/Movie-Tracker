from flask import Flask, jsonify, request, send_file
import mysql.connector
import csv
import os

app = Flask(__name__)

# MySQL Database connection
def get_db_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="root",
        database="movietracker"
    )


# Export movie list to CSV
@app.route('/export_movies', methods=['GET'])
def export_movies():
    try:
        connection = get_db_connection()
        cursor = connection.cursor(dictionary=True)

        query = """
        SELECT m.title, m.release_year, g.genre_id, m.watch_status, m.rating, u.user_id
        FROM Movies m
        JOIN Genres g ON m.genre_id = g.genre_id
        JOIN Users u ON m.user_id = u.user_id
        """

        cursor.execute(query)
        movies = cursor.fetchall()

        # Prepare the CSV output
        csv_file_path = "Export/movie_list.csv"
        with open(csv_file_path, mode='w', newline='', encoding='utf-8') as file:
            writer = csv.DictWriter(file,
                                    fieldnames=["title", "release_year", "genre_id", "watch_status", "rating", "user_id"])
            writer.writeheader()
            for movie in movies:
                writer.writerow(movie)

        cursor.close()
        connection.close()

        # Send the CSV file as a response for download
        return send_file(csv_file_path, as_attachment=True)

    except Exception as e:
        print("Error:", e)  # Log the error
        return jsonify({"error": str(e)}), 500


# Import movie list from CSV
@app.route('/import_movies', methods=['POST'])
def import_movies():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file and file.filename.endswith('.csv'):
        try:
            if not os.path.exists('uploads'):
                os.makedirs('uploads')

            # Save the uploaded CSV file
            file_path = os.path.join('uploads', file.filename)
            file.save(file_path)

            # Open the CSV file and insert data into the database
            connection = get_db_connection()
            cursor = connection.cursor()

            with open(file_path, mode='r', encoding='utf-8') as csvfile:
                reader = csv.DictReader(csvfile)
                for row in reader:
                    try:
                        # Ensure required columns exist in the row (matching the CSV field names)
                        if 'user_id' not in row or 'genre_id' not in row:
                            print(f"Skipping row due to missing user_id or genre_id: {row}")
                            continue

                        # Retrieve user_id and genre_id based on the CSV data (using the CSV fields directly)
                        cursor.execute("SELECT user_id FROM Users WHERE user_id = %s", (row['user_id'],))
                        user_result = cursor.fetchone()
                        user_id = user_result[0] if user_result else None

                        cursor.execute("SELECT genre_id FROM Genres WHERE genre_id = %s", (row['genre_id'],))
                        genre_result = cursor.fetchone()
                        genre_id = genre_result[0] if genre_result else None

                        # Handle empty 'Rating' field (if it's allowed to be empty)
                        rating = row['rating'] if row['rating'] else None

                        # Insert movie data into the Movies table
                        cursor.execute("""
                        INSERT INTO Movies (user_id, title, release_year, genre_id, watch_status, rating)
                        VALUES (%s, %s, %s, %s, %s, %s)
                        """, (user_id, row['title'], row['release_year'], genre_id, row['watch_status'], rating))

                    except Exception as e:
                        print(f"Error inserting row: {row}. Error: {str(e)}")
                        continue  # Skip the row and continue processing others

            # Commit the transaction and close the connection
            connection.commit()
            cursor.close()
            connection.close()

            return jsonify({"message": "Movies imported successfully"}), 200

        except Exception as e:
            print("Error:", e)  # Log the error
            return jsonify({"error": str(e)}), 500

    else:
        return jsonify({"error": "Invalid file format. Only CSV is allowed."}), 400


if __name__ == '__main__':
    app.run(debug=True)
