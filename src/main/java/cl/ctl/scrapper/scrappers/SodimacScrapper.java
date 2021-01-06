package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.CaptchaSolver;
import cl.ctl.scrapper.helpers.FilesHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by des01c7 on 16-12-20.
 */
public class SodimacScrapper {

    WebDriver driver;
    private static  final String URL = "https://b2b.sodimac.com/b2bsocopr/grafica/html/index.html";
    LocalDate processDate  = LocalDate.now().minusDays(1);
    private static final String CADENA = "Sodimac";

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(ConstrumartScrapper.class.getName());
    FileHandler fh;

    public SodimacScrapper() throws IOException {

        // This block configure the logger with handler and formatter
        try {
            fh = new FileHandler("Scrapper.log");
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
        } catch (
        IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void scrap() throws Exception {

        driver.get(URL);

        // *Login
        login();

        Thread.sleep(5000);

        // Generar Scrap Diario
        generateScrap(processDate.getDayOfMonth());
        FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "DAY");

        Thread.sleep(2000);

        driver.quit();
    }

    private void login() throws InterruptedException {

        int cont = 0;

        Select b2b;

            try {
                Thread.sleep(3000);
                driver.switchTo().frame(0);
                b2b = new Select(driver.findElement(By.id("CADENA")));
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());

                try {
                    Thread.sleep(3000);
                    driver.switchTo().parentFrame();
                    b2b = new Select(driver.findElement(By.id("CADENA")));
                }
                catch (Exception e2) {
                    logger.log(Level.SEVERE, e.getMessage());

                    Thread.sleep(3000);
                    driver.switchTo().parentFrame();
                    driver.switchTo().frame(0);
                    b2b = new Select(driver.findElement(By.id("CADENA")));
                }

            }

        Thread.sleep(3000);
        b2b.selectByValue("6");
        Thread.sleep(2000);

        driver.findElement(By.id("empresa")).sendKeys("796027304");
        Thread.sleep(2000);
        driver.findElement(By.id("usuario")).sendKeys("128088660");
        Thread.sleep(2000);
        driver.findElement(By.id("clave")).sendKeys("diy122020");
        Thread.sleep(2000);
        driver.findElement(By.id("entrar2")).click();
    }

    private void generateScrap(int startDay) throws InterruptedException {
        // GoTo Comercial
        int cont = 0;

        try {
            Thread.sleep(3000);

            driver.switchTo().frame(0);

            driver.switchTo().parentFrame();

        }
        catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());

            Thread.sleep(3000);

            driver.switchTo().parentFrame();

            driver.switchTo().frame(0);
        }


        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.id("Bar2"))).perform();

        Thread.sleep(3000);

        driver.findElement(By.id("menuItem223_6")).click();

        Thread.sleep(3000);

        long numberOfFiles = FilesHelper.getInstance().countFiles();
        boolean flag = true;

        while(flag) {
            try {
                driver.findElement(By.className("tablaDatos")).findElements(By.tagName("tbody")).get(0).findElements(By.tagName("tr")).get(0).findElements(By.tagName("td")).get(0).findElements(By.tagName("a")).get(0).click();

                Thread.sleep(5000);

                if(FilesHelper.getInstance().countFiles() > numberOfFiles) {
                   flag = false;
                }
            }
            catch(Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw e;
            }
        }
    }

}
