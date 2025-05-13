// com/library/system/controller/BookController.kt
package com.library.system.web

import com.library.system.model.Book
import com.library.system.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping
    fun addBook(
        @RequestBody book: Book,
    ): ResponseEntity<Book> {
        val newBook = bookService.addBook(book)
        return ResponseEntity(newBook, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getBookById(
        @PathVariable id: UUID,
    ): ResponseEntity<Book> {
        val book = bookService.getBookById(id)
        return ResponseEntity(book, HttpStatus.OK)
    }

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<Book>> {
        val books = bookService.getAllBooks()
        return ResponseEntity(books, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: UUID,
        @RequestBody book: Book,
    ): ResponseEntity<Book> {
        val updatedBook = bookService.updateBook(id, book)
        return ResponseEntity(updatedBook, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        bookService.deleteBookById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) available: Boolean?,
    ): ResponseEntity<List<Book>> {
        val books = bookService.searchBooks(title, author, available)
        return ResponseEntity(books, HttpStatus.OK)
    }

    @PostMapping("/{bookId}/borrow")
    fun borrowBook(
        @PathVariable bookId: UUID,
        @RequestParam userId: UUID,
    ): ResponseEntity<Book> {
        val borrowedBook = bookService.borrowBook(bookId, userId)
        return ResponseEntity(borrowedBook, HttpStatus.OK)
    }

    @PostMapping("/{bookId}/return")
    fun returnBook(
        @PathVariable bookId: UUID,
    ): ResponseEntity<Book> {
        val returnedBook = bookService.returnBook(bookId)
        return ResponseEntity(returnedBook, HttpStatus.OK)
    }
}
