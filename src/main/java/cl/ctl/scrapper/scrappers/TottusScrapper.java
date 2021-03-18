package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.DateOutOfRangeException;
import org.openqa.selenium.By;
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
        url = "https://b2b.tottus.com/b2btoclpr/grafica/html/index.html";
        onlyDiary = true;
        fileExt = ".txt";
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

        driver.findElement(By.id("menuItem223_5")).click();

        Thread.sleep(3000);

        long numberOfFiles = FilesHelper.getInstance().countFiles();
        boolean flag = true;

        DateTimeFormatter formatter;

        String fecha = null;

        while(flag) {
            try {
                //driver.findElement(By.className("tablaDatos")).findElements(By.tagName("tbody")).get(0).findElements(By.tagName("tr")).get(0).findElements(By.tagName("td")).get(0).findElements(By.tagName("a")).get(0).click();

                formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

                fecha = formatter.format(ProcessHelper.getInstance().getProcessDate().plusDays(1));

                driver.findElement(By.xpath("//a[contains(text(), '" + fecha + "')]")).click();

                Thread.sleep(5000);

                if(FilesHelper.getInstance().countFiles() > numberOfFiles) {
                    flag = false;
                }
            }
            catch(Throwable e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new DateOutOfRangeException(fecha);
            }
        }

    }

}
