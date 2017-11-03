package fr.bnancy.mail.web.admin

import fr.bnancy.mail.data.UserAuthority
import fr.bnancy.mail.servers.imap.ImapServer
import fr.bnancy.mail.servers.smtp.SmtpServer
import fr.bnancy.mail.servers.smtp.SubmissionServer
import fr.bnancy.mail.service.UserService
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
    lateinit var submissionServer: SubmissionServer

    @Autowired
    lateinit var imapServer: ImapServer

    @Autowired
    lateinit var userService: UserService

    @RequestMapping
    fun index(model: Model): String {
        model.addAttribute("serversStatus", mapOf(
                "smtp" to smtpServer.isRunning(),
                "submission" to submissionServer.isRunning(),
                "imap" to imapServer.isRunning()
        ))
        model.addAttribute("users", userService.getAllUsers())
        model.addAttribute("roles", UserAuthority.values())
        return "admin/index"
    }

    @RequestMapping("/start-server", method = arrayOf(RequestMethod.POST))
    fun startServer(@RequestParam("server") server: String): String {
        when(server) {
            "smtp" -> {
                if(!smtpServer.running)
                    smtpServer.start()
            }
            "submission" -> {
                if(!submissionServer.running)
                    submissionServer.start()
            }
            "imap" -> {
                if(!imapServer.running)
                    imapServer.start()
            }
        }
        return "redirect:/admin"
    }

    @RequestMapping("/stop-server", method = arrayOf(RequestMethod.POST))
    fun stopServer(@RequestParam("server") server: String): String {
        when(server) {
            "smtp" -> {
                if(smtpServer.running)
                    smtpServer.stop()
            }
            "submission" -> {
                if(submissionServer.running)
                    submissionServer.stop()
            }
            "imap" -> {
                if(imapServer.running)
                    imapServer.stop()
            }
        }
        return "redirect:/admin"
    }

    @RequestMapping("/account", method = arrayOf(RequestMethod.POST))
    fun createAccount(@RequestParam("mail") mail: String, @RequestParam("pass") pass: String) {

    }
}