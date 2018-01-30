package fr.bnancy.mail.security

import fr.bnancy.mail.data.User
import fr.bnancy.mail.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username != null) {
            val user = userRepository.findByMail(username)
            if (user != null) {
                return UserDetailsImpl(user)
            }
        }
        return UserDetailsImpl(User(0, ", ", "", mutableSetOf(), false))
    }

    class UserDetailsImpl(private val user: User) : UserDetails {
        override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
            return user.authorities.map { GrantedAuthority { it.name } }.toMutableList()
        }

        override fun isEnabled(): Boolean {
            return user.enabled
        }

        override fun getUsername(): String {
            return user.mail
        }

        override fun isCredentialsNonExpired(): Boolean {
            return true
        }

        override fun getPassword(): String {
            return user.password
        }

        override fun isAccountNonExpired(): Boolean {
            return true
        }

        override fun isAccountNonLocked(): Boolean {
            return true
        }

    }
}