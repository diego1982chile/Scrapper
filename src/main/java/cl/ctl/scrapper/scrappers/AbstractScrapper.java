package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.LogHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.BusinessException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by des01c7 on 16-12-20.
 */
public abstract class AbstractScrapper {

    WebDriver driver;

    /** Logger para la clase */
    Logger logger = Logger.getLogger(ConstrumartScrapper.class.getName());
    LogHelper fh = LogHelper.getInstance();

    String cadena;

    String url;

    String fileExt = ".csv";

    boolean onlyDiary = false;

    public AbstractScrapper() throws IOException {
        // This block configure the logger with handler and formatter
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);

            WebDriverManager.chromedriver().setup();

            ChromeOptions chrome_options = new ChromeOptions();
            chrome_options.addArguments("--start-maximized");
            //chrome_options.addArguments("--headless");
            chrome_options.addArguments("--no-sandbox");
            chrome_options.addArguments("--disable-dev-shm-usage");

            driver = new ChromeDriver(chrome_options);

        } catch (SecurityException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public boolean isOnlyDiary() {
        return onlyDiary;
    }

    public void setOnlyDiary(boolean onlyDiary) {
        this.onlyDiary = onlyDiary;
    }

    void checkScraps() throws BusinessException {
        if(FilesHelper.getInstance().checkFiles(this)) {
            throw new BusinessException("Scrapper '" + cadena + "' -> Archivos ya fueron generados! se omite el proceso");
        }
    }

    void checkScrap(int count) throws BusinessException {

        String freq;

        switch (count) {
            case 1:
                freq = "DAY";
                break;
            case 2:
                freq = "MONTH";
                break;
            case 3:
                freq = "WEEK";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + count);
        }

        if(FilesHelper.getInstance().checkFile(cadena, freq, fileExt)) {
            throw new BusinessException("Scrapper " + cadena + " -> Archivo de frecuencia '" + freq + "' ya fue generado! se omite el proceso diario");
        }
    }

    void renameFile(String cadena, int count) {

        String freq;

        switch (count) {
            case 1:
                freq = "DAY";
                break;
            case 2:
                freq = "MONTH";
                break;
            case 3:
                freq = "WEEK";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + count);
        }

        FilesHelper.getInstance().renameLastFile(cadena, freq, fileExt);
    }

    void scrap() throws Exception {

        checkScraps();

        Thread.sleep(2000);

        driver.get(url);

        Thread.sleep(2000);

        login();

        Thread.sleep(2000);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");

        // Generar Scrap Diario
        String since = formatter.format(ProcessHelper.getInstance().getProcessDate());
        String until = since;

        logger.log(Level.INFO, "Descargando Scrap Diario...");

        generateScrap(since, until, 1);

        Thread.sleep(2000);

        // Si la cadena genera solo scraps diarios retornar en este punto
        if(onlyDiary) {
            driver.quit();
            return;
        }

        // Generar Scrap Mensual
        since = formatter.format(ProcessHelper.getInstance().getProcessDate().minusDays(ProcessHelper.getInstance().getProcessDate().getDayOfMonth()).plusDays(1));

        logger.log(Level.INFO, "Descargando Scrap Mensual...");

        generateScrap(since, until, 2);

        Thread.sleep(2000);

        // Si es proceso de Domingo
        // Generar Scrap Semanal
        if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            since = formatter.format(ProcessHelper.getInstance().getProcessDate().minusDays(6));
            logger.log(Level.INFO, "Descargando Scrap Semanal...");
            generateScrap(since, until, 3);
        }

        Thread.sleep(2000);

        driver.quit();

    }

    void generateScrap(String since, String until, int count) throws Exception {

        try {
            checkScrap(count);
            doScrap(since, until);
        }
        catch(BusinessException e) {
            logger.log(Level.WARNING, e.getMessage());
            try {
                driver.quit();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            renameFile(cadena, count);
        }
    }

    abstract void doScrap(String since, String until) throws Exception;

    abstract void login() throws Exception;

    public void process() throws Exception {

        try {
            scrap();
        }
        catch(BusinessException e) {
            logger.log(Level.WARNING, e.getMessage());
            try {
                driver.quit();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
