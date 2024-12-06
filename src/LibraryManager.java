import java.io.*;
import java.util.*;

public class LibraryManager {
    private Map<String, Book> booksByISBN;
    private Map<String, Set<Book>> booksByGenre;
    private static final String DATA_FILE = "library_data.ser";
    private static final String CSV_FILE = "library_data.csv";

    public LibraryManager() {
        booksByISBN = new HashMap<>();
        booksByGenre = new TreeMap<>();
        loadBooks();
    }

    public boolean addBook(Book book) {
        // Check for duplicate ISBN
        if (booksByISBN.containsKey(book.getIsbn())) {
            return false;
        }

        // Check for duplicate title and author
        boolean duplicateExists = booksByISBN.values().stream()
            .anyMatch(existingBook -> 
                existingBook.getTitle().equalsIgnoreCase(book.getTitle()) &&
                existingBook.getAuthor().equalsIgnoreCase(book.getAuthor()));

        if (duplicateExists) {
            throw new IllegalArgumentException("A book with this title and author already exists!");
        }

        booksByISBN.put(book.getIsbn(), book);
        booksByGenre.computeIfAbsent(book.getGenre(), k -> new TreeSet<>()).add(book);
        return true;
    }

    public List<Book> getAllBooks() {
        return new LinkedList<>(booksByISBN.values());
    }

    public List<Book> searchByTitle(String title) {
        return booksByISBN.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .sorted()
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
    }

    public List<Book> searchByAuthor(String author) {
        return booksByISBN.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .sorted(Comparator.comparing(Book::getAuthor))
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
    }

    public Book getBookByISBN(String isbn) {
        return booksByISBN.get(isbn);
    }

    public Set<Book> getBooksByGenre(String genre) {
        return new TreeSet<>(booksByGenre.getOrDefault(genre, new TreeSet<>()));
    }

    public List<Book> getBooksSortedByYear() {
        return booksByISBN.values().stream()
                .sorted(Comparator.comparing(Book::getYearOfPublication))
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
    }

    public List<Book> getBooksSortedByAuthor() {
        return booksByISBN.values().stream()
                .sorted(Comparator.comparing(Book::getAuthor))
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
    }

    // Custom serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        // Write version number for future compatibility
        out.writeInt(1); // version 1
        
        // Write the number of books
        out.writeInt(booksByISBN.size());
        
        // Custom serialization for each book
        for (Book book : booksByISBN.values()) {
            // Write each field separately
            out.writeUTF(book.getTitle());
            out.writeUTF(book.getAuthor());
            out.writeUTF(book.getIsbn());
            out.writeInt(book.getYearOfPublication());
            out.writeUTF(book.getGenre());
            
            // Add custom metadata
            out.writeLong(System.currentTimeMillis()); // timestamp
            out.writeInt(book.toString().length()); // content length
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Read and verify version
        int version = in.readInt();
        if (version != 1) {
            throw new IOException("Unsupported version: " + version);
        }

        // Initialize collections
        booksByISBN = new HashMap<>();
        booksByGenre = new TreeMap<>();

        // Read number of books
        int size = in.readInt();

        // Custom deserialization for each book
        for (int i = 0; i < size; i++) {
            String title = in.readUTF();
            String author = in.readUTF();
            String isbn = in.readUTF();
            int year = in.readInt();
            String genre = in.readUTF();

            // Read metadata (but not using it in this version)
            long timestamp = in.readLong();
            int contentLength = in.readInt();

            // Create and add book
            addBook(new Book(title, author, isbn, year, genre));
        }
    }

    public void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(DATA_FILE)))) {
            writeObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBooks() {
        if (!new File(DATA_FILE).exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(DATA_FILE)))) {
            readObject(in);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exportToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            writer.write("Title,Author,ISBN,Year,Genre\n");
            for (Book book : booksByISBN.values()) {
                writer.write(book.toCSV() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFromCSV(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String header = reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        Book book = new Book(
                            parts[0], // title
                            parts[1], // author
                            parts[2], // isbn
                            Integer.parseInt(parts[3]), // year
                            parts[4]  // genre
                        );
                        addBook(book);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid book: " + line);
                        System.err.println("Reason: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removeBook(String isbn) {
        Book book = booksByISBN.remove(isbn);
        if (book != null) {
            Set<Book> genreBooks = booksByGenre.get(book.getGenre());
            if (genreBooks != null) {
                genreBooks.remove(book);
                if (genreBooks.isEmpty()) {
                    booksByGenre.remove(book.getGenre());
                }
            }
            return true;
        }
        return false;
    }
}