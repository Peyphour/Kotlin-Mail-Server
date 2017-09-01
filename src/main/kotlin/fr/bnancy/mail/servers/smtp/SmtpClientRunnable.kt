package fr.bnancy.mail.servers.smtp

import fr.bnancy.mail.servers.smtp.commands.AbstractCommand
import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.data.SessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.io.CRLFTerminatedReader
import fr.bnancy.mail.servers.smtp.listeners.SessionListener
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class SmtpClientRunnable(private var clientSocket: Socket, val listener: SessionListener, private val sessionTimeout: Int, val commands: MutableMap<String, AbstractCommand>): Runnable {

    private var running: Boolean = true
    val session: Session = Session()

    override fun run() {
        var reader = CRLFTerminatedReader(this.clientSocket.inputStream)
        var out = PrintWriter(this.clientSocket.outputStream, true)
        var timeout: Long = System.currentTimeMillis()

        session.netAddress = this.clientSocket.inetAddress.hostAddress
        listener.sessionOpened(session)

        out.println(SmtpResponseCode.HELO("mail.bnancy.ovh ESMTP Ready").code)

        while(running && (System.currentTimeMillis() - timeout < sessionTimeout)) {

            val line = reader.readLine()

            timeout = System.currentTimeMillis()

            if(line == null)
                continue
            println("RCV : $line")

            val response = handleCommand(line, session)

            if(response != SmtpResponseCode.EMPTY) {
                out.println(response.code)
                println("SND : ${response.code}")
            }

            if(session.state.contains(SessionState.TLS_STARTED) && (clientSocket !is SSLSocket)) { // Start TLS negociation
                resetSession()

                val sslSocket = createTlsSocket()

                reader = CRLFTerminatedReader(sslSocket.inputStream)
                out = PrintWriter(sslSocket.outputStream, true)

                clientSocket = sslSocket

                session.secured = true
            }

            running = !session.state.contains(SessionState.QUIT)

            if (session.state.contains(SessionState.DATA) && !session.delivered) {
                listener.deliverMail(session)
                session.delivered = true
            }

        }

        clientSocket.close()
        listener.sessionClosed(session)
    }

    private fun resetSession() {
        session.state = ArrayList()
        session.to = ArrayList()
        session.from = ""
        session.content = ""
        session.receivingData = false
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

    private fun handleCommand(data: String, session: Session): SmtpResponseCode {

        val commandString = data.takeWhile { it.isLetter() }.toUpperCase()

        val command: AbstractCommand? = commands[commandString]
        return when {
            session.receivingData -> commands["DATA"]!!.execute(data, session, listener)
            command != null -> command
                    .execute(data, session, listener)
            else -> SmtpResponseCode.UNKNOWN_COMMAND("Unknown command : $commandString")
        }
    }

    fun stop() {
        this.running = false
    }
}