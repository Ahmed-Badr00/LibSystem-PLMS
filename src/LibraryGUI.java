import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class LibraryGUI extends JFrame {
    private LibraryManager libraryManager;
    private JTextField titleField, authorField, isbnField, yearField, genreField;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterGenreCombo;
    private JComboBox<String> sortByCombo;
    private JComboBox<String> searchType;
    private JTextField searchField;

    public LibraryGUI() {
        libraryManager = new LibraryManager();
        setTitle("Personal Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        initializeGenreFilter();
        pack();
        setLocationRelativeTo(null);
        setSize(1000, 700);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                libraryManager.saveBooks();
            }
        });
    }

    private void initializeGenreFilter() {
        // Clear existing items
        filterGenreCombo.removeAllItems();
        filterGenreCombo.addItem("All Genres");
        
        // Get all genres from loaded books
        Set<String> genres = libraryManager.getAllBooks().stream()
            .map(Book::getGenre)
            .filter(genre -> genre != null && !genre.trim().isEmpty())
            .sorted()
            .collect(java.util.stream.Collectors.toSet());
        
        // Add all genres to combo box
        for (String genre : genres) {
            filterGenreCombo.addItem(genre);
        }
        
        // Set default selection
        filterGenreCombo.setSelectedItem("All Genres");
        
        // Update the table display
        updateBookTable();
    }

    private void initComponents() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        isbnField = new JTextField(20);
        yearField = new JTextField(20);
        genreField = new JTextField(20);

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("ISBN (13 digits):"));
        inputPanel.add(isbnField);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Genre:"));
        inputPanel.add(genreField);

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> addBook());
        inputPanel.add(addButton);

        // Load Books Button
        JButton loadBooksButton = new JButton("Load All Books");
        loadBooksButton.addActionListener(e -> {
            libraryManager.loadBooks();
            
            // First reset all filters and sorts
            filterGenreCombo.removeAllItems();
            filterGenreCombo.addItem("All Genres");
            sortByCombo.setSelectedItem("Title");
            
            // Then update the genre filter with all available genres
            Set<String> genres = libraryManager.getAllBooks().stream()
                .map(Book::getGenre)
                .sorted()
                .collect(java.util.stream.Collectors.toSet());
            
            for (String genre : genres) {
                filterGenreCombo.addItem(genre);
            }
            
            // Select "All Genres" by default
            filterGenreCombo.setSelectedItem("All Genres");
            
            updateBookTable();
            
            int bookCount = libraryManager.getAllBooks().size();
            if (bookCount == 0) {
                JOptionPane.showMessageDialog(this, "No books found in the library!");
            } else {
                JOptionPane.showMessageDialog(this, 
                    String.format("%d book%s loaded successfully!", 
                    bookCount, 
                    bookCount == 1 ? "" : "s"));
            }
        });
        inputPanel.add(loadBooksButton);

        // Table
        String[] columns = {"Title", "Author", "ISBN", "Year", "Genre"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelectedBook());

        // ISBN Remove Panel
        JPanel isbnRemovePanel = new JPanel(new FlowLayout());
        JTextField isbnRemoveField = new JTextField(13);
        JButton removeByIsbnButton = new JButton("Remove by ISBN");
        removeByIsbnButton.addActionListener(e -> {
            String isbn = isbnRemoveField.getText().trim();
            if (isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter ISBN!");
                return;
            }
            if (libraryManager.removeBook(isbn)) {
                updateBookTable();
                updateGenreFilter();
                isbnRemoveField.setText("");
                JOptionPane.showMessageDialog(this, "Book removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!");
            }
        });
        isbnRemovePanel.add(new JLabel("ISBN:"));
        isbnRemovePanel.add(isbnRemoveField);
        isbnRemovePanel.add(removeByIsbnButton);

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> {
            libraryManager.exportToCSV();
            JOptionPane.showMessageDialog(this, "Books exported successfully!");
        });

        JButton importButton = new JButton("Import from CSV");
        importButton.addActionListener(e -> importFromCSV());

        sortByCombo = new JComboBox<>(new String[]{"Title", "Author", "Year", "Genre"});
        sortByCombo.addActionListener(e -> updateBookTable());

        filterGenreCombo = new JComboBox<>();
        filterGenreCombo.addItem("All Genres");
        filterGenreCombo.addActionListener(e -> updateBookTable());

        controlPanel.add(removeButton);
        controlPanel.add(exportButton);
        controlPanel.add(importButton);
        controlPanel.add(new JLabel("Sort by:"));
        controlPanel.add(sortByCombo);
        controlPanel.add(new JLabel("Filter by Genre:"));
        controlPanel.add(filterGenreCombo);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchType = new JComboBox<>(new String[]{"Title", "Author", "ISBN"});
        JButton searchButton = new JButton("Search");
        JButton clearSearchButton = new JButton("Clear Search");

        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            updateBookTable();
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchType);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);

        // Main Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(isbnRemovePanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        updateBookTable();
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            updateBookTable();
            return;
        }

        List<Book> results = switch (searchType.getSelectedItem().toString()) {
            case "Title" -> libraryManager.searchByTitle(searchText);
            case "Author" -> libraryManager.searchByAuthor(searchText);
            case "ISBN" -> {
                Book book = libraryManager.getBookByISBN(searchText);
                yield book != null ? List.of(book) : new ArrayList<>();
            }
            default -> new ArrayList<>();
        };

        displayBooks(results);
    }

    private void addBook() {
        try {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            String genre = genreField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || genre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            Book book = new Book(title, author, isbn, year, genre);
            if (libraryManager.addBook(book)) {
                clearInputFields();
                updateBookTable();
                updateGenreFilter();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Book with this ISBN already exists!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid year format!");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearInputFields() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        yearField.setText("");
        genreField.setText("");
        titleField.requestFocus();
    }

    private void removeSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            String isbn = (String) tableModel.getValueAt(selectedRow, 2);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this book?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (libraryManager.removeBook(isbn)) {
                    updateBookTable();
                    updateGenreFilter();
                    JOptionPane.showMessageDialog(this, "Book removed successfully!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to remove.");
        }
    }

    private void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            libraryManager.importFromCSV(fileChooser.getSelectedFile().getPath());
            libraryManager.saveBooks(); // Auto-save after import
            
            // Reset filters
            filterGenreCombo.setSelectedItem("All Genres");
            sortByCombo.setSelectedItem("Title");
            
            // Update UI
            updateGenreFilter();
            updateBookTable();
            
            JOptionPane.showMessageDialog(this, "Books imported successfully!");
        }
    }

    private void updateGenreFilter() {
        String selectedGenre = (String) filterGenreCombo.getSelectedItem();
        
        // Store current selection
        filterGenreCombo.removeAllItems();
        filterGenreCombo.addItem("All Genres");
        
        // Get all unique genres from current books
        Set<String> genres = libraryManager.getAllBooks().stream()
            .map(Book::getGenre)
            .filter(genre -> genre != null && !genre.trim().isEmpty())
            .sorted()
            .collect(java.util.stream.Collectors.toSet());
        
        // Add all genres to combo box
        for (String genre : genres) {
            filterGenreCombo.addItem(genre);
        }
        
        // Restore previous selection if valid, otherwise select "All Genres"
        if (selectedGenre != null && genres.contains(selectedGenre)) {
            filterGenreCombo.setSelectedItem(selectedGenre);
        } else {
            filterGenreCombo.setSelectedItem("All Genres");
        }
    }

    private void updateBookTable() {
        List<Book> books;
        String selectedGenre = (String) filterGenreCombo.getSelectedItem();
        if (selectedGenre != null && !selectedGenre.equals("All Genres")) {
            books = new ArrayList<>(libraryManager.getBooksByGenre(selectedGenre));
        } else {
            books = libraryManager.getAllBooks();
        }

        // Apply sorting
        String sortBy = (String) sortByCombo.getSelectedItem();
        if (sortBy != null) {
            switch (sortBy) {
                case "Author" -> books.sort(Comparator.comparing(Book::getAuthor));
                case "Year" -> books.sort(Comparator.comparing(Book::getYearOfPublication));
                case "Genre" -> books.sort(Comparator.comparing(Book::getGenre));
                default -> books.sort(Comparator.comparing(Book::getTitle));
            }
        }

        displayBooks(books);
    }

    private void displayBooks(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getYearOfPublication(),
                book.getGenre()
            });
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LibraryGUI().setVisible(true);
        });
    }
}