package fr.bnancy.mail.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.logging.Logger

@Service
class IpBlacklistService {

    private val cache = mutableMapOf<String, String>()
    private val bl = arrayOf("sbl.spamhaus.org", "cbl.abuseat.org")
    private val logger = Logger.getLogger(javaClass.simpleName)

    @Scheduled(fixedRate = 1000 * 60 * 60) // Empty cache each hour
    private fun emptyCache() {
        cache.clear()
    }

    fun blacklistedIp(ip: String): Boolean {

        if(cache[ip] != null) {
            logger.info("$ip is listed by ${cache[ip]}, closing session")
            return true
        }

        for(host in bl) {
            val reversedIp = ip.split(".").reversed().joinToString(".")
            try {
                InetAddress.getByName(reversedIp + "." + host)
                cache.put(ip, host)
                logger.info("$ip is listed by ${cache[ip]}, closing session")
                return true
            } catch(e: UnknownHostException) {
            }
        }
        return false
    }
}