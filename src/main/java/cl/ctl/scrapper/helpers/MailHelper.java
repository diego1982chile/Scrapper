package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.scrappers.AbstractScrapper;
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
public class MailHelper {

    private static final MailHelper instance = new MailHelper();

    private static Session session;

    private static Properties prop;

    //private static final String from = "sistemas@minsal.cl";//"semantikos.minsal@gmail.com";

    private static final String from = ConfigHelper.getInstance().CONFIG.get("mail.from.user"); //"diego.abelardo.soto@gmail.com";//"semantikos.minsal@gmail.com";

    //private static final String to = "diego.abelardo.soto@gmail.com";

    //private static final String to = "cristian.fiedler@fiedler-bi.com";//"semantikos.minsal@gmail.com";

    private static final String to = ConfigHelper.getInstance().CONFIG.get("mail.to");//"diego.abelardo.soto@gmail.com";//"semantikos.minsal@gmail.com";

    private static final String username = ConfigHelper.getInstance().CONFIG.get("mail.from.user"); //"diego.abelardo.soto@gmail.com";

    private static final String password = ConfigHelper.getInstance().CONFIG.get("mail.from.password");;

    private static String subject = "CTL - Descarga Scraps";

    //private static String body = "<b>Bienvenido a Semantikos</b><br><br>Una cuenta asociada a este correo ha sido creada. <ul><li>Para activar su cuenta, por favor pinche el siguiente link: <br>%link%</li><li>Su contraseña inicial es: %password%</li><li>Cambie su contraseña inicial</li><li>Configure sus preguntas de seguridad</li></ul>El Equipo Semantikos";

    private static String body; //= "<table style='border-collapse:collapse;table-layout:fixed;min-width:320px;width:100%;background-color:#f5f7fa;' cellpadding='0' cellspacing='0'><tr><td><table style='background:white;border-collapse:collapse;table-layout:fixed;max-width:600px;min-width:320px;width:100%;font-family:Impact, Charcoal, sans-serif;color:#283593' align='center' cellpadding='0' cellspacing='0'><tr><td align='center' style='width:100%' colspan='3'><img src=\"cid:image2\" style='width:230px'></td></tr><tr><td colspan=3 style='padding-left:2em;padding-right:2em'><hr style='padding: 2px; background: #283593;' /><br/><br/>Estimado(a) %username%, una cuenta asociada a este correo ha sido creada en el Sistema.</p><ul><li>Su contraseña inicial es: %password%</li><li>Cambie su contraseña inicial</li><li>Configure sus preguntas de seguridad</li></ul><p>El equipo Semantikos</p><br/></td></tr><tr><td style='width:30%'></td><td style='background: #283593;width:40%;text-align:center;font-family:arial;font-size:13px;border-radius: 15px;-moz-border-radius: 15px;' align='center' height=31><a style='color: white;text-decoration:none;text-align:center' href='%link%'><strong>Activar Cuenta</strong></a></td><td></td></tr><tr><td colspan='3' align='center'><br/><div>©2016 Ministerio de Salud</div></td></tr></table></td></tr></table>";

    BufferedReader reader;

    private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);

    public MailHelper() {

         prop = System.getProperties();

        //Get the session object
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public static MailHelper getInstance() {
        return instance;
    }

    public void sendMail() throws IOException, MessagingException {

        this.body = "";
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

        String server = "IDTECHSUPP";

        if(UploadHelper.getInstance().getServer().equalsIgnoreCase("LOCAL")) {
            server = "CTL3";
        }

        subject = "Scraps CTL: Proceso " + FilesHelper.getInstance().PROCESS_NAME + " - " + server;

        this.body = this.body.replace("[Server]", server);
        this.body = this.body.replace("[Process]", FilesHelper.getInstance().PROCESS_NAME);
        this.body = this.body.replace("[Retailer]", ProcessHelper.getInstance().getRetailer());
        this.body = this.body.replace("[ProcessDay]", weekProcessDay + " " + processMonthDay + " de " + processMonth + " del " + processYear);
        this.body = this.body.replace("[ExecutionDay]", weekExecutionDay + " " + executionMonthDay + " de " + executionMonth + " del " + executionYear);
        this.body = this.body.replace("%email%", to);

        addRecords();

        if(newFileExists()) {
            send();
        }
    }

    private boolean newFileExists() {

        return true;
        /*
        for (AbstractScrapper scrapper : ScrapperHelper.getInstance().getScrappersByClient(ProcessHelper.getInstance().getClient()).values()) {
            for (FileControl fileControl : scrapper.getFileControlList()) {
                // Solo archivos registrados con nombre proceso actual
                if(fileControl.getFileName().contains(FilesHelper.getInstance().PROCESS_NAME) &&
                        fileControl.getFileName().toLowerCase().contains(scrapper.getHolding().toLowerCase())
                        && scrapper.getNewScraps().contains(fileControl.getFileName())
                        //&& fileControl.isNew()
                        ) {
                    return true;
                }
            }
        }

        if(ProcessHelper.getInstance().getProcessDate().equals(LocalDate.now().minusDays(1))) {
            return true;
        }

        return false;
        */
    }

    public void addRecords() {

        String html = "";

        //for (AbstractScrapper scrapper : ProcessHelper.getInstance().getScrappers().values()) {
        for (AbstractScrapper scrapper : ScrapperHelper.getInstance().getScrappersByRetailer(ProcessHelper.getInstance().getRetailer()).values()) {
            for (FileControl fileControl : scrapper.getFileControlList()) {
                // Solo archivos registrados con nombre proceso actual
                if(fileControl.getFileName().contains(FilesHelper.getInstance().PROCESS_NAME) &&
                        fileControl.getFileName().toLowerCase().contains(scrapper.getClient().toLowerCase())) {

                    String fileName = fileControl.getFileName();
                    String status = fileControl.getStatus();

                    String color = "#4CAF50";

                    if(fileControl.getStatus().equalsIgnoreCase("Error")) {
                        color = "#F44336";
                    }

                    html = html + "<tr>";

                    html = html + "<td style='padding: 0 0 0 0;'>" + fileName + "</td>";
                    html = html + "<td style='padding: 0 0 0 0; color: " + color + "'>" + status + "</td>";

                    html = html + "</tr>";

                    if(fileControl.getStatus().equalsIgnoreCase("Error")) {
                        int rows = (int) Math.round(fileControl.getErrors().get(0).toString().length()*0.03);

                        html = html + "<tr>";
                        html = html + "<td colspan='2'><textarea style='width:98%;resize:none;font-size:12px' rows='" + rows + "' disabled>" + fileControl.getErrors().get(0) + "</textarea></td>";
                        html = html + "</tr>";
                    }

                }
            }
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
        reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Mail/mail.html")));
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