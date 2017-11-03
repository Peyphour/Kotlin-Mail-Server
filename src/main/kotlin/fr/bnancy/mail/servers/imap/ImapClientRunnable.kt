package fr.bnancy.mail.servers.imap

import fr.bnancy.mail.servers.smtp.io.CRLFTerminatedReader
import java.io.PrintWriter
import javax.net.ssl.SSLSocket

class ImapClientRunnable(private var clientSocket: SSLSocket) : Runnable {

    private var running: Boolean = true

    override fun run() {

        val reader = CRLFTerminatedReader(this.clientSocket.inputStream)
        val out = PrintWriter(this.clientSocket.outputStream, true)

        while(running) {
            val line = reader.readLine()
            out.println(line)
        }
    }

    fun stop() {
        this.running = false
    }
}