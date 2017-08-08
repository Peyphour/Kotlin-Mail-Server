package fr.bnancy.mail.web

import fr.bnancy.mail.repository.MailRepository
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

    @Autowired
    lateinit var mailRepository: MailRepository

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("serverStatus", smtpServer.isRunning())
        return "index"
    }

    @RequestMapping("/start-server", method = arrayOf(RequestMethod.POST))
    fun startSmtpServer(model: Model): String {
        smtpServer.start()
        return index(model)
    }

    @RequestMapping("/stop-server", method = arrayOf(RequestMethod.POST))
    fun stopSmtpServer(model: Model): String {
        smtpServer.stop()
        return index(model)
    }

    @RequestMapping("/mail")
    fun getMails(model: Model): String {
        model.addAttribute("mails", mailRepository.findAll().toList())
        return "mail"
    }
}