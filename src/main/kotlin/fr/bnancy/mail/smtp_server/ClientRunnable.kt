package fr.bnancy.mail.smtp_server

import fr.bnancy.mail.smtp_server.commands.AbstractCommand
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import java.io.InputStream
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory



class ClientRunnable(var clientSocket: Socket, val listener: SessionListener, val sessionTimeout: Int, val commands: MutableMap<String, AbstractCommand>): Runnable {

    var running: Boolean = true
    val session: Session = Session()

    override fun run() {
        var stream: InputStream = this.clientSocket.getInputStream()
        var out: PrintWriter = PrintWriter(this.clientSocket.getOutputStream(), true)
        var timeout: Long = System.currentTimeMillis()

        session.netAddress = this.clientSocket.inetAddress.hostAddress
        listener.sessionOpened(session)

        out.println(SmtpResponseCode.HELO("mail.bnancy.ovh ESMTP Ready").code)

        while(running && (System.currentTimeMillis() - timeout < sessionTimeout)) {
            if (stream.available() > 0) {
                val buffer: ByteArray = ByteArray(stream.available())
                stream.read(buffer)

                println("RCV : ${String(buffer)}")

                timeout = System.currentTimeMillis()

                val response = handleCommand(buffer, session)
                out.println(response.code)

                println("SND : ${response.code}")

                if(session.state.contains(SessionState.TLS_STARTED) && (clientSocket !is SSLSocket)) { // Start TLS negociation
                    resetSession()

                    println("creating TLS socket")
                    val sslSocket = createTlsSocket()
                    // Wait for handshake
                    while(!session.state.contains(SessionState.TLS_STARTED)) {}

                    println("get streams")
                    stream = sslSocket.inputStream
                    out = PrintWriter(sslSocket.getOutputStream(), true)
                    println("end of TLS part")
                    out.println(SmtpResponseCode.HELO("mail.bnancy.ovh ESMTP Ready").code)
                    clientSocket = sslSocket
                }

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

        socket.addHandshakeCompletedListener {
            println("handshake complete")
            session.state.add(SessionState.TLS_STARTED)
        }

        socket.startHandshake()

        return socket
    }

    private fun handleCommand(buffer: ByteArray, session: Session): SmtpResponseCode {
        val data = String(buffer)

        val commandString = data.takeWhile { it.isLetter() }.toUpperCase()

        val command: AbstractCommand? = commands[commandString]
        if(command != null)
            return command
                    .execute(data, session, listener)
        else if(session.receivingData)
            return commands["DATA"]!!.execute(data, session, listener)
        else
            return SmtpResponseCode.UNKNOWN_COMMAND("Unknown command : $commandString")
    }

    fun stop() {
        this.running = false
    }
}