package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.model.exceptions.ScrapEmptyException;
import cl.ctl.scrapper.model.exceptions.ScrapSellsEqualsToZeroException;
import cl.ctl.scrapper.model.scraps.SMURecord;
import cl.ctl.scrapper.scrappers.AbstractScrapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static cl.ctl.scrapper.model.ParameterEnum.FILE_DOWNLOAD_PATH;

/**
 * Created by des01c7 on 17-12-20.
 */
public class FilesHelper {

    //String DOWNLOAD_PATH = System.getProperty("user.home");;

    private String DOWNLOAD_PATH = ConfigHelper.getInstance().getParameter(FILE_DOWNLOAD_PATH.getParameter()); //"C:\\Users\\home-user\\Downloads";

    String PROCESS_NAME;

    JSONParser parser = new JSONParser();

    private String SEPARATOR;

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(FilesHelper.class.getName());

    private static LogHelper fh = LogHelper.getInstance();

    private static final FilesHelper instance = new FilesHelper();

    public static FilesHelper getInstance() {
        return instance;
    }


    public String getSEPARATOR() {
        return SEPARATOR;
    }


    void flushProcessName() {
        String month = String.valueOf(ProcessHelper.getInstance().getProcessDate().getMonthValue());

        if(ProcessHelper.getInstance().getProcessDate().getMonthValue() < 10) {
            month = "0" + month;
        }

        String day = String.valueOf(ProcessHelper.getInstance().getProcessDate().getDayOfMonth());

        if(ProcessHelper.getInstance().getProcessDate().getDayOfMonth() < 10) {
            day = "0" + day;
        }

        PROCESS_NAME = String.valueOf(ProcessHelper.getInstance().getProcessDate().getYear()) + month + day;
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
                    String ext = "." + file2.getPath().split("\\.")[file2.getPath().split("\\.").length-1];
                    //file2.renameTo(new File(file.getAbsolutePath() + ".csv"));
                    file2.renameTo(new File(file.getAbsolutePath() + ext));
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
    public void renameLastFile(AbstractScrapper scrapper, String frequency) {

        logger.log(Level.INFO, "Moviendo archivo retailer = " + scrapper.getRetailer() + " frecuencia = " + frequency);

        String frec = null;

        try {
            String baseName = scrapper.getClient() + "_" + scrapper.getRetailer();

            String ext = ".txt";

            switch(frequency) {
                case "DAY":
                    baseName = baseName + "_Dia";
                    frec = "Dia";
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

            if (!directory.exists()) {
                directory.mkdir();
            }

            String fileName;

            File downloadDir;

            if(scrapper.getFileExt().equals(".csv") || scrapper.getFileExt().equals(".xlsx")) {
                ext = ".zip";
            }

            fileName = DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + baseName + "_" + PROCESS_NAME + ext;

            downloadDir = new File(DOWNLOAD_PATH + SEPARATOR + scrapper.getDownloadSubdirectory());

            File[] files = downloadDir.listFiles();

            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2)
                {
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                }
            });

            for(int i = 0; i < files.length; ++i) {
                if(files[i].isFile()) {
                    ext = "." + files[i].getName().split("\\.")[files[i].getName().split("\\.").length-1];
                    fileName = DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + baseName + "_" + PROCESS_NAME + ext;
                    files[i].renameTo(new File(fileName));
                    break;
                }
            }

            String processDay = ProcessHelper.getInstance().getProcessDate().toString();
            String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
            String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
            String fileNameShort = fileName.split(Pattern.quote(SEPARATOR))[fileName.split(Pattern.quote(SEPARATOR)).length - 1];
            String status = "OK";

            FileControl fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), "DIA", scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);
            fileControl.setScrapper(scrapper);

            LogHelper.getInstance().registerFileControl(fileControl);
            //scrapper.getFileControlList().add(new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), "DIA", scrapper.getHolding(), scrapper.getCadena(), fileNameShort, status));
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw  e;
        }

    }

    // Comprobar tamaño de ultimo archivo descargado
    public void checkLastFile(AbstractScrapper scrapper, String frequency) throws ScrapEmptyException, IOException, ScrapSellsEqualsToZeroException {

        logger.log(Level.INFO, "Comprobando archivo retailer = " + scrapper.getRetailer() + " frecuencia = " + frequency);

        File downloadDir;

        downloadDir = new File(DOWNLOAD_PATH + SEPARATOR + scrapper.getDownloadSubdirectory());

        File[] files = downloadDir.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
            }
        });

        File file = null;

        for(int i = 0; i < files.length; ++i) {

            if(files[i].isFile()) {

                file = files[i];

                if((int) files[i].length() == 0) {
                    throw new ScrapEmptyException("El scrap está vacío!!");
                }
                else {
                    break;
                }

            }

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

        String retailer = scrapper.getRetailer();

        String ext = scrapper.getFileExt();

        File directory = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME);

        // Comprobar que exista el directorio del proceso, de lo contrario retornar false
        if(!directory.exists()) {
            return false;
        }

        File diario = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + retailer + "_Dia_" + PROCESS_NAME + ext);

        String processDay = ProcessHelper.getInstance().getProcessDate().toString();
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));

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

        FileControl fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), "Dia", scrapper.getClient(), retailer, fileNameShort, status);
        fileControl.setScrapper(scrapper);

        LogHelper.getInstance().registerFileControl(fileControl);

        if(!scrapper.getFileControlList().contains(fileControl)) {
            scrapper.getFileControlList().add(fileControl);
        }

        if(scrapper.isOnlyDiary()) {
            return true;
        }

        File mensual = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + retailer + "_Mes_" + PROCESS_NAME + ext);
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

        fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), "Mes", scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);

        LogHelper.getInstance().registerFileControl(fileControl);

        if(!scrapper.getFileControlList().contains(fileControl)) {
            scrapper.getFileControlList().add(fileControl);
        }

        // Si el proceso es del Domingo, Comprobar que exista el archivo semanal de la cadena, de lo contrario retornar false
        if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            File semanal = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + retailer + "_Dom_" + PROCESS_NAME + ext);
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

            fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), "Dom", scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);

            LogHelper.getInstance().registerFileControl(fileControl);

            if(!scrapper.getFileControlList().contains(fileControl)) {
                scrapper.getFileControlList().add(fileControl);
            }
        }

        return true;
    }

    public boolean checkFile(AbstractScrapper scrapper, String frequency) {

        switch(frequency) {
            case "DAY":
                frequency = "Dia";
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
        File file = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + scrapper.getRetailer() + "_" + frequency + "_" + PROCESS_NAME + scrapper.getFileExt());

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
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String fileNameShort = file.getName().split(Pattern.quote(SEPARATOR))[file.getName().split(Pattern.quote(SEPARATOR)).length - 1];
        String status = "OK";

        FileControl fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), frequency, scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);

        LogHelper.getInstance().registerFileControl(fileControl);

        if(!scrapper.getFileControlList().contains(fileControl)) {
            scrapper.getFileControlList().add(fileControl);
        }

        return true;
    }


    public void registerFileControlOK(AbstractScrapper scrapper, String frequency) {

        switch(frequency) {
            case "DAY":
                frequency = "Dia";
                break;
            case "MONTH":
                frequency = "Mes";
                break;
            case "WEEK":
                frequency = "Dom";
                break;
        }

        // Comprobar que exista el archivo con la frecuencia de la cadena, de lo contrario retornar false
        File file = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + scrapper.getRetailer() + "_" + frequency + "_" + PROCESS_NAME + scrapper.getFileExt());

        String processDay = ProcessHelper.getInstance().getProcessDate().toString();
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String fileNameShort = file.getName().split(Pattern.quote(SEPARATOR))[file.getName().split(Pattern.quote(SEPARATOR)).length - 1];
        String status = "OK";

        FileControl fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), frequency, scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);

        LogHelper.getInstance().registerFileControl(fileControl);

        if(!scrapper.getFileControlList().contains(fileControl)) {
            scrapper.getFileControlList().add(fileControl);
        }

    }

    public void registerFileControlNew(AbstractScrapper scrapper, String frequency) {

        switch(frequency) {
            case "DAY":
                frequency = "Dia";
                break;
            case "MONTH":
                frequency = "Mes";
                break;
            case "WEEK":
                frequency = "Dom";
                break;
        }

        // Comprobar que exista el archivo con la frecuencia de la cadena, de lo contrario retornar false
        File file = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + scrapper.getRetailer() + "_" + frequency + "_" + PROCESS_NAME + scrapper.getFileExt());

        String processDay = ProcessHelper.getInstance().getProcessDate().toString();
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String fileNameShort = file.getName().split(Pattern.quote(SEPARATOR))[file.getName().split(Pattern.quote(SEPARATOR)).length - 1];
        String status = "OK";

        FileControl fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), frequency, scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);

        fileControl.setNew(true);

        scrapper.getNewScraps().add(fileNameShort);

        LogHelper.getInstance().registerFileControl(fileControl);

        if(!scrapper.getFileControlList().contains(fileControl)) {
            scrapper.getFileControlList().add(fileControl);
        }

    }

    public void registerFileControlError(AbstractScrapper scrapper, String frequency, String errorMsg) {

        switch(frequency) {
            case "DAY":
                frequency = "Dia";
                break;
            case "MONTH":
                frequency = "Mes";
                break;
            case "WEEK":
                frequency = "Dom";
                break;
        }

        // Comprobar que exista el archivo con la frecuencia de la cadena, de lo contrario retornar false
        File file = new File(DOWNLOAD_PATH + SEPARATOR + PROCESS_NAME + SEPARATOR + scrapper.getClient() + "_" + scrapper.getRetailer() + "_" + frequency + "_" + PROCESS_NAME + scrapper.getFileExt());

        String processDay = ProcessHelper.getInstance().getProcessDate().toString();
        String dayOfWeekProcess = WordUtils.capitalize(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String dayOfWeek = WordUtils.capitalize(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")));
        String fileNameShort = file.getName().split(Pattern.quote(SEPARATOR))[file.getName().split(Pattern.quote(SEPARATOR)).length - 1];
        String status = "Error";

        FileControl fileControl = new FileControl(PROCESS_NAME, processDay, dayOfWeekProcess, dayOfWeek, ProcessHelper.getInstance().getProcessDate().toString(), frequency, scrapper.getClient(), scrapper.getRetailer(), fileNameShort, status);

        if(errorMsg.length() > 180) {
            errorMsg = errorMsg.substring(0, 180) + " ...";
        }

        fileControl.getErrors().add(errorMsg);

        LogHelper.getInstance().registerFileControl(fileControl);

        if(!scrapper.getFileControlList().contains(fileControl)) {
            scrapper.getFileControlList().add(fileControl);
        }
    }


}
