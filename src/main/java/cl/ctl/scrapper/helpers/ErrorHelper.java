package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.model.Log;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author Diego Soto
 */
public class ErrorHelper {

    private static final ErrorHelper instance = new ErrorHelper();

    private static Session session;

    private static Properties prop;

    //private static final String from = "sistemas@minsal.cl";//"semantikos.minsal@gmail.com";

    private static final String from = ConfigHelper.getInstance().CONFIG.get("mail.from.user"); //"diego.abelardo.soto@gmail.com";//"semantikos.minsal@gmail.com";

    //private static final String to = "diego.abelardo.soto@gmail.com";

    //private static final String to = "cristian.fiedler@fiedler-bi.com";//"semantikos.minsal@gmail.com";

    private static final String to = ConfigHelper.getInstance().CONFIG.get("error.to");//"diego.abelardo.soto@gmail.com";//"semantikos.minsal@gmail.com";

    private static final String username = ConfigHelper.getInstance().CONFIG.get("mail.from.user"); //"diego.abelardo.soto@gmail.com";

    private static final String password = ConfigHelper.getInstance().CONFIG.get("mail.from.password");

    private static final String subject = "CTL - Descarga Scraps";

    //private static String body = "<b>Bienvenido a Semantikos</b><br><br>Una cuenta asociada a este correo ha sido creada. <ul><li>Para activar su cuenta, por favor pinche el siguiente link: <br>%link%</li><li>Su contraseña inicial es: %password%</li><li>Cambie su contraseña inicial</li><li>Configure sus preguntas de seguridad</li></ul>El Equipo Semantikos";

    private static String body; //= "<table style='border-collapse:collapse;table-layout:fixed;min-width:320px;width:100%;background-color:#f5f7fa;' cellpadding='0' cellspacing='0'><tr><td><table style='background:white;border-collapse:collapse;table-layout:fixed;max-width:600px;min-width:320px;width:100%;font-family:Impact, Charcoal, sans-serif;color:#283593' align='center' cellpadding='0' cellspacing='0'><tr><td align='center' style='width:100%' colspan='3'><img src=\"cid:image2\" style='width:230px'></td></tr><tr><td colspan=3 style='padding-left:2em;padding-right:2em'><hr style='padding: 2px; background: #283593;' /><br/><br/>Estimado(a) %username%, una cuenta asociada a este correo ha sido creada en el Sistema.</p><ul><li>Su contraseña inicial es: %password%</li><li>Cambie su contraseña inicial</li><li>Configure sus preguntas de seguridad</li></ul><p>El equipo Semantikos</p><br/></td></tr><tr><td style='width:30%'></td><td style='background: #283593;width:40%;text-align:center;font-family:arial;font-size:13px;border-radius: 15px;-moz-border-radius: 15px;' align='center' height=31><a style='color: white;text-decoration:none;text-align:center' href='%link%'><strong>Activar Cuenta</strong></a></td><td></td></tr><tr><td colspan='3' align='center'><br/><div>©2016 Ministerio de Salud</div></td></tr></table></td></tr></table>";

    BufferedReader reader;

    private static final Logger logger = LoggerFactory.getLogger(ErrorHelper.class);

    private static boolean flag = false;

