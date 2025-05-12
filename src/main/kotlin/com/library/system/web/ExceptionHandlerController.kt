package com.library.system.web

import com.library.system.model.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController {

    data class ErrorResponse(val message: String)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Resource not found"), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BookNotAvailableException::class)
    fun handleBookNotAvailableException(ex: BookNotAvailableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Book not available"), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(BookAlreadyReturnedException::class)
    fun handleBookAlreadyReturnedException(ex: BookAlreadyReturnedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Book already returned"), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(BorrowingLimitExceededException::class)
    fun handleBorrowingLimitExceededException(ex: BorrowingLimitExceededException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Borrowing limit exceeded"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}