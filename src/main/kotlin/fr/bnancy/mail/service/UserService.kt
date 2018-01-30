package fr.bnancy.mail.service

import fr.bnancy.mail.data.User
import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.security.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    fun createUser(username: String, password: String, role: UserAuthority) {

        if (userRepository.findByMail(username) != null)
            return

        val encoder = BCryptPasswordEncoder()
        val user = User(0, username, encoder.encode(password), mutableSetOf(role), true)
        userRepository.save(user)
    }

    fun getAllUsers(): MutableIterable<User> {
        return userRepository.findAll()
    }

    fun isValidUser(username: String, password: String): Boolean {
        val user = userRepository.findByMail(username)
        return user != null && BCryptPasswordEncoder().matches(password, user.password)
    }

    fun deleteUser(email: String) {
        if ((SecurityContextHolder.getContext().authentication.principal as UserDetailsServiceImpl.UserDetailsImpl).username == email) // Do not delete current logged in account
            return
        userRepository.delete(userRepository.findByMail(email))
    }
}