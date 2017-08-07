package fr.bnancy.mail.smtp_server

import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import java.io.InputStream
import java.io.PrintWriter
import java.net.Socket


// TODO: implement flow control and mailbox validation
class ClientRunnable(val clientSocket: Socket, val listener: SessionListener, val sessionTimeout: Int): Runnable {

    var running: Boolean = true
    val session: Session = Session()

    override fun run() {
        val stream: InputStream = this.clientSocket.getInputStream()
        val out: PrintWriter = PrintWriter(this.clientSocket.getOutputStream(), true)
        var timeout: Long = System.currentTimeMillis()

        session.netAddress = this.clientSocket.inetAddress.hostAddress
        listener.sessionOpened(session)

        out.println(SmtpResponseCode.HELO.code)

        while(running && (System.currentTimeMillis() - timeout < sessionTimeout)) {
            if (stream.available() > 0) {
                val buffer: ByteArray = ByteArray(stream.available())
                stream.read(buffer)
                timeout = System.currentTimeMillis()
                val response = handleCommand(buffer, session)
                out.println(response.code)
                running = !session.end
            }
        }

        clientSocket.close()
        listener.sessionClosed(session)
    }

    private fun handleCommand(buffer: ByteArray, session: Session): SmtpResponseCode {
        val data = String(buffer)

        val mailRegex = Regex("<(.*)>")

        if(data.startsWith("HELO") || data.startsWith("EHLO"))
            return SmtpResponseCode.OK
        else if(data.startsWith("MAIL FROM")) {
            session.from = mailRegex.find(data)!!.groupValues[1]
            return SmtpResponseCode.OK
        } else if (data.startsWith("RCPT TO")) {
            session.to.add(mailRegex.find(data)!!.groupValues[1])
            return SmtpResponseCode.OK
        } else if (data.startsWith("DATA") && !session.expectData) {
            session.expectData = true
            return SmtpResponseCode.DATA
        } else if(session.expectData) {
            session.content += data
            if(session.content.endsWith("\r\n.\r\n")) {
                session.expectData = false
                session.content = session.content.dropLast(5) // remove CRLF.CRLFL
                return SmtpResponseCode.OK
            }
        } else if(data.startsWith("QUIT")) {
            session.end = true
            return SmtpResponseCode.QUIT
        }
        return SmtpResponseCode.UNKNOWN
    }

    fun stop() {
        this.running = false
    }
}