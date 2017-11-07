package fr.bnancy.mail.servers.smtp;

import fr.bnancy.mail.config.SubmissionServerConfig
import fr.bnancy.mail.servers.smtp.commands.SmtpAbstractCommand
import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.Socket
import java.net.SocketException
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory

@Component
class SubmissionServer {

    @Autowired
    lateinit var configSubmission: SubmissionServerConfig

    @Autowired
    lateinit var smtpListener: SmtpSessionListener

    lateinit var sslServerSocket: SSLServerSocket

    private val logger = Logger.getLogger(javaClass.simpleName)

    var running: Boolean = false
    val clients: ArrayList<ClientRunnable> = ArrayList()

    val commands: MutableMap<String, SmtpAbstractCommand> = HashMap()

    @PostConstruct
    fun init() {
        val reflections = Reflections("fr.bnancy.mail.servers.smtp.commands")
        reflections.getTypesAnnotatedWith(SmtpCommand::class.java)
                .filter { it.getAnnotation(SmtpCommand::class.java).scope.contains("submission") }
                .forEach { commands.put(it.getAnnotation(SmtpCommand::class.java).command, it.newInstance() as SmtpAbstractCommand) }
    }

    fun start() {
        this.running = true
        this.sslServerSocket = SSLServerSocketFactory.getDefault().createServerSocket(configSubmission.port) as SSLServerSocket
        Thread({
            while(running) {
                val client: Socket = this.sslServerSocket.accept()
                clients.add(ClientRunnable(client, smtpListener, configSubmission.sessionTimeout, commands))
                Thread(clients[clients.size - 1], "client-runnable-${client.inetAddress.hostName}").start()
            }
            println("server closed")
        }, "submission-server").start()

        logger.info("Starting Submission server on port ${configSubmission.port}")
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
