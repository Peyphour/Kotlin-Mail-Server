package fr.bnancy.mail.web.mail

import fr.bnancy.mail.repository.MailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/mail")
class WebmailController {

    @Autowired
    lateinit var mailRepository: MailRepository

    @RequestMapping
    fun getMails(model: Model): String {
        model.addAttribute("mails", mailRepository.findAll().toList())
        return "mail/index"
    }
}