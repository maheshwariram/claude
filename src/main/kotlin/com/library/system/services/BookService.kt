package com.library.system.services

import com.library.system.model.*
import com.library.system.repository.BookRepository
import com.library.system.repository.BorrowingRecordRepository
import com.library.system.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val borrowingRecordRepository: BorrowingRecordRepository
) {
    // Constants
    private val BORROWING_PERIOD_WEEKS = 2L
    private val LATE_FEE_PER_DAY = 0.5
    private val BORROWING_LIMIT = 5L

    fun addBook(book: Book): Book {
        return bookRepository.save(book)
    }

    fun getBookById(id: UUID): Book {
        return bookRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Book with ID $id not found") }
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    fun updateBook(id: UUID, bookDetails: Book): Book {
        val existingBook = getBookById(id)

        // Create a new book object with updated fields but keep the original ID
        val bookToUpdate = bookDetails.copy(id = existingBook.id)

        return bookRepository.save(bookToUpdate)
    }

    fun deleteBookById(id: UUID) {
        if (!bookRepository.existsById(id)) {
            throw ResourceNotFoundException("Book with ID $id not found for deletion")
        }
        bookRepository.deleteById(id)
    }

    fun searchBooks(title: String?, author: String?, available: Boolean?): List<Book> {
        return when {
            // All three parameters provided
            title != null && author != null && available != null ->
                bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(title, author, available)

            // Two parameters provided
            title != null && author != null ->
                bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author)
            title != null && available != null ->
                bookRepository.findByTitleContainingIgnoreCaseAndAvailable(title, available)
            author != null && available != null ->
                bookRepository.findByAuthorContainingIgnoreCaseAndAvailable(author, available)

            // One parameter provided
            title != null -> bookRepository.findByTitleContainingIgnoreCase(title)
            author != null -> bookRepository.findByAuthorContainingIgnoreCase(author)
            available != null -> bookRepository.findByAvailable(available)

            // No parameters provided - return all books
            else -> bookRepository.findAll()
        }
    }

    fun borrowBook(bookId: UUID, userId: UUID): Book {
        // Check if book exists
        val book = getBookById(bookId)

        // Check if book is available
        if (!book.available) {
            throw BookNotAvailableException("Book with ID $bookId is not available for borrowing.")
        }

        // Check if user exists
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User with ID $userId not found for borrowing.") }

        // Check borrowing limit
        val currentlyBorrowedCount = borrowingRecordRepository.countByUserIdAndReturnDateIsNull(userId)
        if (currentlyBorrowedCount >= BORROWING_LIMIT) {
            throw BorrowingLimitExceededException("User with ID $userId has reached the borrowing limit of $BORROWING_LIMIT books.")
        }

        // Calculate due date
        val borrowDate = LocalDate.now()
        val dueDate = borrowDate.plusWeeks(BORROWING_PERIOD_WEEKS)

        // Update book status
        val updatedBook = book.copy(
            available = false,
            borrowedByUserId = userId,
            dueDate = dueDate
        )
        bookRepository.save(updatedBook)

        // Create borrowing record
        val borrowingRecord = BorrowingRecord(
            bookId = bookId,
            userId = userId,
            borrowDate = borrowDate,
            dueDate = dueDate,
            returnDate = null,
            lateFee = 0.0
        )
        borrowingRecordRepository.save(borrowingRecord)

        return updatedBook
    }

    fun returnBook(bookId: UUID): Book {
        // Check if book exists
        val book = getBookById(bookId)

        // Find active borrowing record
        val borrowingRecord = borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)
            .orElseThrow { BookAlreadyReturnedException("Book with ID $bookId is already available or no active borrowing record found.") }

        // Calculate late fee if applicable
        val returnDate = LocalDate.now()
        val lateFee = calculateLateFee(borrowingRecord.dueDate, returnDate)

        // Update borrowing record
        val updatedBorrowingRecord = borrowingRecord.copy(
            returnDate = returnDate,
            lateFee = lateFee
        )
        borrowingRecordRepository.save(updatedBorrowingRecord)

        // Update book status
        val updatedBook = book.copy(
            available = true,
            borrowedByUserId = null,
            dueDate = null
        )

        return bookRepository.save(updatedBook)
    }

    private fun calculateLateFee(dueDate: LocalDate, returnDate: LocalDate): Double {
        val daysLate = ChronoUnit.DAYS.between(dueDate, returnDate)
        return if (daysLate > 0) daysLate * LATE_FEE_PER_DAY else 0.0
    }
}