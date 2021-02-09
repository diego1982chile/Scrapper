package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.LogHelper;
import cl.ctl.scrapper.model.BusinessException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by des01c7 on 16-12-20.
 */
public class AbstractScrapper {

    WebDriver driver;

    /** Logger para la clase */
    Logger logger = Logger.getLogger(ConstrumartScrapper.class.getName());
    LogHelper fh = LogHelper.getInstance();

    String CADENA;

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
}
