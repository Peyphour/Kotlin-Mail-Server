package fr.bnancy.mail.web.mail

import fr.bnancy.mail.repository.MailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/mail")
class WebmailController {

    @Autowired
    lateinit var mailRepository: MailRepository

    @RequestMapping
    fun getMails(model: Model, auth: Authentication): String {
        val email = (auth.principal as UserDetails).username
        val mails = mailRepository.findAll().filter { it.recipients.contains(email) }
        model.addAttribute("mails", mails)
        return "mail/index"
    }
}