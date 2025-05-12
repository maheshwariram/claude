
// com/library/system/controller/UserController.kt
package com.library.system.web

import com.library.system.model.User
import com.library.system.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun registerUser(@RequestBody user: User): ResponseEntity<User> {
        val newUser = userService.registerUser(user)
        return ResponseEntity(newUser, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user: User): ResponseEntity<User> {
        val updatedUser = userService.updateUser(id, user)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }
}