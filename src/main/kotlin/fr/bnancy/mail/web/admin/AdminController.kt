package fr.bnancy.mail.web.admin

import fr.bnancy.mail.service.UserService
import fr.bnancy.mail.smtp_server.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@RequestMapping("/admin")
class AdminController {

    @Autowired
    lateinit var smtpServer: Server

    @Autowired
    lateinit var userService: UserService

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("serverStatus", smtpServer.isRunning())
        model.addAttribute("mailAddresses", userService.getAllUsers().map { it -> it.mail })
        return "admin/index"
    }

    @RequestMapping("/start-server", method = arrayOf(RequestMethod.POST))
    fun startSmtpServer(): String {
        if(!smtpServer.running)
            smtpServer.start()
        return "/admin"
    }

    @RequestMapping("/stop-server", method = arrayOf(RequestMethod.POST))
    fun stopSmtpServer(): String {
        if(smtpServer.running)
            smtpServer.stop()
        return "/admin"
    }
}