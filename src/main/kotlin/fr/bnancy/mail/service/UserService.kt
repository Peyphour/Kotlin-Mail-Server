package fr.bnancy.mail.service

import fr.bnancy.mail.data.User
import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    fun createUser(username: String, password: String, role: UserAuthority) {
        val encoder = BCryptPasswordEncoder()
        val user = User(0, username, encoder.encode(password), mutableSetOf(role), true)
        userRepository.save(user)
    }

    fun getAllUsers(): MutableIterable<User> {
        return userRepository.findAll()
    }
}