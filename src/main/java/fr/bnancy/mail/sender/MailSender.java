package fr.bnancy.mail.sender;

import static com.fasterxml.jackson.module.kotlin.ExtensionsKt.jacksonObjectMapper;

import fr.bnancy.mail.CRLFTerminatedReader;
import fr.bnancy.mail.data.Mail;
import fr.bnancy.mail.servers.smtp.data.Header;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.simplejavamail.mailer.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailSender {

  private Mail content;
  private ServerConfig config;
  private CRLFTerminatedReader reader;
  private PrintWriter writer;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public MailSender(Mail content, ServerConfig config) {
    this.content = content;
    this.config = config;
  }

  public boolean send() {
    try {
      logger.info("Connecting to {}", config);
      Socket socket = new Socket(config.getHost(), config.getPort());

      reader = new CRLFTerminatedReader(socket.getInputStream());
      writer = new PrintWriter(socket.getOutputStream());

      except("250", read(reader));

      final String helloResponse = ehlo();

      if (helloResponse.contains("STARTTLS")) {

        startTls();

        final SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        final InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        final SSLSocket newSocket = (SSLSocket) sslSocketFactory
            .createSocket(socket, remoteAddress.getHostName(), socket.getPort(), true);
        newSocket.setUseClientMode(true);
        newSocket.setEnabledCipherSuites(newSocket.getSupportedCipherSuites());
        newSocket.startHandshake();

        reader = new CRLFTerminatedReader(newSocket.getInputStream());
        writer = new PrintWriter(newSocket.getOutputStream());

        socket = newSocket;

        ehlo();
      }

      mailFrom();
      rcptTo();
      data();
      quit();

      logger.info("Closing connection to {}. Mail delivered successfully.", config);
      socket.close();

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private String read(CRLFTerminatedReader reader) throws IOException {
    StringBuilder content = new StringBuilder();
    String tmp;

    do {
      tmp = reader.readLine();
      content.append(tmp).append("\r\n");
    } while (tmp.charAt(3) == '-');
    content.deleteCharAt(content.length() - 1);
    content.deleteCharAt(content.length() - 1);

    return content.toString();
  }

  private void write(PrintWriter writer, String s) {
    writer.write(s);
    writer.write("\r\n");
    writer.flush();
  }

  private String ehlo() throws IOException {
    write(writer, "EHLO mail-server-pey");

    final String helloResponse = read(reader);
    except("250", helloResponse);

    return helloResponse;
  }

  private void startTls() throws IOException {
    write(writer, "STARTTLS");
    except("220", read(reader));
  }

  private void mailFrom() throws IOException {
    write(writer, "MAIL FROM: <" + content.getSender() + ">");
    except("250", read(reader));
  }

  private void rcptTo() throws IOException {
    write(writer, "RCPT TO: <" + content.getRecipients().get(0) + ">");
    except("250", read(reader));
  }

  private void data() throws IOException {

    write(writer, "DATA");
    except("354", read(reader));

    Header[] headers = jacksonObjectMapper().readValue(content.getHeaders(), Header[].class);
    for (Header header : headers) {
      write(writer, header.getKey() + ": " + header.getValue());
    }
    write(writer, "");
    write(writer, content.getContent());
    write(writer, ".");

    except("250", read(reader));
  }

  private void quit() throws IOException {
    write(writer, "QUIT");
    except("221", read(reader));
  }

  private void except(String code, String content) {
    if (content.indexOf(code) != 0) {
      throw new RuntimeException(
          "SMTP exception, expected code " + code + " and got response " + content);
    }
  }
}
