package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.BusinessException;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class SodimacScrapper extends AbstractScrapper {

    private static  final String URL = "https://b2b.sodimac.com/b2bsocopr/grafica/html/index.html";

    public SodimacScrapper() throws IOException {
        super();
        CADENA = "Sodimac";
    }

    public void scrap() throws Exception {

        try {
            checkScraps();

            driver.get(URL);

            // *Login
            login();

            Thread.sleep(5000);

            // Generar Scrap Diario
            generateScrap(ProcessHelper.getInstance().getProcessDate().getDayOfMonth());
            FilesHelper.getInstance().renameLastDownloadedFile(CADENA, "DAY");

            Thread.sleep(2000);

            driver.quit();
        }
        catch(BusinessException e) {
            logger.log(Level.WARNING, e.getMessage());
        }

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
        driver.findElement(By.id("clave")).sendKeys("diy012021");
        Thread.sleep(2000);
        driver.findElement(By.id("entrar2")).click();
    }

    private void generateScrap(int startDay) throws InterruptedException {

        try {
            checkScrap(1);

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
                    //driver.findElement(By.className("tablaDatos")).findElements(By.tagName("tbody")).get(0).findElements(By.tagName("tr")).get(0).findElements(By.tagName("td")).get(0).findElements(By.tagName("a")).get(0).click();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

                    String fecha = formatter.format(ProcessHelper.getInstance().getProcessDate().plusDays(1));

                    driver.findElement(By.xpath("//a[contains(text(), '" + fecha + "')]")).click();

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
        catch (BusinessException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

}
