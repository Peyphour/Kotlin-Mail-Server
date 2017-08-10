package fr.bnancy.mail.web

import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.service.UserService
import fr.bnancy.mail.smtp_server.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HomeController {

    @Autowired
    lateinit var smtpServer: Server

    @Autowired
    lateinit var mailRepository: MailRepository

    @Autowired
    lateinit var userService: UserService

    @RequestMapping
    fun index(model: Model, authentication: Authentication?): String {
        model.addAttribute("serverStatus", smtpServer.isRunning())
        model.addAttribute("mailAddresses", userService.getAllUsers().map { it -> it.mail })

        return "index"
    }

    @RequestMapping("/start-server", method = arrayOf(RequestMethod.POST))
    fun startSmtpServer(model: Model): String {
        if(!smtpServer.running)
            smtpServer.start()
        return "/index"
    }

    @RequestMapping("/stop-server", method = arrayOf(RequestMethod.POST))
    fun stopSmtpServer(model: Model): String {
        if(smtpServer.running)
            smtpServer.stop()
        return "/index"
    }

    @RequestMapping("/mail")
    fun getMails(model: Model): String {
        model.addAttribute("mails", mailRepository.findAll().toList())
        return "mail"
    }

    @RequestMapping("/login")
    fun login(): String {
        return "login"
    }

    @RequestMapping("/register", method = arrayOf(RequestMethod.GET))
    fun registerForm(): String {
        return "register"
    }

    @RequestMapping("/register", method = arrayOf(RequestMethod.POST))
    fun registerPost(model: Model, @RequestParam username: String, @RequestParam password: String): String {

        userService.createUser(username, password)

        return "/index"
    }
}