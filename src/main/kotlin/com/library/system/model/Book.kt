package com.library.system.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val author: String,
    @Column(nullable = false)
    val available: Boolean = true,
    @Column(name = "borrowed_by_user_id")
    val borrowedByUserId: UUID? = null,
    @Column(name = "due_date")
    val dueDate: LocalDate? = null,
)
