package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.LogHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 16-12-20.
 */
public class ConstrumartScrapperOld {

    WebDriver driver;
    private static  final String URL = "https://b2b.construmart.cl/Construccion/BBRe-commerce/access/login.do";
    LocalDate processDate  = LocalDate.now().minusDays(1);
    private static final String CADENA = "Construmart";

    Screen screen;

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(ConstrumartScrapperOld.class.getName());
    static LogHelper fh = LogHelper.getInstance();

    public ConstrumartScrapperOld() throws Exception {

        // This block configure the logger with handler and formatter
        try {

            logger.addHandler(fh);

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

        } catch (SecurityException e) {
            throw e;
        }

    }

    public void scrap() throws Exception {

        driver.get(URL);

        logger.log(Level.INFO, "Scrapper Construmart -> Login");

        // *Login
        try {
            login();
        }
        catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        Thread.sleep(2000);

        // Click Flash
        logger.log(Level.INFO, "Scrapper Construmart -> Click Flash");

        clickFlash();

        Thread.sleep(3000);

        // Allow Flash
        allowFlash();

        Thread.sleep(10000);

        // Generar Scrap Diario
        generateScrap(processDate.getDayOfMonth(), 1);
        FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "DAY");

        Thread.sleep( 10000);

        // Generar Scrap Mensual
        generateScrap(1, 2);
        FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "MONTH");

        // Si es proceso de Domingo
        // Generar Scrap Semanal
        if(processDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            generateScrap(processDate.minusDays(6).getDayOfMonth(), 3);
            FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "WEEK");
        }

        driver.quit();
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

    private void generateScrap(int startDay, int count) throws InterruptedException, FindFailed {

        screen = new Screen();

        String name =  ConstrumartScrapperOld.class.getCanonicalName();

        ImagePath.add(name + "/Construmart/Screenshots");

        Settings.MinSimilarity = 0.6;

        boolean flag = true;

        int cont = 0;

        // GoTo Comercial
        while(cont < 10) {
            try {
                cont++;

                screen.click("comercial");

                Thread.sleep(2000);

                screen.click("ventas");

                Thread.sleep(5000);

                screen.click("cerrar");

                break;

            }
            catch(Exception e) {
                if(cont >= 10) {
                    throw e;
                }
            }

        }

        Thread.sleep(10000);

        Settings.MinSimilarity = 0.8;

        cont = 0;

        // Select parameters
        while(cont < 10) {
            try {
                cont++;

                screen.click("solo_productos_activos");

                Thread.sleep(2000);

                screen.click("excluir_productos_sin_ventas");

                Thread.sleep(2000);

                screen.click("desde");

                Thread.sleep(2000);

                screen.click(String.valueOf(startDay));

                Thread.sleep(2000);

                screen.click("generar_informe");

                break;


            }
            catch(Exception e) {
                if(cont >= 10) {
                    throw e;
                }
            }

        }

        Thread.sleep(10000);

        cont = 0;

        // Download file
        while(cont < 10) {
            try {
                cont++;

                screen.click("descargar_reporte");

                Thread.sleep(10000);

                screen.click("si");

                Thread.sleep(10000);

                screen.click("guardar");

                Thread.sleep(10000);

                screen.type(Key.HOME);

                Thread.sleep(5000);

                screen.type(FilesHelper.getInstance().getDownloadPath());

                Thread.sleep(5000);

                screen.type(Key.ENTER);

                Thread.sleep(5000);

                screen.click("cerrar_descarga");

                Thread.sleep(5000);

                screen.click("cerrar_ventas");

                break;

            }
            catch(Exception e) {
                if(cont >= 10) {
                    throw e;
                }
            }

        }

    }

}
