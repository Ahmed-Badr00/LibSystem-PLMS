import java.io.Serializable;

public class Book implements Serializable, Comparable<Book> {
    private static final long serialVersionUID = 1L;
    private String title;
    private String author;
    private String isbn;
    private int yearOfPublication;
    private String genre;

    public Book(String title, String author, String isbn, int yearOfPublication, String genre) {
        if (!isValidISBN(isbn)) {
            throw new IllegalArgumentException("Invalid ISBN format. Must be 13 digits.");
        }
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.yearOfPublication = yearOfPublication;
        this.genre = genre;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getYearOfPublication() { return yearOfPublication; }
    public String getGenre() { return genre; }

    private boolean isValidISBN(String isbn) {
        return isbn != null && isbn.matches("\\d{13}");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn) || 
               (title.equals(book.title) && author.equals(book.author));
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return String.format("%s by %s (ISBN: %s, Year: %d, Genre: %s)",
                title, author, isbn, yearOfPublication, genre);
    }

    // Custom serialization format
    public String toCSV() {
        return String.format("%s,%s,%s,%d,%s",
                title, author, isbn, yearOfPublication, genre);
    }
}