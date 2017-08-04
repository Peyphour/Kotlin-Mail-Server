package fr.bnancy.mail.web

import fr.bnancy.mail.smtp_server.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class HomeController {

    @Autowired
    lateinit var smtpServer: Server

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("name", "test")
        return "index"
    }

    @RequestMapping("/start-server", method = arrayOf(RequestMethod.POST))
    fun startSmtpServer(): String {
        smtpServer.start()
        return "index"
    }

    @RequestMapping("/stop-server", method = arrayOf(RequestMethod.POST))
    fun stopSmtpServer(): String {
        smtpServer.stop()
        return "index"
    }
}