package fr.bnancy.mail.smtp_server.listeners

import fr.bnancy.mail.smtp_server.data.Session

interface SessionListener {
    fun sessionOpened(session: Session)
    fun sessionClosed(session: Session)
}