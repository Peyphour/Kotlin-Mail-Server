package fr.bnancy.mail.servers.pop3

import fr.bnancy.mail.config.Pop3ServerConfig
import fr.bnancy.mail.servers.pop3.commands.Pop3AbstractCommand
import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.SocketException
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocket

@Component
class Pop3Server {

    @Autowired
    lateinit var pop3Config: Pop3ServerConfig

    lateinit var sslServerSocket: SSLServerSocket

    private val logger = Logger.getLogger(javaClass.simpleName)

    var running: Boolean = false
    val clients: ArrayList<ClientRunnable> = ArrayList()

    val commands: MutableMap<String, Pop3AbstractCommand> = HashMap()

    @PostConstruct
    fun init() {
        val reflections = Reflections("fr.bnancy.mail.servers.imap.commands")
        reflections.getTypesAnnotatedWith(Pop3Command::class.java)
                .forEach { commands.put(it.getAnnotation(Pop3Command::class.java).command, it.newInstance() as Pop3AbstractCommand) }
    }

    fun start() {
        this.running = true
        this.sslServerSocket = SSLServerSocketFactory.getDefault().createServerSocket(pop3Config.port) as SSLServerSocket
        Thread({
            while(running) {
                val client: SSLSocket = sslServerSocket.accept() as SSLSocket
                clients.add(ClientRunnable(client, pop3Config.sessionTimeout, commands))
                Thread(clients[clients.size - 1], "client-runnable-${client.inetAddress.hostName}").start()
            }
        }, "pop3-server").start()

        logger.info("Starting POP3 server on port ${pop3Config.port}")
    }

    fun stop() {
        running = false
        clients.forEach { it.stop() }
        try {
            this.sslServerSocket.close()
        } catch (e: SocketException) {
            // ignore
        }
    }

    fun isRunning(): Boolean {
        return running
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60, initialDelay = 1000 * 60 * 60) // One call per hour
    fun cleanupHangingClients() {
        logger.info("Starting to clean threads")
        if(!this.running)
            return
        this.stop()
        Thread.sleep(1000)
        this.clients.clear()
        this.start()
    }
}