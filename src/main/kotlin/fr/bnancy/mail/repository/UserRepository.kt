package fr.bnancy.mail.repository

import fr.bnancy.mail.data.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {
    fun findByMail(mail: String): User?
}