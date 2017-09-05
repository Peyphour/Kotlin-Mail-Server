package fr.bnancy.mail.servers.smtp.listeners

import fr.bnancy.mail.servers.smtp.data.Session

interface SessionListener {
    fun sessionOpened(session: Session)
    fun sessionClosed(session: Session)
    fun deliverMail(session: Session)
    fun acceptRecipient(recipientAddress: String): Boolean
    fun  acceptSender(address: String): Boolean
}
