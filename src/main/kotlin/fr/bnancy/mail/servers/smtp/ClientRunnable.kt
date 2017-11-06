package fr.bnancy.mail.servers.smtp

import fr.bnancy.mail.getHostname
import fr.bnancy.mail.servers.smtp.commands.SmtpAbstractCommand
import fr.bnancy.mail.servers.smtp.data.LoginState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.io.CRLFTerminatedReader
import fr.bnancy.mail.servers.smtp.listeners.SessionListener
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class ClientRunnable(private var clientSocket: Socket, val listener: SessionListener, private val sessionTimeout: Int, val commands: MutableMap<String, SmtpAbstractCommand>): Runnable {

    private var running: Boolean = true
    val smtpSession: SmtpSession = SmtpSession()

    private val lineSeparator = "\r\n"

    override fun run() {
        var reader = CRLFTerminatedReader(this.clientSocket.inputStream)
        var out = PrintWriter(this.clientSocket.outputStream, true)
        var timeout: Long = System.currentTimeMillis()

        smtpSession.netAddress = this.clientSocket.inetAddress.hostAddress
        listener.sessionOpened(smtpSession)

        if(clientSocket is SSLSocket) {
            smtpSession.secured = true
        }

        write(out, SmtpResponseCode.HELO("${getHostname()} ESMTP Ready").code)

        while(running && (System.currentTimeMillis() - timeout < sessionTimeout)) {

            val line = reader.readLine()

            timeout = System.currentTimeMillis()

            if(line == null) { // Received EOF
                break
            }
            println("RCV : $line")

            val response = handleCommand(line, smtpSession)

            if(response != SmtpResponseCode.EMPTY) {
                write(out, response.code)
                println("SND : ${response.code}")
            }

            if(smtpSession.stateSmtp.contains(SmtpSessionState.TLS_STARTED) && (clientSocket !is SSLSocket)) { // Start TLS negotiation
                resetSession()

                val sslSocket = createTlsSocket()

                reader = CRLFTerminatedReader(sslSocket.inputStream)
                out = PrintWriter(sslSocket.outputStream, true)

                clientSocket = sslSocket

                smtpSession.secured = true
            }

            running = !smtpSession.stateSmtp.contains(SmtpSessionState.QUIT)

            if (smtpSession.stateSmtp.contains(SmtpSessionState.DATA) && !smtpSession.delivered) {
                listener.deliverMail(smtpSession)
                smtpSession.delivered = true
                resetSession()
            }

        }

        clientSocket.close()
        listener.sessionClosed(smtpSession)
    }

    private fun write(out: PrintWriter, s: String) {
        out.write(s)
        out.write(lineSeparator)
        out.flush()
    }

    private fun resetSession() {
        smtpSession.stateSmtp = ArrayList()
        smtpSession.to = ArrayList()
        smtpSession.from = ""
        smtpSession.content = ""
        smtpSession.receivingData = false
        smtpSession.delivered = false
    }

    private fun createTlsSocket(): SSLSocket {
        val sf = SSLSocketFactory.getDefault() as SSLSocketFactory
        val remoteAddress = clientSocket.remoteSocketAddress as InetSocketAddress
        val socket = sf.createSocket(clientSocket, remoteAddress.hostName, clientSocket.port, true) as SSLSocket

        socket.useClientMode = false

        socket.enabledCipherSuites = socket.supportedCipherSuites

        socket.addHandshakeCompletedListener {
            println("handshake complete")
        }

        socket.startHandshake()

        return socket
    }

    private fun handleCommand(data: String, smtpSession: SmtpSession): SmtpResponseCode {

        val commandString = data.takeWhile { it.isLetter() }.toUpperCase()

        val command: SmtpAbstractCommand? = commands[commandString]
        return when {
            smtpSession.receivingData -> commands["DATA"]!!.execute(data, smtpSession, listener)
            smtpSession.loginState.contains(LoginState.LOGIN_IN_PROGRESS) -> commands["AUTH"]!!.execute(data, smtpSession, listener)
            command != null -> command
                    .execute(data, smtpSession, listener)
            else -> SmtpResponseCode.UNKNOWN_COMMAND("Unknown command : $commandString")
        }
    }

    fun stop() {
        this.running = false
    }
}