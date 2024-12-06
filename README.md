# Personal Library Management System (PLMS)

## Course Information
- **Course**: CSCE3102 - Programming in Java (Fall 2024)
- **Institution**: The American University in Cairo

## Developers
- **Ahmed Badr**
  - ID: 900202868
  - Email: ahmedbadr00@aucegypt.edu
- **Bemen Girgis**
  - ID: 900213066
  - Email: bemen@aucegypt.edu

## Project Overview
The Personal Library Management System (PLMS) is a Java-based application that helps users manage their book collection. It provides a graphical user interface for adding, removing, searching, and organizing books with various features like sorting, filtering, and file persistence.

## Features

### Core Features
1. **Book Management**
   - Add books with details (Title, Author, ISBN, Year, Genre)
   - Remove books by ISBN or table selection
   - Search books by Title, Author, or ISBN
   - Display all books in a sortable table

2. **Categorization**
   - Group books by genre using TreeMap
   - Filter books by specific genres
   - Dynamic genre list updates

3. **File Persistence**
   - Automatic save on application close
   - Load books on startup
   - Manual load option available

### Advanced Features
1. **Serialization**
   - Custom serialization mechanism
   - Version control for data format
   - Error handling for data corruption

2. **File Operations**
   - Export books to CSV format
   - Import books from CSV files
   - Automatic backup before operations

3. **Sorting and Filtering**
   - Sort by Title, Author, Year, or Genre
   - Filter by Genre
   - Combination of sort and filter options

## Technical Implementation

### Design Choices
1. **Data Structures**
   - `HashMap` for ISBN-based book storage
   - `TreeMap` for genre-based categorization
   - `TreeSet` for maintaining sorted collections
   - Avoided ArrayList as per requirements

2. **GUI Implementation**
   - Swing-based user interface
   - Grid layout for form inputs
   - JTable for book display
   - Dynamic updates for all operations

3. **File Handling**
   - Buffered operations for improved performance
   - Custom serialization format
   - CSV support for data portability

### Validation Rules
1. **ISBN Validation**
   - Must be exactly 13 digits
   - Must be unique in the system
   - Required field

2. **Book Validation**
   - No duplicate Title + Author combinations
   - Valid year format
   - All fields required
   - Genre case-sensitive

## How to Run the Project

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Any operating system (Windows, Linux, macOS)

### Compilation
Navigate to the project directory and compile the Java files:
```bash
javac Book.java LibraryManager.java LibraryGUI.java
```

### Running the Application
After compilation, run the main application:
```bash
java LibraryGUI
```

### Testing
To run the test suite:
```bash
javac LibraryTest.java
java LibraryTest
```

### Sample Data
A sample CSV file is provided for testing:
```bash
# Import using the GUI or copy to project directory
sample_books.csv
```
```

## Usage Guide

### Adding Books
1. Fill in all fields in the form
2. Click "Add Book"
3. System validates and adds the book

### Removing Books
Two methods available:
1. Select book in table and click "Remove Selected"
2. Enter ISBN and click "Remove by ISBN"

### Searching
1. Enter search term
2. Select search type (Title/Author/ISBN)
3. Click "Search" or "Clear Search"

### Importing/Exporting
- Click "Import from CSV" to load books
- Click "Export to CSV" to save current collection

### Sorting and Filtering
- Use "Sort by" dropdown for different sort orders
- Use "Filter by Genre" to show specific genres
- Combinations of sort and filter are supported

## Error Handling
The system handles various errors:
- Invalid ISBN format
- Duplicate books
- Invalid year format
- File operation errors
- Data corruption
- CSV format errors

## Known Limitations
- No support for batch operations
- Single file backup only
- No undo/redo functionality
- Case-sensitive genre matching

## Future Enhancements
Possible improvements:
1. Multiple file format support
2. Batch import/export
3. Advanced search options
4. Statistics and reporting
5. Multi-user support
6. Cloud backup integration

## Contributing
Please contact the developers for any contributions or suggestions.

## License
This project is part of the CSCE3102 course requirements and is not licensed for public use.