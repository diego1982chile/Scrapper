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

    String CADENA;

    String URL;

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

    void checkScraps() throws BusinessException {
        if(FilesHelper.getInstance().checkFiles(CADENA)) {
            throw new BusinessException("Scrapper" + CADENA + " -> Archivos ya fueron generados! se omite el proceso");
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

        if(FilesHelper.getInstance().checkFiles(CADENA, freq)) {
            throw new BusinessException("Scrapper " + CADENA + " -> Archivo diario ya fue generado! se omite el proceso diario");
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

        FilesHelper.getInstance().renameLastFile(cadena, freq);
    }

    void scrap() throws Exception {

        checkScraps();

        Thread.sleep(2000);

        driver.get(URL);

        Thread.sleep(2000);

        login();

        Thread.sleep(2000);

        // Generar Scrap Diario
        int since = ProcessHelper.getInstance().getProcessDate().getDayOfMonth();
        int until = since;

        generateScrap(since, until, 1);

        Thread.sleep(2000);

        // Si la cadena genera solo scraps diarios retornar en este punto
        if(onlyDiary) {
            driver.quit();
            return;
        }

        // Generar Scrap Mensual
        since = 1;

        generateScrap(since, until, 2);

        Thread.sleep(2000);

        // Si es proceso de Domingo
        // Generar Scrap Semanal
        if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            since = ProcessHelper.getInstance().getProcessDate().minusDays(6).getDayOfMonth();
            generateScrap(since, ProcessHelper.getInstance().getProcessDate().getDayOfMonth(), 3);
        }

        Thread.sleep(2000);

        driver.quit();

    }

    void generateScrap(int since, int until, int count) throws Exception {

        try {
            checkScrap(count);
            doScrap(since, until, count);
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
            renameFile(CADENA, count);
        }
    }

    abstract void doScrap(int since, int until, int count) throws Exception;

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
