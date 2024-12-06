public class LibraryTest {
    public static void main(String[] args) {
        testLibraryManager();
    }

    public static void testLibraryManager() {
        LibraryManager manager = new LibraryManager();
        
        // Test 1: Add a book
        System.out.println("Test 1: Adding a book");
        Book book1 = new Book("Test Book", "Test Author", "9780123456789", 2024, "Test Genre");
        boolean added = manager.addBook(book1);
        System.out.println("Book added: " + added);
        
        // Test 2: Add duplicate book
        System.out.println("\nTest 2: Adding duplicate book");
        Book book2 = new Book("Test Book", "Test Author", "9780123456789", 2024, "Test Genre");
        added = manager.addBook(book2);
        System.out.println("Duplicate book added: " + added + " (should be false)");
        
        // Test 3: Search by title
        System.out.println("\nTest 3: Search by title");
        var results = manager.searchByTitle("Test");
        System.out.println("Search results: " + results.size() + " books found");
        results.forEach(System.out::println);
        
        // Test 4: Remove book
        System.out.println("\nTest 4: Remove book");
        boolean removed = manager.removeBook("9780123456789");
        System.out.println("Book removed: " + removed);
        
        // Test 5: Import CSV
        System.out.println("\nTest 5: Import CSV");
        manager.importFromCSV("sample-books.csv");
        System.out.println("Total books after import: " + manager.getAllBooks().size());
        
        // Test 6: Get books by genre
        System.out.println("\nTest 6: Get books by genre");
        var fantasyBooks = manager.getBooksByGenre("Fantasy");
        System.out.println("Fantasy books: " + fantasyBooks.size());
        fantasyBooks.forEach(System.out::println);
        
        // Test 7: Get sorted books
        System.out.println("\nTest 7: Get books sorted by year");
        var sortedBooks = manager.getBooksSortedByYear();
        System.out.println("Books sorted by year:");
        sortedBooks.forEach(b -> 
            System.out.println(b.getYearOfPublication() + ": " + b.getTitle()));
        
        // Test 8: Export to CSV
        System.out.println("\nTest 8: Export to CSV");
        manager.exportToCSV();
        System.out.println("Books exported to library_data.csv");
    }
}