package fr.bnancy.mail.web

import fr.bnancy.mail.servers.smtp.SmtpServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController {

    @Autowired
    lateinit var smtpServer: SmtpServer

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("serverStatus", smtpServer.isRunning())
        return "index"
    }
}