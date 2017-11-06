package fr.bnancy.mail.servers.smtp.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SmtpCommand(val command: String, val scope: Array<String> = arrayOf("smtp", "submission"))