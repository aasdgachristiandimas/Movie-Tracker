import javax.swing.*;
import java.awt.*;
import java.util.List;
import moviewachlisttracker.CRUDOperations;

public class MovieTrackerGUI extends JFrame {
    private final WatchlistAPI watchlistAPI;
    private final CRUDOperations crudOperations;

    public MovieTrackerGUI() {
        watchlistAPI = new WatchlistAPI();
        crudOperations = new CRUDOperations(); // Initialize CRUDOperations
        setTitle("Movie Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        // Buttons for CRUD Operations and Import/Export
        JButton addMovieButton = new JButton("Add Movie");
        JButton viewMoviesButton = new JButton("View Movies");
        JButton updateMovieButton = new JButton("Update Movie Status");
        JButton deleteMovieButton = new JButton("Delete Movie");
        JButton importButton = new JButton("Import Watch List");
        JButton exportButton = new JButton("Export Watch List");

        // Add Action Listeners
        addMovieButton.addActionListener(e -> showAddMovieDialog());
        viewMoviesButton.addActionListener(e -> showMovies());
        updateMovieButton.addActionListener(e -> showUpdateMovieDialog());
        deleteMovieButton.addActionListener(e -> showDeleteMovieDialog());
        importButton.addActionListener(e -> watchlistAPI.importWatchList());
        exportButton.addActionListener(e -> watchlistAPI.exportWatchList());

        // Add buttons to panel
        panel.add(addMovieButton);
        panel.add(viewMoviesButton);
        panel.add(updateMovieButton);
        panel.add(deleteMovieButton);
        panel.add(importButton);
        panel.add(exportButton);

        // Add panel to frame
        add(panel, BorderLayout.CENTER);
    }

    // Show dialog to add a movie to the watch list
    private void showAddMovieDialog() {
        JTextField titleField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField ratingField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Watching", "Watched", "To Watch"});

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);
        panel.add(new JLabel("Status:"));
        panel.add(statusComboBox);

        int option = JOptionPane.showConfirmDialog(this, panel, "Add Movie", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            int year = Integer.parseInt(yearField.getText());
            double rating = Double.parseDouble(ratingField.getText());
            String status = (String) statusComboBox.getSelectedItem();
            
            // Assuming userId is available, here using a placeholder (this can be dynamic based on your system)
            int userId = 1; 
            int genreId = 1; // Placeholder for genreId
            crudOperations.addMovie(userId, title, year, genreId, status, rating); 
            JOptionPane.showMessageDialog(this, "Movie Added: " + title + ", " + year + ", " + rating + ", Status: " + status);
        }
    }

    // View all movies in the watch list
    private void showMovies() {
        // Assuming userId is available, here using a placeholder
        int userId = 1;
        List<String> movies = crudOperations.getMoviesForDisplay();

        // Display movies in a dialog box
        StringBuilder movieList = new StringBuilder();
        for (String movie : movies) {
            movieList.append(movie).append("\n");
        }
        JOptionPane.showMessageDialog(this, movieList.toString());
    }

    // Show dialog to update a movie's status
    private void showUpdateMovieDialog() {
        JTextField titleField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Watching", "Watched", "To Watch"});

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Status:"));
        panel.add(statusComboBox);

        int option = JOptionPane.showConfirmDialog(this, panel, "Update Movie Status", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String status = (String) statusComboBox.getSelectedItem();

            // You would need to fetch movieId dynamically in a real app, this is just a placeholder
            int movieId = 1; 
            crudOperations.updateMovieStatus(movieId, status);
            JOptionPane.showMessageDialog(this, "Movie Updated: " + title + ", Status: " + status);
        }
    }

    // Show dialog to delete a movie
    private void showDeleteMovieDialog() {
        JTextField titleField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Delete Movie", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText();

            // You would need to fetch movieId dynamically in a real app, this is just a placeholder
            int movieId = 1; 
            crudOperations.deleteMovie(movieId);
            JOptionPane.showMessageDialog(this, "Movie Deleted: " + title);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieTrackerGUI::new);
    }
}
