package fr.bnancy.mail.web.mail

import fr.bnancy.mail.data.MailSummary
import fr.bnancy.mail.repository.MailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/mails")
class WebmailController {

    @Autowired
    lateinit var mailRepository: MailRepository

    @RequestMapping
    fun getMails(model: Model, auth: Authentication): String {
        val email = (auth.principal as UserDetails).username
        val mails = mailRepository.findBy().filter { it.getRecipients().contains(email) }
                .map { it -> MailSummary(it.getId(), it.getHeaders()) }
        model.addAttribute("mails", mails)
        return "mail/index"
    }

    @RequestMapping("/mail")
    fun getMail(model: Model, auth: Authentication, @RequestParam mailId: Long): String {
        val mail = mailRepository.findOne(mailId)
        val userEmail = (auth.principal as UserDetails).username
        if(mail == null ||  !mail.recipients.contains(userEmail)) {
            return "redirect:/mails"
        }
        model.addAttribute("mail", mail)
        return "mail/mail"
    }
}