package fr.bnancy.mail.web

import fr.bnancy.mail.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AccountController {

    @Autowired
    lateinit var userService: UserService

    @RequestMapping("/login")
    fun login(): String {
        return "account/login"
    }

    @RequestMapping("/register", method = arrayOf(RequestMethod.GET))
    fun registerForm(): String {
        return "account/register"
    }

    @RequestMapping("/register", method = arrayOf(RequestMethod.POST))
    fun registerPost(model: Model, @RequestParam username: String, @RequestParam password: String): String {

        userService.createUser(username, password)

        return "/index"
    }
}