package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.scrappers.AbstractScrapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.time.DayOfWeek;
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
public class FilesHelper {

    //String DOWNLOAD_PATH = System.getProperty("user.home");;

    String DOWNLOAD_PATH = ConfigHelper.getInstance().CONFIG.get("file.download_path");//"C:\\Users\\home-user\\Downloads";

    String PROCESS_NAME;

    JSONParser parser = new JSONParser();

    static String SEPARATOR;

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(FilesHelper.class.getName());

    static LogHelper fh = LogHelper.getInstance();

    private static final FilesHelper instance = new FilesHelper();

    public static FilesHelper getInstance() {
        return instance;
    }

    public FilesHelper() {

        String homePath = System.getProperty("user.home");;
        String preferencesPath = null;

        String month = String.valueOf(ProcessHelper.getInstance().getProcessDate().getMonthValue());

        if(ProcessHelper.getInstance().getProcessDate().getMonthValue() < 10) {
            month = "0" + month;
        }

        String day = String.valueOf(ProcessHelper.getInstance().getProcessDate().getDayOfMonth());

        if(ProcessHelper.getInstance().getProcessDate().getDayOfMonth() < 10) {
            day = "0" + day;
        }

        PROCESS_NAME = String.valueOf(ProcessHelper.getInstance().getProcessDate().getYear()) + month + day;

        // This block configure the logger with handler and formatter
        try {
            logger.addHandler(fh);

            if (SystemUtils.IS_OS_LINUX) {
                SEPARATOR = "/";
            } else if (SystemUtils.IS_OS_WINDOWS) {
                SEPARATOR = "\\";
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        /*
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
            }
            else {
                // Si se alcanzó esta excepción es porque el directorio de descargas corresponde a las descargas del sistema
                if(Locale.getDefault().toLanguageTag().equals("es-CL")) {
                    DOWNLOAD_PATH = homePath + SEPARATOR + "Descargas";
                }
                if(Locale.getDefault().toLanguageTag().equals("en-US")) {
                    DOWNLOAD_PATH = homePath + SEPARATOR + "Downloads";
                }
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        */

    }

    public void processFiles() throws ZipException {

        File directory = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME);

        // Descomprimir archivos descargados
        for (File file : directory.listFiles()) {
            if(file.getName().contains(".zip")) {
                uncompress(file);
                file.delete();
            }
        }

        // Renombrar archivos descomprimidos
        for (File file : directory.listFiles()) {
            if(file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    file2.renameTo(new File(file.getAbsolutePath() + ".csv"));
                }
                file.delete();
            }
        }

        LogHelper.getInstance().updateFileNames();
    }

