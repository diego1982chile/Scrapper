package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import org.apache.commons.lang.SystemUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

/**
 * Created by des01c7 on 17-12-20.
 */
public class MailHelper {

    String DOWNLOAD_PATH = System.getProperty("user.home");;
    String DOWNLOAD_PATH_ALT = System.getProperty("user.home");;

    JSONParser parser = new JSONParser();
    LocalDate processDate  = LocalDate.now().minusDays(1);
    static String SEPARATOR;

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(MailHelper.class.getName());

    FileHandler fh;

    private static final MailHelper instance = new MailHelper();

    public static MailHelper getInstance() {
        return instance;
    }

    public MailHelper() {

        String homePath = System.getProperty("user.home");;
        String preferencesPath = null;

        // This block configure the logger with handler and formatter
        try {
            fh = new FileHandler("Scrapper.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (SystemUtils.IS_OS_LINUX) {
                preferencesPath = "/.config/google-chrome/Default/Preferences";
                preferencesPath = homePath + preferencesPath;
                SEPARATOR = "/";
            } else if (SystemUtils.IS_OS_WINDOWS) {
                preferencesPath = "\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Preferences";
                preferencesPath = homePath + preferencesPath;
                SEPARATOR = "\\";
            } else {
                //throw new Exception("Plataforma no soportada. No se puede determinar el HOME de este sistema");
                logger.log(Level.SEVERE, "Plataforma no soportada. No se puede determinar el HOME de este sistema");
            }

            JSONObject preferences = (JSONObject) parser.parse(new FileReader(preferencesPath));

            JSONObject download = (JSONObject) preferences.get("download");

            String defaultDirectory = (String) download.get("default_directory");

            if(defaultDirectory != null) {
                DOWNLOAD_PATH = defaultDirectory;
                DOWNLOAD_PATH_ALT = DOWNLOAD_PATH_ALT + SEPARATOR + "Downloads";
            }
            else {
                // Si se alcanzó esta excepción es porque el directorio de descargas corresponde a las descargas del sistema
                if(Locale.getDefault().toLanguageTag().equals("es-CL")) {
                    DOWNLOAD_PATH = homePath + SEPARATOR + "Descargas";
                }
                if(Locale.getDefault().toLanguageTag().equals("en-US")) {
                    DOWNLOAD_PATH = homePath + SEPARATOR + "Downloads";
                }
                DOWNLOAD_PATH_ALT = DOWNLOAD_PATH_ALT + SEPARATOR + "Downloads";
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

    }

    public void processFiles() {

        // Descomprimir archivos descargados


        // Renombrar archivos dentro de las carpetas descomprimidas

        // Mover archivos a carpeta padre

        // Subir archivos a servidor
    }

    // Renombrar archivos descargados
    public void renameLastDownloadedFile(String holding, String frequency) {

        try {
            String baseName = "Legrand_" + holding;

            switch(frequency) {
                case "DAY":
                    baseName = baseName + "_Dia";
                    break;
                case "MONTH":
                    baseName = baseName + "_Mes";
                    break;
                case "WEEK":
                    baseName = baseName + "_Dom";
                    break;
            }

            String processName = String.valueOf(processDate.getYear()) + String.valueOf(processDate.getMonthValue()) + String.valueOf(processDate.getDayOfMonth());

            File directory = new File(DOWNLOAD_PATH + SEPARATOR + processName);

            if (! directory.exists()){
                directory.mkdir();
            }

            String fileName;

            File downloadDir;

            if(holding.equals("Sodimac")) {
                fileName = DOWNLOAD_PATH + SEPARATOR + processName + SEPARATOR + baseName + "_" + processName + ".txt";
                downloadDir = new File(DOWNLOAD_PATH_ALT);
            }
            else {
                fileName = DOWNLOAD_PATH + SEPARATOR + processName + SEPARATOR + baseName + "_" + processName + ".zip";
                downloadDir = new File(DOWNLOAD_PATH);
            }

            File[] files = downloadDir.listFiles();

            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2)
                {
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                }
            });

            files[0].renameTo(new File(fileName));

            String processDay = processDate.toString();
            String dayOfWeekProcess = processDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            String dayOfWeek = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            String fileNameShort = fileName.split(Pattern.quote(SEPARATOR))[fileName.split(Pattern.quote(SEPARATOR)).length - 1];
            String status = "OK";

            ProcessHelper.getInstance().registerFileControl(new FileControl(processDay, dayOfWeekProcess, dayOfWeek, frequency, holding, fileNameShort, status));
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

    }

    public String getDownloadPath() {
        return DOWNLOAD_PATH + SEPARATOR;
    }

}
