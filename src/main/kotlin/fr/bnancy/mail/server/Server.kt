package fr.bnancy.mail.server

import fr.bnancy.mail.server.listeners.SessionListener
import java.io.InputStream
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket


class Server(val port: Int) : SessionListener {

    override fun sessionOpened(session: Session) {
        println(session)
    }

    override fun sessionClosed(session: Session) {
        println(session)
    }

    var socketServer: ServerSocket = ServerSocket(this.port)
    var running: Boolean = false
    val clients: ArrayList<ClientRunnable> = ArrayList()

    fun run() {
        this.running = true
        Thread({
            while(running) {
                val client: Socket = this.socketServer.accept()
                clients.add(ClientRunnable(client, this))
                Thread(clients[clients.size - 1]).start()
            }
        }).start()
    }

    fun stop() {
        running = false
        clients.forEach { it.stop() }
    }

    class ClientRunnable(val clientSocket: Socket, val listener: SessionListener): Runnable {

        var running: Boolean = true
        val session: Session = Session()

        override fun run() {
            val stream: InputStream = this.clientSocket.getInputStream()
            val out: PrintWriter = PrintWriter(this.clientSocket.getOutputStream(), true)
            var timeout: Long = System.currentTimeMillis()
            session.netAddress = this.clientSocket.inetAddress.hostAddress
            listener.sessionOpened(session)
            out.println("220")
            while(running && (System.currentTimeMillis() - timeout < 10000)) {
                if (stream.available() > 0) {
                    val buffer: ByteArray = ByteArray(stream.available())
                    stream.read(buffer)
                    timeout = System.currentTimeMillis()
                    val response = handleCommand(buffer, session)
                    out.println(response)
                    running = !session.end
                }
            }
            clientSocket.close()
            listener.sessionClosed(session)
        }

        private fun handleCommand(buffer: ByteArray, session: Session): String {
            val data: String = String(buffer)

            val mailRegex: Regex = Regex("<(.*)>")

            if(data.startsWith("HELO") || data.startsWith("EHLO"))
                return "250"
            else if(data.startsWith("MAIL FROM")) {
                session.from = mailRegex.find(data)!!.groupValues[0]
                return "250"
            } else if (data.startsWith("RCPT TO")) {
                session.to.add(mailRegex.find(data)!!.groupValues[0])
                return "250"
            } else if (data.startsWith("DATA") && !session.expectData) {
                session.expectData = true
                return "354"
            } else if(session.expectData) {
                session.content = data

                if(data.endsWith("\r\n.\r\n")) {
                    session.expectData = false
                    session.end = true
                    session.content.dropLast(1)
                }
            }
            return ""
        }

        fun stop() {
            this.running = false
        }
    }
}

