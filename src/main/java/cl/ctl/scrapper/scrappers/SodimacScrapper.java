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
public class SodimacScrapper {

    WebDriver driver;
    private static  final String URL = "https://b2b.sodimac.com/b2bsocopr/grafica/html/index.html";
    LocalDate processDate  = LocalDate.now().minusDays(1);
    private static final String CADENA = "Sodimac";

    public SodimacScrapper() {
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

        // *Login
        login();

        Thread.sleep(5000);

        // Generar Scrap Diario
        generateScrap(processDate.getDayOfMonth());
        filesHelper.renameLastDownloadedFile(CADENA, "DAY");

        Thread.sleep(2000);

        driver.quit();
    }

    private void login() throws InterruptedException {

        driver.switchTo().frame(0);

        Select b2b = new Select(driver.findElement(By.id("CADENA")));
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
        driver.switchTo().frame(0);

        Thread.sleep(3000);

        driver.findElement(By.id("Bar2"));

        Thread.sleep(3000);
    }

}
