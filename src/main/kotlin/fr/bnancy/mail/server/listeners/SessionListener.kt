package fr.bnancy.mail.server.listeners

import fr.bnancy.mail.server.Session

interface SessionListener {
    fun sessionOpened(session: Session)
    fun sessionClosed(session: Session)
}