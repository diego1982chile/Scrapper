package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.scrappers.AbstractScrapper;
import com.jcraft.jsch.*;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by des01c7 on 17-12-20.
 */
public class UploadHelper {

    private static Session session;
    private static JSch jsch;
    private static Channel sftp;
    private static ChannelSftp sftpChannel;

    private static String destiny = "/di/projects/Legrand/_Scraps_Hoy/";
    private static String remote = ConfigHelper.getInstance().CONFIG.get("upload.path");//"/home/dsoto/temp/";

    private static String user =  ConfigHelper.getInstance().CONFIG.get("upload.user");//"dsoto";
    private static String host = ConfigHelper.getInstance().CONFIG.get("upload.host");//"cfiedler.dyndns.org";
    int port = 22;

    private static final int CHANNEL_TIMEOUT = 5000;

    private static String keyPassword = ConfigHelper.getInstance().CONFIG.get("upload.password");//"diegoabelardo";

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(UploadHelper.class.getName());
    static LogHelper fh = LogHelper.getInstance();

    private static final UploadHelper instance = new UploadHelper();

    public static UploadHelper getInstance() {
        return instance;
    }

    public UploadHelper() {
        // This block configure the logger with handler and formatter
        try {
            logger.addHandler(fh);
            session = createSession(user, host, keyPassword);
        } catch (SecurityException | JSchException e) {
            e.printStackTrace();
        }

    }

    public void sendSignal(String name) {

        String local = FilesHelper.getInstance().getUploadPath();
        File signal = new File(local + FileSystems.getDefault().getSeparator() + name + "_signal.txt");

        try {
            signal.createNewFile();
            copyLocalToRemote(local, remote, signal.getName());

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFiles() throws JSchException, IOException {
        String local = FilesHelper.getInstance().getUploadPath();

        for (AbstractScrapper scrapper : ProcessHelper.getInstance().getScrappers().values()) {
            for (FileControl fileControl : scrapper.getFileControlList()) {
                if(!fileControl.getStatus().equalsIgnoreCase("Error")) {
                    try {
                        // Solo archivos registrados con nombre proceso actual y cliente proceso actual
                        if(fileControl.getFileName().contains(FilesHelper.getInstance().PROCESS_NAME) && fileControl.getFileName().contains(scrapper.getHolding())) {
                            copyLocalToRemote(local, remote, fileControl.getFileName());
                        }
                    } catch (JSchException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                        throw e;
                    }
                    catch (FileNotFoundException e) {
                        // TODO: Por ahora los archivos que ya habian sido descargados en procesos anteriores, omitirlos en la excepción
                        logger.log(Level.WARNING, e.getMessage());
                    }
                    catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                        throw e;
                    }
                }
            }
        }
    }

    public void closeSession() {
        sftpChannel.disconnect();
        sftp.disconnect();
        session.disconnect();
    }

    public void moveFiles() throws SftpException, IOException, JSchException {

        try {
            sftpChannel.ls(remote);

            for (Object o : sftpChannel.ls(remote)) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) o;
                if(!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                    //sftpChannel.rename(remote + entry.getFilename(), destiny + entry.getFilename());
                    String command = "mv " + remote + entry.getFilename() + " " + destiny + entry.getFilename();
                    runSudoCommand(user, keyPassword, host, command);
                }
            }

            //sftpChannel.disconnect();
            //sftp.disconnect();
            //session.disconnect();

        } catch (SftpException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }

    }

    public static void runSudoCommand(String user, String password, String host, String command) throws IOException, JSchException {

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //config.put("PreferredAuthentications", "password");
        JSch jsch = new JSch();
        Session session;
        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected to " + host);
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
            channel.setInputStream(null);
            OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            ((ChannelExec) channel).setPty(true);
            channel.connect();
            out.write((password + "\n").getBytes());
            out.flush();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("Exit status: " + channel.getExitStatus());
                    break;
                }
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        } catch (JSchException | IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }
    }

    private static Session createSession(String user, String host, String keyPassword) throws JSchException {

        jsch = new JSch();

        Channel channel;
        OutputStream os;

        try {

            session = jsch.getSession(user, host, 22);
            session.setPassword(keyPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            sftp = session.openChannel("sftp");
            sftp.connect();
            sftpChannel = (ChannelSftp) sftp;

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }

        return session;
    }

    private static void copyLocalToRemote(String from, String to, String fileName) throws JSchException, IOException {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //config.put("PreferredAuthentications", "password");
        JSch jsch = new JSch();
        Session session;
        session = jsch.getSession(user, host, 22);
        session.setPassword(keyPassword);
        session.setConfig(config);
        session.connect();

        boolean ptimestamp = true;
        from = from + fileName;
        to = to + fileName;

        // exec 'scp -t rfile' remotely
        String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + to;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        File _lfile = new File(from);

        if (ptimestamp) {
            command = "T" + (_lfile.lastModified() / 1000) + " 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }
        }

        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = _lfile.length();
        command = "C0644 " + filesize + " ";
        if (from.lastIndexOf('/') > 0) {
            command += from.substring(from.lastIndexOf('/') + 1);
        } else {
            command += from;
        }

        command += "\n";
        out.write(command.getBytes());
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        // send a content of lfile
        FileInputStream fis = new FileInputStream(from);
        byte[] buf = new byte[1024];
        while (true) {
            int len = fis.read(buf, 0, buf.length);
            if (len <= 0) break;
            out.write(buf, 0, len); //out.flush();
        }

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }
        out.close();

        try {
            if (fis != null) fis.close();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw ex;
        }

        channel.disconnect();
        session.disconnect();
    }

    public static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //         -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}
