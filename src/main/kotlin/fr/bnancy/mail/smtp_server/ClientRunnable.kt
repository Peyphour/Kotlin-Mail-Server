package fr.bnancy.mail.smtp_server

import fr.bnancy.mail.smtp_server.commands.AbstractCommand
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import java.io.InputStream
import java.io.PrintWriter
import java.net.Socket

class ClientRunnable(val clientSocket: Socket, val listener: SessionListener, val sessionTimeout: Int, val commands: MutableMap<String, AbstractCommand>): Runnable {

    var running: Boolean = true
    val session: Session = Session()

    override fun run() {
        val stream: InputStream = this.clientSocket.getInputStream()
        val out: PrintWriter = PrintWriter(this.clientSocket.getOutputStream(), true)
        var timeout: Long = System.currentTimeMillis()

        session.netAddress = this.clientSocket.inetAddress.hostAddress
        listener.sessionOpened(session)

        out.println(SmtpResponseCode.HELO("smtp-bnancy SMTP Ready").code)

        while(running && (System.currentTimeMillis() - timeout < sessionTimeout)) {
            if (stream.available() > 0) {
                val buffer: ByteArray = ByteArray(stream.available())
                stream.read(buffer)
                timeout = System.currentTimeMillis()
                val response = handleCommand(buffer, session)
                out.println(response.code)
                running = !session.state.contains(SessionState.QUIT)
                if (session.state.contains(SessionState.DATA) && !session.delivered) {
                    listener.deliverMail(session)
                    session.delivered = true
                }
            }
        }

        clientSocket.close()
        listener.sessionClosed(session)
    }

    private fun handleCommand(buffer: ByteArray, session: Session): SmtpResponseCode {
        val data = String(buffer)

        val commandString = data.takeWhile { it.isLetter() }

        val command: AbstractCommand? = commands[commandString]
        if(command != null)
            return command
                    .execute(data, session, listener)
        else if(session.receivingData)
            return commands["DATA"]!!.execute(data, session, listener)
        else
            return SmtpResponseCode.UNKNOWN_COMMAND("Unknown command : $commandString")

        // I'm a state machine baby!
//        when (session.state) {
//            SessionState.START -> if(data.startsWith("HELO") || data.startsWith("EHLO")) {
//                session.state = SessionState.WAIT_FROM
//                return SmtpResponseCode.OK
//            } else {
//                return SmtpResponseCode.BAD_SEQUENCE
//            }
//            SessionState.WAIT_FROM -> if (data.startsWith("MAIL FROM")) {
//                val address = mailRegex.find(data)!!.groupValues[1]
//                if (listener.acceptSender(address)) {
//                    session.from = address
//                    session.state = SessionState.WAIT_TO
//                    return SmtpResponseCode.OK
//                } else {
//                    return SmtpResponseCode.SENDER_BLACKLIST
//                }
//            } else {
//                return SmtpResponseCode.BAD_SEQUENCE
//            }
//            SessionState.WAIT_TO -> if (data.startsWith("RCPT TO")) {
//                val address = mailRegex.find(data)!!.groupValues[1]
//                if (listener.acceptRecipient(address))
//                    session.to.add(address)
//                else {
//                    return SmtpResponseCode.MAILBOX_UNAVAILABLE
//                }
//                return SmtpResponseCode.OK
//            } else if (data.startsWith("DATA") && session.to.size > 0) {
//                session.state = SessionState.WAIT_DATA
//                return SmtpResponseCode.DATA
//            } else {
//                return SmtpResponseCode.BAD_SEQUENCE
//            }
//            SessionState.WAIT_DATA -> {
//                session.content += data
//                if(session.content.endsWith("\r\n.\r\n")) {
//                    session.content = session.content.dropLast(5) // remove CRLF.CRLF
//                    session.state = SessionState.WAIT_QUIT
//                    return SmtpResponseCode.OK
//                }
//            }
//            SessionState.WAIT_QUIT -> {
//                if (data.startsWith("QUIT")) {
//                    session.state = SessionState.END
//                    return SmtpResponseCode.QUIT
//                }
//            }
//            SessionState.END -> {
//                if(!data.isEmpty())
//                    return SmtpResponseCode.BAD_SEQUENCE
//            }
//        }
//        return SmtpResponseCode.UNKNOWN
    }

    fun stop() {
        this.running = false
    }
}