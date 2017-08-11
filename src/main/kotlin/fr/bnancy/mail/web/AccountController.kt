package fr.bnancy.mail.web

import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
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


    @RequestMapping("/admin/register", method = arrayOf(RequestMethod.POST))
    fun registerPost(@RequestParam mail: String, @RequestParam pass: String, @RequestParam role: UserAuthority): String {
        userService.createUser(mail, pass, role)
        return "redirect:/admin"
    }
}