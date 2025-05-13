package com.library.system.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "borrowing_records")
data class BorrowingRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "book_id", nullable = false)
    val bookId: UUID,
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    @Column(name = "borrow_date", nullable = false)
    val borrowDate: LocalDate,
    @Column(name = "due_date", nullable = false)
    val dueDate: LocalDate,
    @Column(name = "return_date")
    val returnDate: LocalDate? = null,
    @Column(name = "late_fee")
    val lateFee: Double = 0.0,
)
