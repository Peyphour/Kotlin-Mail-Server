package fr.bnancy.mail.servers.imap

import fr.bnancy.mail.config.ImapServerConfig
import fr.bnancy.mail.servers.AbstractServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.ServerSocket
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocket

@Component
class ImapServer : AbstractServer {

    @Autowired
    lateinit var configImap: ImapServerConfig

    lateinit var socketServer: ServerSocket

    var running: Boolean = false
    val clients: ArrayList<ImapClientRunnable> = ArrayList()

    override fun start() {
        this.running = true
        this.socketServer = SSLServerSocketFactory
                .getDefault()
                .createServerSocket(this.configImap.port)
        Thread({
            while(running) {
                val client = this.socketServer.accept() as SSLSocket
                client.enabledCipherSuites = client.supportedCipherSuites
                clients.add(ImapClientRunnable(client))
                Thread(clients[clients.size - 1]).start()
            }
        }, "imap-server").start()
        println("Starting IMAP server on port ${configImap.port}")
    }

    override fun stop() {
        this.running = false
    }
}