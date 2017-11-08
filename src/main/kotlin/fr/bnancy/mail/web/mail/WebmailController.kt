package fr.bnancy.mail.web.mail

import fr.bnancy.mail.data.MailSummary
import fr.bnancy.mail.repository.MailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/mails")
class WebmailController {

    @Autowired
    lateinit var mailRepository: MailRepository

    @RequestMapping
    fun getMails(model: Model, auth: Authentication): String {
        val email = (auth.principal as UserDetails).username
        val mails = mailRepository.findBy().filter { it.getRecipients().contains(email) }
                .sortedByDescending { it.getId() }
                .map { it -> MailSummary(it.getId(), it.getHeaders(), it.getSeen()) }
        model.addAttribute("mails", mails)
        return "mail/index"
    }

    @RequestMapping("/{id}")
    fun getMail(model: Model, auth: Authentication, @PathVariable id: Long): String {
        val mail = mailRepository.findOne(id)
        val userEmail = (auth.principal as UserDetails).username
        if(mail == null ||  !mail.recipients.contains(userEmail)) {
            return "redirect:/mails"
        }
        model.addAttribute("mail", mail)
        mail.seen = true
        mailRepository.save(mail)
        return "mail/mail"
    }
}