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

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Created by des01c7 on 16-12-20.
 */
public class EasyScrapper {

    WebDriver driver;
    private static  final String URL = "https://www.cenconlineb2b.com/auth/realms/cencosud/protocol/openid-connect/auth?response_type=code&client_id=easycl-client-prod&redirect_uri=https%3A%2F%2Fwww.cenconlineb2b.com%2FEasyCL%2FBBRe-commerce%2Fswf%2Fmain.html&state=bad15b30-d2d2-4738-8409-ffaad6602ac6&login=true&scope=openid";
    LocalDate processDate  = LocalDate.now().minusDays(1);
    private static final String CADENA = "Easy";

    public EasyScrapper() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chrome_options = new ChromeOptions();
        chrome_options.addArguments("--start-maximized");
        //chrome_options.addArguments("--headless");
        chrome_options.addArguments("--no-sandbox");
        chrome_options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(chrome_options);
    }

    public void scrap() throws Exception {

        driver.get(URL);

        FilesHelper filesHelper = new FilesHelper();

        // *SolveCaptcha
        CaptchaSolver captchaSolver = new CaptchaSolver(driver, URL);
        captchaSolver.solveCaptcha();

        Thread.sleep(2000);

        // *Login
        login();

        Thread.sleep(2000);

        // Redirect Home
        redirectHome();

        Thread.sleep(2000);

        // Select country
        selectCountry();

        Thread.sleep(2000);

        // Generar Scrap Diario
        generateScrap(processDate.getDayOfMonth(), 1);
        filesHelper.renameLastDownloadedFile(CADENA, "DAY");

        Thread.sleep(2000);

        // Cerrar Tab
        closeTab();

        Thread.sleep(2000);

        // Generar Scrap Mensual
        generateScrap(1, 2);
        filesHelper.renameLastDownloadedFile(CADENA, "MONTH");

        // Si es proceso de Domingo
        // Generar Scrap Semanal
        if(processDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            generateScrap(processDate.minusDays(7).getDayOfMonth(), 3);
            filesHelper.renameLastDownloadedFile(CADENA, "WEEK");
        }

        // PROCESAMIENTO ARCHIVOS DESCARGADOS
        filesHelper.processFiles();

        // Descomprimir archivos descargados

        // Renombrar archivos dentro de las carpetas descomprimidas

        // Mover archivos a carpeta padre

        // Subir archivos a servidor
    }

    private void login() {
        driver.findElement(By.id("username")).sendKeys("michel.lotissier@legrand.cl");
        driver.findElement(By.id("password")).sendKeys("diy12easy2020");
        driver.getPageSource();
        driver.findElement(By.id("kc-login")).click();
    }

    private void redirectHome() {
        driver.get("https://www.cenconlineb2b.com/");
    }

    private void selectCountry() throws InterruptedException {
        Select pais = new Select(driver.findElement(By.id("pais")));
        Thread.sleep(3000);
        pais.selectByValue("chi");
        Thread.sleep(2000);
        driver.findElement(By.id("btnIngresar")).click();
    }

    private void closeTab() throws InterruptedException {
        driver.findElement(By.xpath("//span[@class='v-tabsheet-caption-close']")).click();

        Thread.sleep(5000);
    }

    private void generateScrap(int startDay, int count) throws InterruptedException {
        // GoTo Comercial
        while(true) {
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
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        Thread.sleep(3000);

        Actions actions;

        String check1Id = "";
        String check2Id = "";

        switch(count) {
            case 1:
                check1Id = "gwt-uid-8";
                check2Id = "gwt-uid-9";
                break;
            case 2:
                check1Id = "gwt-uid-25";
                check2Id = "gwt-uid-26";
                break;
            case 3:
                check1Id = "gwt-uid-37";
                check2Id = "gwt-uid-38";
                break;
        }

        // *SelectParameters
        while(true) {
            try {
                WebElement checkDisplayStock = driver.findElement(By.xpath("//input[@id='" + check1Id + "']"));
                actions = new Actions(driver);
                actions.moveToElement(checkDisplayStock).click().build().perform();

                Thread.sleep(1000);

                WebElement excludeProductsWithoutStock = driver.findElement(By.xpath("//input[@id='" + check2Id + "']"));
                actions = new Actions(driver);
                actions.moveToElement(excludeProductsWithoutStock).click().build().perform();

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
                System.out.println(e.getMessage());
            }

        }

        Thread.sleep(1000);

        // GenerateFile

        while(true) {
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
                System.out.println(e.getMessage());
            }

        }
    }
}
