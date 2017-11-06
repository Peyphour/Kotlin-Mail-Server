package fr.bnancy.mail.servers.smtp.listeners

import fr.bnancy.mail.servers.smtp.data.SmtpSession

interface SessionListener {
    fun sessionOpened(smtpSession: SmtpSession)
    fun sessionClosed(smtpSession: SmtpSession)
    fun deliverMail(smtpSession: SmtpSession)
    fun acceptRecipient(recipientAddress: String, smtpSession: SmtpSession): Boolean
    fun acceptSender(address: String): Boolean
    fun isValidUser(smtpSession: SmtpSession, password: String): Boolean
}
