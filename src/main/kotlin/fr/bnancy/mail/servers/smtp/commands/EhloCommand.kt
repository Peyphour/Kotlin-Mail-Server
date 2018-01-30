package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.getHostname
import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener

@SmtpCommand("EHLO")
class EhloCommand : SmtpAbstractCommand {

    override fun execute(data: String, smtpSession: SmtpSession, smtpListener: SmtpSessionListener): SmtpResponseCode {
        val split = data.split(" ")

        if (split.size != 2)
            return SmtpResponseCode.ARGUMENT_ERROR("Missing hostname")

        smtpSession.senderHostname = split[1].takeWhile { it.isLetterOrDigit() }

        smtpSession.stateSmtp.add(SmtpSessionState.HELO)

        val options = "-${getHostname()}\r\n" +
                "250-SIZE 51200000\r\n" +
                "250-ETRN\r\n" +
                "250-STARTTLS\r\n" +
                "250-8BITMIME\r\n" +
                when {
                    smtpSession.secured -> "250-AUTH LOGIN\r\n"
                    else -> ""
                } +
                "250 DSN"

        return SmtpResponseCode.EHLO(options)
    }

}