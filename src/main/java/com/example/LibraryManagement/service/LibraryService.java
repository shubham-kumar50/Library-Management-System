package com.example.LibraryManagement.service;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.model.Fine;
import com.example.LibraryManagement.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class LibraryService {

    // Store books in the library
    private final List<Book> books = new ArrayList<>();

    // Track books by category
    private final Map<String, Set<Book>> booksByCategory = new HashMap<>();

    // Track reservations: A map from a book to a queue of users waiting for it
    private final Map<Book, Queue<User>> reservations = new HashMap<>();

    // Track borrowed books and due dates
    private final Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    // Track fines for each user
    private final Map<User, List<Fine>> userFines = new HashMap<>();

    // Track the number of times each book is borrowed
    private final Map<Book, Integer> borrowCounts = new HashMap<>();

    private static final double FINE_PER_DAY = 5.0;  // Fine per day for late returns

    // Add a new book to the library
    public Book addBook(Book book) {
        books.add(book);
        booksByCategory.computeIfAbsent(book.getCategory(), k -> new HashSet<>()).add(book);
        return book;
    }

    // Borrow a book for a user
    public boolean borrowBook(int bookId, User user) {
        Optional<Book> bookOpt = getBookById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (!book.isBorrowed()) {
                book.setBorrowed(true);
                borrowedBooks.put(book, LocalDate.now().plusDays(14)); // Due in 14 days
                borrowCounts.put(book, borrowCounts.getOrDefault(book, 0) + 1);
                return true;
            } else {
                // Add user to reservation if book is already borrowed
                reservations.computeIfAbsent(book, k -> new LinkedList<>()).offer(user);
            }
        }
        return false;
    }

    // Return a book and check if any user has reserved it
    public boolean returnBook(int bookId, User user) {
        Optional<Book> bookOpt = getBookById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.isBorrowed()) {
                // Check for late return
                LocalDate dueDate = borrowedBooks.remove(book);
                if (LocalDate.now().isAfter(dueDate)) {
                    long daysLate = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
                    addFine(user, book, daysLate * FINE_PER_DAY);
                }

                book.setBorrowed(false); // Book is now returned

                // Check for reservations and lend the book to the next user in line
                Queue<User> reservationQueue = reservations.get(book);
                if (reservationQueue != null && !reservationQueue.isEmpty()) {
                    User nextUser = reservationQueue.poll();
                    borrowBook(book.getId(), nextUser);
                }
                return true;
            }
        }
        return false;
    }

    // Add a fine to a user
    private void addFine(User user, Book book, double amount) {
        Fine fine = new Fine(user.getId(), book.getId(), amount);
        userFines.computeIfAbsent(user, k -> new ArrayList<>()).add(fine);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return books;
    }

    // Get books by category
    public Set<Book> getBooksByCategory(String category) {
        return booksByCategory.getOrDefault(category, Collections.emptySet());
    }

    // Get fines for a specific user
    public List<Fine> getFinesForUser(User user) {
        return userFines.getOrDefault(user, Collections.emptyList());
    }

    // Get the most borrowed books
    public List<Book> getTopBorrowedBooks() {
        List<Book> sortedBooks = new ArrayList<>(borrowCounts.keySet());
        sortedBooks.sort((b1, b2) -> borrowCounts.get(b2) - borrowCounts.get(b1));
        return sortedBooks;
    }

    // Find a book by ID
    private Optional<Book> getBookById(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst();
    }
}