    public void uncompress(File zipFile) throws ZipException {

        try {

            File directory = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME);

            ZipFile file = new ZipFile(zipFile);

            file.extractAll(directory.getAbsolutePath() + SEPARATOR + zipFile.getName().replace(".zip", "").replace(".csv", ""));

        }
        catch(IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }

    }

    // Renombrar archivos descargados
    public void renameLastFile(String holding, String frequency, String ext) {

        logger.log(Level.INFO, "Moviendo archivo cadena = " + holding + " frecuencia = " + frequency);
        
        String frec = null;
        
        try {
            String baseName = "Legrand_" + holding;

            switch(frequency) {
                case "DAY":
                    baseName = baseName + "_Dia";
                    frec = "Día";
                    break;
                case "MONTH":
                    baseName = baseName + "_Mes";
                    frec = "Mes";
                    break;
                case "WEEK":
                    baseName = baseName + "_Dom";
                    frec = "Dom";
                    break;
            }

            File directory = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME);

            if (! directory.exists()) {
                directory.mkdir();
            }

            String fileName;

            File downloadDir;

            if(ext.equals(".csv")) {
                ext = ".zip";
            }

            fileName = DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + baseName + "_" + PROCESS_NAME + ext;

            downloadDir = new File(DOWNLOAD_PATH);

            File[] files = downloadDir.listFiles();

            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2)
                {
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                }
            });

            for(int i = 0; i < files.length; ++i) {
                if(files[i].isFile()) {
                    files[i].renameTo(new File(fileName));
                    break;
                }
            }

            String processDay = ProcessHelper.getInstance().getProcessDate().toString();
            String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
            String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
            String fileNameShort = fileName.split(Pattern.quote(SEPARATOR))[fileName.split(Pattern.quote(SEPARATOR)).length - 1];
            String status = "OK";

            LogHelper.getInstance().registerFileControl(new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, frec, holding, fileNameShort, status));
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw  e;
        }

    }

    public String getDownloadPath() {
        return DOWNLOAD_PATH + SEPARATOR;
    }

    public long countFiles() {
        return  new File(DOWNLOAD_PATH).listFiles().length;
    }

    public String getUploadPath() {
        return DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR;
    }

    public boolean checkFiles(AbstractScrapper scrapper) {

        String cadena = scrapper.getCadena();

        String ext = scrapper.getFileExt();

        File directory = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME);

        // Comprobar que exista el directorio del proceso, de lo contrario retornar false
        if(!directory.exists()) {
            return false;
        }

        File diario = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + "Legrand_" + cadena + "_Dia_" + PROCESS_NAME + ext);

        String processDay = ProcessHelper.getInstance().getProcessDate().toString();
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));

        String fileNameShort = diario.getName().split(Pattern.quote(SEPARATOR))[diario.getName().split(Pattern.quote(SEPARATOR)).length - 1];
        String status = "OK";

        boolean flag = false;

        // Comprobar que exista el archivo diario de la cadena, de lo contrario retornar false
        for (String s : directory.list()) {
            if(diario.getName().split("\\.")[0].equals(s.split("\\.")[0])) {
                flag = true;
                break;
            }
        }

        if(!flag) {
            return false;
        }

        LogHelper.getInstance().registerFileControl(new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, "Dia", cadena, fileNameShort, status));

        if(scrapper.isOnlyDiary()) {
            return true;
        }

        File mensual = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + "Legrand_" + cadena + "_Mes_" + PROCESS_NAME + ext);
        fileNameShort = mensual.getName().split(Pattern.quote(SEPARATOR))[mensual.getName().split(Pattern.quote(SEPARATOR)).length - 1];

        flag = false;

        // Comprobar que exista el archivo mensual de la cadena, de lo contrario retornar false
        for (String s : directory.list()) {
            if(mensual.getName().split("\\.")[0].equals(s.split("\\.")[0])) {
                flag = true;
                break;
            }
        }

        if(!flag) {
            return false;
        }

        LogHelper.getInstance().registerFileControl(new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, "Mes", cadena, fileNameShort, status));

        // Si el proceso es del Domingo, Comprobar que exista el archivo semanal de la cadena, de lo contrario retornar false
        if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            File semanal = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + "Legrand_" + cadena + "_Dom_" + PROCESS_NAME + ext);
            fileNameShort = semanal.getName().split(Pattern.quote(SEPARATOR))[semanal.getName().split(Pattern.quote(SEPARATOR)).length - 1];

            flag = false;

            for (String s : directory.list()) {
                if(semanal.getName().split("\\.")[0].equals(s.split("\\.")[0])) {
                    flag = true;
                    break;
                }
            }

            if(!flag) {
                return false;
            }

            LogHelper.getInstance().registerFileControl(new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, "Dom", cadena, fileNameShort, status));
        }

        return true;
    }

    public boolean checkFile(String cadena, String frequency, String ext) {


        switch(frequency) {
            case "DAY":
                frequency = "Día";
                break;
            case "MONTH":
                frequency = "Mes";
                break;
            case "WEEK":
                frequency = "Dom";
                break;
        }

        File directory = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME);

        // Comprobar que exista el directorio del proceso, de lo contrario retornar false
        if(!directory.exists()) {
            return false;
        }

        // Comprobar que exista el archivo con la frecuencia de la cadena, de lo contrario retornar false
        File file = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + "Legrand_" + cadena + "_" + frequency + "_" + PROCESS_NAME + ext);

        boolean flag = false;

        for (String s : directory.list()) {
            if(file.getName().split("\\.")[0].equals(s.split("\\.")[0])) {
                flag = true;
                break;
            }
        }

        if(!flag) {
            return false;
        }

        String processDay = ProcessHelper.getInstance().getProcessDate().toString();
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        String fileNameShort = file.getName().split(Pattern.quote(SEPARATOR))[file.getName().split(Pattern.quote(SEPARATOR)).length - 1];
        String status = "OK";

        LogHelper.getInstance().registerFileControl(new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, frequency, cadena, fileNameShort, status));

        return true;
    }

}
