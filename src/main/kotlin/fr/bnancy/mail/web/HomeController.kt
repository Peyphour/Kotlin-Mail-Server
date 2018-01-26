package fr.bnancy.mail.web

import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/")
class HomeController {

    @Autowired
    lateinit var userService: UserService

    @RequestMapping
    fun index(model: Model): String {
        if(userService.getAllUsers().count() == 0)
            return "setup"
        return "index"
    }

    @PostMapping("/setup")
    fun setup(@RequestParam mail: String, @RequestParam password: String): String {

        if(userService.getAllUsers().count() != 0) // do not create account if an account already exists
            return "redirect:/"

        userService.createUser(mail, password, UserAuthority.ROLE_ADMIN)
        return "redirect:/"
    }
}