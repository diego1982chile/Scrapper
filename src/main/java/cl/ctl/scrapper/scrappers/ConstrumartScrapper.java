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
public class ConstrumartScrapper {

    WebDriver driver;
    private static  final String URL = "https://sso.bbr.cl/auth/realms/construmart/protocol/openid-connect/auth?response_type=code&client_id=construmart-client-prod&redirect_uri=https%3A%2F%2Fb2b.construmart.cl%2FBBRe-commerce%2Fmain&state=5d08ee52-2336-4ed0-abc4-b431ee1e3a55&login=true&scope=openid";
    LocalDate processDate  = LocalDate.now().minusDays(1);
    private static final String CADENA = "Easy";

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(ConstrumartScrapper.class.getName());
    FileHandler fh;

    public ConstrumartScrapper() throws IOException {

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
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void scrap() throws Exception {

        driver.get(URL);

        // *SolveCaptcha
        CaptchaSolver captchaSolver = new CaptchaSolver(driver, URL);
        captchaSolver.solveCaptcha();

        Thread.sleep(2000);

        // *Login
        login();

        Thread.sleep(2000);

        // Generar Scrap Diario
        generateScrap(processDate.getDayOfMonth(), 1);
        Thread.sleep(5000);
        FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "DAY");

        Thread.sleep(2000);

        // Cerrar Tab
        closeTab();

        Thread.sleep(2000);

        // Generar Scrap Mensual
        generateScrap(1, 2);
        Thread.sleep(5000);
        FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "MONTH");

        // Si es proceso de Domingo
        // Generar Scrap Semanal
        if(processDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            generateScrap(processDate.minusDays(6).getDayOfMonth(), 3);
            Thread.sleep(5000);
            FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "WEEK");
        }

        driver.quit();
    }

    private void login() throws InterruptedException {
        try {
            driver.findElement(By.id("username")).sendKeys("brenda.gimenez@legrand.cl");
            Thread.sleep(2000);
            driver.findElement(By.id("password")).sendKeys("diy012021");
            Thread.sleep(2000);
            driver.getPageSource();
            driver.findElement(By.id("kc-login")).click();
        }
        catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }
    }

    private void redirectHome() {
        try {
            driver.get("https://www.cenconlineb2b.com/");
        }
        catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }
    }

    private void selectCountry() throws InterruptedException {
        try {
            Select pais = new Select(driver.findElement(By.id("pais")));
            Thread.sleep(3000);
            pais.selectByValue("chi");
            Thread.sleep(2000);
            driver.findElement(By.id("btnIngresar")).click();
        }
        catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }
    }

    private void closeTab() throws InterruptedException {
        try {
            driver.findElement(By.xpath("//span[@class='v-tabsheet-caption-close']")).click();
            Thread.sleep(5000);
        }
        catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }
    }

    private void generateScrap(int startDay, int count) throws InterruptedException {
        // GoTo Comercial
        int cont = 0;

        while(cont < 10) {

            cont++;

            try {
                WebElement menuCommerce = driver.findElement(By.xpath("//div[@class='v-menubar v-widget mainMenuBar v-menubar-mainMenuBar v-has-width']")).findElements(By.cssSelector("span:nth-child(3)")).get(0);
                WebDriverWait wait = new WebDriverWait(driver, 10);
                wait.until(ExpectedConditions.elementToBeClickable(menuCommerce));

                Thread.sleep(2000);

                menuCommerce.click();

                Thread.sleep(2000);

                WebElement submenuCommerce = driver.findElement(By.xpath("//div[@class='v-menubar-submenu v-widget mainMenuBar v-menubar-submenu-mainMenuBar v-has-width']")).findElements(By.cssSelector("span:nth-child(1)")).get(0);
                wait = new WebDriverWait(driver, 10);
                wait.until(ExpectedConditions.elementToBeClickable(submenuCommerce));

                Thread.sleep(2000);

                submenuCommerce.click();

                break;
            }
            catch(Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
                if(cont >= 10) {
                    throw e;
                }
            }

        }

        Thread.sleep(3000);

        Actions actions;

        // *SelectParameters
        while(cont < 10) {

            cont++;

            try {

                Thread.sleep(1000);

                WebElement sinceCalendar = driver.findElement(By.xpath("//button[@class='v-datefield-button']"));

                sinceCalendar.click();

                Thread.sleep(1000);

                WebElement day = driver.findElement(By.xpath("//span[text()='" + startDay + "']"));
                actions = new Actions(driver);
                actions.moveToElement(day).click().build().perform();

                break;
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
                if(cont >= 10) {
                    throw e;
                }
            }

        }

        Thread.sleep(1000);

        // GenerateFile

        cont = 0;

        while(true) {

            cont++;

            try {
                WebElement generateReport = driver.findElement(By.xpath("//div[@class='v-button v-widget btn-filter-search v-button-btn-filter-search']"));
                actions = new Actions(driver);
                actions.moveToElement(generateReport).click().build().perform();

                Thread.sleep(20000);

                WebElement downloadReportMenu = driver.findElement(By.xpath("//div[@class='v-button v-widget toolbar-button v-button-toolbar-button bbr-popupbutton']"));
                actions = new Actions(driver);
                actions.moveToElement(downloadReportMenu).click().build().perform();

                Thread.sleep(2000);

                WebElement downloadReportOption = driver.findElement(By.xpath("//div[@class='v-verticallayout v-layout v-vertical v-widget v-has-width v-margin-right v-margin-left']")).findElements(By.cssSelector("div:nth-child(2)")).get(0);
                actions = new Actions(driver);
                actions.moveToElement(downloadReportOption).click().build().perform();

                Thread.sleep(2000);

                WebElement downloadReportButton = driver.findElement(By.xpath("//div[@class='v-button v-widget primary v-button-primary btn-generic v-button-btn-generic v-has-width']"));
                actions = new Actions(driver);
                actions.moveToElement(downloadReportButton).click().build().perform();

                Thread.sleep(30000);

                WebElement downloadReportLink = driver.findElement(By.xpath("//div[@class='v-horizontallayout v-layout v-horizontal v-widget']")).findElements(By.xpath("//div[@class='v-slot']")).get(0).findElements(By.xpath("//div[@class='v-link v-widget']")).get(0);
                actions = new Actions(driver);
                actions.moveToElement(downloadReportLink).click().build().perform();

                break;
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
                if(cont >= 10) {
                    throw e;
                }
            }

        }
    }
}
