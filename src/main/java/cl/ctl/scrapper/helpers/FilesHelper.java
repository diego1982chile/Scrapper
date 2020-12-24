package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.FileControl;
import org.apache.commons.lang.SystemUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by des01c7 on 17-12-20.
 */
public class FilesHelper {

    //String DOWNLOAD_PATH = System.getProperty("user.home");;

    String DOWNLOAD_PATH = "C:\\Users\\home-user\\Downloads";

    JSONParser parser = new JSONParser();
    LocalDate processDate  = LocalDate.now().minusDays(1);
    static String SEPARATOR;

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(FilesHelper.class.getName());

    FileHandler fh;

    private static final FilesHelper instance = new FilesHelper();

    public static FilesHelper getInstance() {
        return instance;
    }

    public FilesHelper() {

        String homePath = System.getProperty("user.home");;
        String preferencesPath = null;

        // This block configure the logger with handler and formatter
        try {
            fh = new FileHandler("Scrapper.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);

            if (SystemUtils.IS_OS_LINUX) {
                SEPARATOR = "/";
            } else if (SystemUtils.IS_OS_WINDOWS) {
                SEPARATOR = "\\";
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public void processFiles() {

        String processName = String.valueOf(processDate.getYear()) + String.valueOf(processDate.getMonthValue()) + String.valueOf(processDate.getDayOfMonth());

        File directory = new File(DOWNLOAD_PATH + SEPARATOR + processName);

        // Descomprimir archivos descargados
        for (File file : directory.listFiles()) {
            if(file.getName().contains(".zip")) {
                uncompress(file);
                file.delete();
            }
        }
    }

    public void uncompress(File zipFile) {

        try(ZipFile file = new ZipFile(zipFile))
        {
            String processName = String.valueOf(processDate.getYear()) + String.valueOf(processDate.getMonthValue()) + String.valueOf(processDate.getDayOfMonth());

            File directory = new File(DOWNLOAD_PATH + SEPARATOR + processName);

            FileSystem fileSystem = FileSystems.getDefault();
            //Get file entries
            Enumeration<? extends ZipEntry> entries = file.entries();

            //We will unzip files in this folder
            String uncompressedDirectory = directory.getAbsolutePath();
            //Files.createDirectory(fileSystem.getPath(uncompressedDirectory));

            //Iterate over entries
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory())
                {
                    System.out.println("Creating Directory:" + uncompressedDirectory + entry.getName());
                    Files.createDirectories(fileSystem.getPath(uncompressedDirectory + entry.getName()));
                }
                //Else create the file
                else
                {
                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = uncompressedDirectory + SEPARATOR + zipFile.getName().replace("zip","csv");
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                    while (bis.available() > 0)
                    {
                        fileOutput.write(bis.read());
                    }
                    fileOutput.close();
                    System.out.println("Written :" + entry.getName());
                }
            }
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage());
        }

    }

    // Renombrar archivos descargados
    public void renameLastDownloadedFile(String holding, String frequency) {

        logger.log(Level.INFO, "Moviendo archivo cadena = " + holding + " frecuencia = " + frequency);

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

            if (! directory.exists()) {
                directory.mkdir();
            }

            String fileName;

            File downloadDir;

            if(holding.equals("Sodimac")) {
                fileName = DOWNLOAD_PATH + SEPARATOR + processName + SEPARATOR + baseName + "_" + processName + ".txt";
            }
            else {
                fileName = DOWNLOAD_PATH + SEPARATOR + processName + SEPARATOR + baseName + "_" + processName + ".zip";
            }

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

    public long countFiles() {
        return  new File(DOWNLOAD_PATH).listFiles().length;
    }

}
