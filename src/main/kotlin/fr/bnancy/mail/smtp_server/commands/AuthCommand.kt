package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.LoginState
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import java.nio.charset.Charset
import java.util.*

@Command("AUTH")
class AuthCommand : AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        if(!session.secured)
            return SmtpResponseCode.BAD_SEQUENCE("Must issue STARTTLS first")

        if(!session.state.contains(SessionState.HELO))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue HELO/EHLO first.")

        if(!session.loginState.contains(LoginState.LOGIN_IN_PROGRESS))
            session.loginState.add(LoginState.LOGIN_IN_PROGRESS)

        if(!session.loginState.contains(LoginState.USERNAME_TRANSMITTED)
                && !session.loginState.contains(LoginState.PASSWORD_TRANSMITTED)
                && session.loginUsername.isEmpty()) {
            session.loginState.add(LoginState.USERNAME_TRANSMITTED)
            return SmtpResponseCode.AUTH_GO_ON("VXNlcm5hbWU6")
        } else if(session.loginState.contains(LoginState.USERNAME_TRANSMITTED)
                && !session.loginState.contains(LoginState.PASSWORD_TRANSMITTED)
                && session.loginUsername.isEmpty()) {
            session.loginUsername = Base64.getDecoder().decode(data.dropLast(2)).toString(Charset.forName("UTF-8"))
            session.loginState.add(LoginState.PASSWORD_TRANSMITTED)
            return SmtpResponseCode.AUTH_GO_ON("UGFzc3dvcmQ6")
        } else if (session.loginState.contains(LoginState.USERNAME_TRANSMITTED) && session.loginState.contains(LoginState.PASSWORD_TRANSMITTED)) {
            val password = Base64.getDecoder().decode(data.dropLast(2)).toString(Charset.forName("UTF-8"))
            session.loginState.clear()
            return if(listener.isValidUser(session, password))
                SmtpResponseCode.AUTH_OK("Welcome back ${session.loginUsername}")
            else
                SmtpResponseCode.INVALID("Invalid user/password")
        } else {
            return SmtpResponseCode.BAD_SEQUENCE
        }
    }
}