package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by des01c7 on 16-12-20.
 */
public class ConstrumartScrapper {

    WebDriver driver;
    private static  final String URL = "https://b2b.construmart.cl/Construccion/BBRe-commerce/access/login.do";
    LocalDate processDate  = LocalDate.now().minusDays(1);
    private static final String CADENA = "Construmart";

    public ConstrumartScrapper() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions chrome_options = new ChromeOptions();
        chrome_options.addArguments("--start-maximized");
        //chrome_options.addArguments("--headless");
        chrome_options.addArguments("--no-sandbox");
        chrome_options.addArguments("--disable-dev-shm-usage");

        // disable ephemeral flash permissions flag
        chrome_options.addArguments("--disable-features=EnableEphemeralFlashPermission");
        Map<String, Object> prefs = new HashMap<String, Object>();
        // Enable flash for all sites for Chrome 69
        prefs.put("profile.content_settings.exceptions.plugins.*,*.setting", 1);
        prefs.put("profile.default_content_setting_values.plugins", 1);
        prefs.put("profile.content_settings.plugin_whitelist.adobe-flash-player", 1);
        prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.adobe-flash-player", 1);

        chrome_options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(chrome_options);

        // Step one visit the site you want to activate flash player
        driver.get("https://helpx.adobe.com/flash-player.html");

        // Step 2  Once your page is loaded in chrome, go to the URL where lock sign is there visit the
        // setting page where you will see that the flash is disabled.

        // step 3 copy that link and paste below
        driver.get("chrome://settings/content/siteDetails?site=https%3A%2F%2Fhelpx.adobe.com");

        // below code is for you to reach to flash dialog box and change it to allow from block.
        Actions actions = new Actions(driver);
        for(int i = 0; i < 21; ++i) {
            actions = actions.sendKeys(Keys.TAB);
        }
        actions = actions.sendKeys(Keys.ARROW_DOWN);
        actions.perform();

        driver.get("chrome://settings/content/flash");

        actions = new Actions(driver);
        for(int i = 0; i < 14; ++i) {
            actions = actions.sendKeys(Keys.TAB);
        }
        actions = actions.sendKeys(Keys.ENTER);
        actions.perform();

        // This Step will bring you back to your original page where you want to load the flash
        //driver.navigate();
    }

    public void scrap() throws Exception {
        driver.get(URL);

        FilesHelper filesHelper = new FilesHelper();

        // *Login
        login();

        Thread.sleep(2000);

        // Click Flash
        clickFlash();

        Thread.sleep(3000);

        // Allow Flash
        allowFlash();

        Thread.sleep(10000);

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
        driver.findElement(By.id("logid")).sendKeys("139843827");
        driver.findElement(By.id("password")).sendKeys("Inicio4*");
        driver.getPageSource();
        driver.findElement(By.id("btnIngresar")).click();
    }

    private void clickFlash() {
        driver.findElement(By.xpath("//a")).click();
    }

    private void allowFlash() throws InterruptedException, AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        Thread.sleep(2000);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        Thread.sleep(2000);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        Thread.sleep(2000);
        if (SystemUtils.IS_OS_LINUX) {
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(2000);
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
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
