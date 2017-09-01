package fr.bnancy.mail.web.admin

import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.service.UserService
import fr.bnancy.mail.servers.smtp.SmtpServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController {

    @Autowired
    lateinit var smtpServer: SmtpServer

    @Autowired
    lateinit var userService: UserService

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("serverStatus", smtpServer.isRunning())
        model.addAttribute("users", userService.getAllUsers())
        model.addAttribute("roles", UserAuthority.values())
        return "admin/index"
    }

    @RequestMapping("/start-server", method = arrayOf(RequestMethod.POST))
    fun startSmtpServer(): String {
        if(!smtpServer.running)
            smtpServer.start()
        return "redirect:/admin"
    }

    @RequestMapping("/stop-server", method = arrayOf(RequestMethod.POST))
    fun stopSmtpServer(): String {
        if(smtpServer.running)
            smtpServer.stop()
        return "redirect:/admin"
    }

    @RequestMapping("/account", method = arrayOf(RequestMethod.POST))
    fun createAccount(@RequestParam("mail") mail: String, @RequestParam("pass") pass: String) {

    }
}