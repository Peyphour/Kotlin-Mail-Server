package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.Command
import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.data.SessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@Command("EHLO")
class EhloCommand: AbstractCommand {

    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        val split = data.split(" ")

        if(split.size != 2)
            return SmtpResponseCode.ARGUMENT_ERROR("Missing hostname")

        session.senderHostname = split[1].takeWhile { it.isLetterOrDigit() }

        session.state.add(SessionState.HELO)

        return SmtpResponseCode.EHLO
    }

}