    public ErrorHelper() {

        prop = System.getProperties();

        //Get the session object
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public static ErrorHelper getInstance() {
        return instance;
    }

    public void sendMail() throws Exception {

        this.body = "";

        if(flag) {
           return;
        }

        flag = true;

        loadMailBody();

        List<FileControl> fileControlList = LogHelper.getInstance().getFileControlList();

        String weekProcessDay = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String processMonthDay = String.valueOf(ProcessHelper.getInstance().getProcessDate().getDayOfMonth());
        String processMonth = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String processYear = String.valueOf(ProcessHelper.getInstance().getProcessDate().getYear());

        String weekExecutionDay = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String executionMonthDay = String.valueOf(LocalDate.now().getDayOfMonth());
        String executionMonth = WordUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String executionYear = String.valueOf(LocalDate.now().getYear());

        this.body = this.body.replace("[Process]", FilesHelper.getInstance().PROCESS_NAME);
        this.body = this.body.replace("[ProcessDay]", weekProcessDay + " " + processMonthDay + " de " + processMonth + " del " + processYear);
        this.body = this.body.replace("[ExecutionDay]", weekExecutionDay + " " + executionMonthDay + " de " + executionMonth + " del " + executionYear);
        this.body = this.body.replace("%email%", to);


        addRecords();

        send();

        throw new Exception("Ha ocurrido una excepción irrecuperable durante el proceso. Se ha enviado un correo al Adminsitrador con el log adjunto.");
    }

    public void addRecords() {

        String html = "";

        String html2 = "";

        for (Log log : LogHelper.getInstance().getLogs()) {
            html2 = "";

            String timestamp = log.getTimestamp();
            String classname = log.getClassname();
            String method = log.getMethod();
            String message = log.getMessage();

            String src = "cid:error";

            if(log.getLevel().equalsIgnoreCase("INFO")) {
                src = "cid:info";
            }
            if(log.getLevel().equalsIgnoreCase("SEVERE")) {
                src = "cid:error";
            }
            if(log.getLevel().equalsIgnoreCase("WARNING")) {
                src = "cid:warning";
            }

            if(message.length() > 500) {
                message = message.substring(0, 500) + " ...";
            }

            html2 = "<tr><td style='padding: 10 10 10 10;' width='19%' align='center'><div><img class='log-icon' height='auto' src='" + src + "' style='border:0;display:block;outline:none;text-decoration:none;width:40%;' /></div>";
            html2 = html2 + "<div><label style=''>" + timestamp + "</label></div></td><td>";
            html2 = html2 + "<div style='border-left:1px solid #ecedee;padding-left:12px;color:#009688'>" + method + "</div>";
            html2 = html2 + "<div style='border-left:1px solid #ecedee;padding-left:12px;color:#757575'>" + classname + "</div>";
            html2 = html2 + "<div style='border-left:1px solid #ecedee;padding-left:12px'><label>" + message + "</label></div></td></tr>";
            html2 = html2 + "<tr><td style='padding: 0 0 0 0;' width='19%'></td><td><div style='border-left:1px solid #ecedee;padding-left:12px'><br/></div></td></tr>";


            html = html + html2;
        }

        this.body = this.body.replace("[rows]", html);
    }

    private void send() throws MessagingException {

        logger.info("Enviando correo a destinatario: " + to);

        int count = 0;
        int maxTries = 5;

        while(true) {

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                Address toAddress= new InternetAddress(to);
                message.addRecipient(Message.RecipientType.TO, toAddress);
                message.setSubject(subject);
                message.setContent(body, "text/html; charset=utf-8");
                //Transport.send(message);

                //Transport transport = mySession.getTransport();

                //
                // This HTML mail have to 2 part, the BODY and the embedded image
                //
                MimeMultipart multipart = new MimeMultipart("related");

                // first part  (the html)
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(body, "text/html; charset=utf-8");

                // add it
                multipart.addBodyPart(messageBodyPart);

                // attach images
                attachImage(multipart, "<image1>", "/Images/Logo_CTL.webp");

                attachImage(multipart, "<info>", "/Images/info.png");

                attachImage(multipart, "<warning>", "/Images/warning.png");

                attachImage(multipart, "<error>", "/Images/error.png");

                // put everything together
                message.setContent(multipart);

                //transport.connect();
                Transport.send(message, message.getRecipients(Message.RecipientType.TO));
                //transport.close();

                break;

            } catch (Exception e) {
                // handle exception
                logger.info((count+1)+"° intento enviando correo a destinatario: " + to + " - " + e.getMessage());
                if (++count >= maxTries) {
                    logger.error("Error al enviar correo a destinatario: " + to + " - " + e.getMessage());
                    throw e;
                }
            }
        }
    }

    private void attachImage(MimeMultipart multipart, String cid, String fileName) {

        try {

            // second part (the image)
            BodyPart messageBodyPart = new MimeBodyPart();

            InputStream stream = this.getClass().getResourceAsStream(fileName);

            if (stream == null) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = this.getClass().getClassLoader();
                }

                stream = classLoader.getResourceAsStream(fileName);
            }

            DataSource ds = new ByteArrayDataSource(stream, "image/*");

            messageBodyPart.setDataHandler(new DataHandler(ds));
            messageBodyPart.setHeader("Content-ID", cid);
            messageBodyPart.setHeader("Content-Type", ds.getContentType());
            messageBodyPart.setDisposition(Part.INLINE);

            // add it
            multipart.addBodyPart(messageBodyPart);
        }
        catch (MessagingException e1) {
            logger.error("Error: "+e1.getMessage());
            e1.printStackTrace();
        }
        catch (IOException e1) {
            logger.error("Error: "+e1.getMessage());
            e1.printStackTrace();
        }
    }

    private void loadMailBody() throws IOException {
        reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Mail/error.html")));
        String line = "";
        while ((line = reader.readLine()) != null) {
            if(line.trim().isEmpty()) {
                continue;
            }
            body = body + line;
        }
        reader.close();
    }
}