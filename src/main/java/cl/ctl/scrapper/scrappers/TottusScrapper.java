package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.DateOutOfRangeException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class TottusScrapper extends AbstractScrapper {

    public TottusScrapper() throws IOException {
        super();
        cadena = "Tottus";
        holding = "Nutrisa";
        url = "https://b2b.tottus.com/b2btoclpr/grafica/html/index.html";
    }

    void login() throws InterruptedException {

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
            b2b.selectByValue("8");
            Thread.sleep(2000);

            driver.findElement(By.id("empresa")).sendKeys("952140000 ");
            Thread.sleep(2000);
            driver.findElement(By.id("usuario")).sendKeys("100777517");
            Thread.sleep(2000);
            //driver.findElement(By.id("clave")).sendKeys("diy012021");
            driver.findElement(By.id("clave")).sendKeys("Nutrisa21.21 ");
            Thread.sleep(2000);
            driver.findElement(By.id("entrar2")).click();
    }

    void doScrap(String since, String until) throws Exception {

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

        driver.findElement(By.id("menuItem226_7")).click();

        Thread.sleep(3000);


        cont = 0;

        while(cont < 10) {

            cont++;

            try {

                WebElement checkStock = driver.findElement(By.xpath("//input[@id='chkstock']"));
                actions = new Actions(driver);
                actions.moveToElement(checkStock).click().build().perform();

                Thread.sleep(2000);

                WebElement checkActivo = driver.findElement(By.xpath("//input[@id='chksactivo']"));
                actions = new Actions(driver);
                actions.moveToElement(checkActivo).click().build().perform();

                Thread.sleep(2000);

                since = since.replace("-","/");

                WebElement sinceInput = driver.findElement(By.xpath("//input[@id='desde']"));
                sinceInput.clear();
                sinceInput.sendKeys(since);

                Thread.sleep(1000);

                if(!driver.findElements(By.xpath("//div[@class='v-datefield v-datefield-popupcalendar v-widget v-has-width v-has-height v-datefield-error-error v-datefield-error v-datefield-day']")).isEmpty()) {
                    throw new DateOutOfRangeException(since);
                }

                until = until.replace("-","/");

                WebElement untilInput = driver.findElement(By.xpath("//input[@id='hasta']"));
                untilInput.clear();
                untilInput.sendKeys(until);

                Thread.sleep(1000);

                if(!driver.findElements(By.xpath("//div[@class='v-datefield v-datefield-popupcalendar v-widget v-has-width v-has-height v-datefield-error-error v-datefield-error v-datefield-day']")).isEmpty()) {
                    throw new DateOutOfRangeException(until);
                }

                Thread.sleep(1000);

                WebElement generateReport = driver.findElement(By.xpath("//input[@name='botonBuscar']"));
                actions = new Actions(driver);
                actions.moveToElement(generateReport).click().build().perform();

                Thread.sleep(2000);

                break;

            }
            catch (Throwable e) {
                e.printStackTrace();

                if(e instanceof DateOutOfRangeException) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }

                if(cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }
        }

    }

}
