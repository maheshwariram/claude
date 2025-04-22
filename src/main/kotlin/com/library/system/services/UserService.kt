package com.library.system.services

import com.library.system.model.ResourceNotFoundException
import com.library.system.model.User
import com.library.system.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository) {

    fun registerUser(user: User): User {
        return userRepository.save(user)
    }

    fun getUserById(id: UUID): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with ID $id not found") }
    }

    fun updateUser(id: UUID, userDetails: User): User {
        val existingUser = getUserById(id)

        // Create a new user object with updated fields but keep the original ID
        val userToUpdate = userDetails.copy(id = existingUser.id)

        return userRepository.save(userToUpdate)
    }
}