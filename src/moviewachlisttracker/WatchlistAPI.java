import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JFileChooser;

public class WatchlistAPI {

    private static final String API_URL = "http://127.0.0.1:5000";

    // Export the movie list from Flask API
    public void exportWatchList() {
        try {
            URL url = new URL(API_URL + "/export_movies");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully exported the movie list, save it as a CSV file
                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream("exported_movies.csv");
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                fileOutputStream.close();
                inputStream.close();
                System.out.println("Movies exported successfully!");
            } else {
                System.out.println("Failed to export movies. HTTP response code: " + responseCode);
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Import the movie list to Flask API
    public void importWatchList() {
        // Open file explorer to choose a CSV file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();
            try {
                URL url = new URL(API_URL + "/import_movies");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---boundary");

                try (OutputStream outputStream = connection.getOutputStream();
                     FileInputStream fileInputStream = new FileInputStream(csvFile)) {

                    String boundary = "---boundary";
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";

                    outputStream.write((twoHyphens + boundary + lineEnd).getBytes());
                    outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + csvFile.getName() + "\"" + lineEnd).getBytes());
                    outputStream.write(("Content-Type: text/csv" + lineEnd + lineEnd).getBytes());

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.write((lineEnd + twoHyphens + boundary + twoHyphens + lineEnd).getBytes());

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        System.out.println("Movies imported successfully!");
                    } else {
                        System.out.println("Failed to import movies. HTTP response code: " + responseCode);
                    }
                }

                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file selected.");
        }
    }
}
