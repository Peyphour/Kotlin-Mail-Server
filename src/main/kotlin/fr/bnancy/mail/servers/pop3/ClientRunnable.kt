package fr.bnancy.mail.servers.pop3

import fr.bnancy.mail.servers.pop3.commands.Pop3AbstractCommand
import fr.bnancy.mail.servers.smtp.io.CRLFTerminatedReader
import java.io.PrintWriter
import javax.net.ssl.SSLSocket

class ClientRunnable(private val clientSocket: SSLSocket, private val sessionTimeout: Int, val commands: MutableMap<String, Pop3AbstractCommand>) : Runnable {

    private var running: Boolean = true
    private val lineSeparator = "\r\n"


    override fun run() {

        var reader = CRLFTerminatedReader(this.clientSocket.inputStream)
        var out = PrintWriter(this.clientSocket.outputStream, true)

        write(out, "+OK POP3 ready")

        while(running) {
            val line = reader.readLine() ?: // Received EOF
                    break

            write(out, line)
        }
    }

    private fun write(out: PrintWriter, s: String) {
        out.write(s)
        out.write(lineSeparator)
        out.flush()
    }

    fun stop() {
        this.running = false
    }
}