package fr.bnancy.mail.web.admin

import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.getRSAKeyPair
import fr.bnancy.mail.servers.pop3.Pop3Server
import fr.bnancy.mail.servers.smtp.SmtpServer
import fr.bnancy.mail.servers.smtp.SubmissionServer
import fr.bnancy.mail.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
@RequestMapping("/admin")
class AdminController {

    @Autowired
    lateinit var smtpServer: SmtpServer

    @Autowired
    lateinit var submissionServer: SubmissionServer

    @Autowired
    lateinit var pop3Server: Pop3Server

    @Autowired
    lateinit var userService: UserService

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("serversStatus", mapOf(
                "smtp" to smtpServer.isRunning(),
                "submission" to submissionServer.isRunning(),
                "pop3" to pop3Server.isRunning()
        ))
        model.addAttribute("serverNumber", 3)
        model.addAttribute("users", userService.getAllUsers())
        model.addAttribute("roles", UserAuthority.values())
        model.addAttribute("dkimPublicKey", Base64.getEncoder().encodeToString(getRSAKeyPair()?.public?.encoded ?: "".toByteArray()))
        return "admin/index"
    }

    @RequestMapping("/start-server", method = [(RequestMethod.POST)])
    fun startSmtpServer(@RequestParam("server") server: String): String {
        when(server) {
            "smtp" -> {
                if(!smtpServer.running)
                    smtpServer.start()
            }
            "submission" -> {
                if(!submissionServer.running)
                    submissionServer.start()
            }
            "pop3" -> {
                if(!pop3Server.running)
                    pop3Server.start()
            }
        }
        return "redirect:/admin"
    }

    @RequestMapping("/stop-server", method = [(RequestMethod.POST)])
    fun stopSmtpServer(@RequestParam("server") server: String): String {
        when(server) {
            "smtp" -> {
                if(smtpServer.running)
                    smtpServer.stop()
            }
            "submission" -> {
                if(submissionServer.running)
                    submissionServer.stop()
            }
            "pop3" -> {
                if(pop3Server.running)
                    pop3Server.stop()
            }
        }
        return "redirect:/admin"
    }
}