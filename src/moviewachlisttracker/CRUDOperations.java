/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moviewachlisttracker;

/**
 *
 * @author User
 */
import java.sql.*;

public class CRUDOperations {
    public void addMovie(int userId, String title, int releaseYear, int genreId, String watchStatus, Double rating) {
        String sql = "INSERT INTO Movies (user_id, title, release_year, genre_id, watch_status, rating) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setInt(3, releaseYear);
            stmt.setInt(4, genreId);
            stmt.setString(5, watchStatus);
            stmt.setObject(6, rating, Types.DOUBLE);
            stmt.executeUpdate();
            conn.commit();
            System.out.println("Movie added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewMoviesByUser(int userId) {
        String sql = "SELECT m.title, m.release_year, g.genre_name, m.watch_status, m.rating " +
                     "FROM Movies m LEFT JOIN Genres g ON m.genre_id = g.genre_id WHERE m.user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.printf("Title: %s, Year: %d, Genre: %s, Status: %s, Rating: %.1f%n",
                        rs.getString("title"),
                        rs.getInt("release_year"),
                        rs.getString("genre_name"),
                        rs.getString("watch_status"),
                        rs.getDouble("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMovieStatus(int movieId, String watchStatus) {
        String sql = "UPDATE Movies SET watch_status = ? WHERE movie_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            stmt.setString(1, watchStatus);
            stmt.setInt(2, movieId);
            int rowsUpdated = stmt.executeUpdate();
            conn.commit();
            System.out.println(rowsUpdated + " movie(s) updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMovie(int movieId) {
        String sql = "DELETE FROM Movies WHERE movie_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            stmt.setInt(1, movieId);
            int rowsDeleted = stmt.executeUpdate();
            conn.commit();
            System.out.println(rowsDeleted + " movie(s) deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public java.util.List<String> getMoviesForDisplay() {
    String sql = "SELECT m.movie_id, m.title, m.release_year, g.genre_name, m.watch_status, m.rating " +
                 "FROM Movies m " +
                 "JOIN Genres g ON m.genre_id = g.genre_id";
    java.util.List<String> movies = new java.util.ArrayList<>();

    try (Connection conn = DatabaseManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            int id = rs.getInt("movie_id");
            String title = rs.getString("title");
            int year = rs.getInt("release_year");
            String genre = rs.getString("genre_name");
            String status = rs.getString("watch_status");
            double rating = rs.getDouble("rating");

            // Format movie information
            movies.add(String.format("ID: %d, Title: %s, Year: %d, Genre: %s, Status: %s, Rating: %.1f",
                    id, title, year, genre, status, rating));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return movies;
}

}
