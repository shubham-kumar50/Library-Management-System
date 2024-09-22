package com.example.LibraryManagement.controller;

import com.example.LibraryManagement.model.Book;
import com.example.LibraryManagement.model.Fine;
import com.example.LibraryManagement.model.User;
import com.example.LibraryManagement.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Add a new book
    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(libraryService.addBook(book));
    }

    @PostMapping("/books/multiple")
    public ResponseEntity<String> addMultipleBooks(@RequestBody List<Book> books) {
        books.forEach(libraryService::addBook);  // Add each book using the service
        return ResponseEntity.ok("All books added successfully.");
    }

    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody User user, @RequestParam int bookId) {
        boolean success = libraryService.borrowBook(bookId, user);
        return success ? ResponseEntity.ok("Book borrowed successfully.") :
                ResponseEntity.badRequest().body("Book is already borrowed or doesn't exist.");
    }

    // Return a book
    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody User user, @RequestParam int bookId) {
        boolean success = libraryService.returnBook(bookId, user);
        return success ? ResponseEntity.ok("Book returned successfully.") :
                ResponseEntity.badRequest().body("Error in returning the book.");
    }

    // View all books
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    // View books by category
    @GetMapping("/books/category")
    public ResponseEntity<Set<Book>> getBooksByCategory(@RequestParam String category) {
        return ResponseEntity.ok(libraryService.getBooksByCategory(category));
    }

    // Get fines for a user
    @PostMapping("/fines")
    public ResponseEntity<List<Fine>> getFines(@RequestBody User user) {
        return ResponseEntity.ok(libraryService.getFinesForUser(user));
    }

    // View top borrowed books
    @GetMapping("/books/top")
    public ResponseEntity<List<Book>> getTopBorrowedBooks() {
        return ResponseEntity.ok(libraryService.getTopBorrowedBooks());
    }
}

