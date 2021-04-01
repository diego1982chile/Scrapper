package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.DateOutOfRangeException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class WalMartScrapper extends AbstractScrapper {

    public WalMartScrapper() throws IOException {
        super();
        cadena = "WalMart";
        holding = "Nutrisa";
        url = "https://retaillink.login.wal-mart.com/?ServerType=IIS1&CTAuthMode=BASIC&language=en&utm_source=retaillink&utm_medium=redirect&utm_campaign=FalconRelease&CT_ORIG_URL=/&ct_orig_uri=/ ";
        fileExt = ".xlsx";
        onlyDiary = true;
    }

    void login() throws InterruptedException {

            driver.findElements(By.className("form-control__formControl___3uDUX")).get(0).sendKeys("sdellepiane@nutrisa.cl");
            Thread.sleep(2000);
            driver.findElements(By.className("form-control__formControl___3uDUX")).get(1).sendKeys("Nutrisa20.21");
            Thread.sleep(2000);
            driver.findElement(By.className("spin-button-children")).click();
            Thread.sleep(20000);
    }

    void doScrap(String since, String until) throws Exception {

        // GoTo Decision Support

        Actions actions;

        WebElement decisionSupport = driver.findElement(By.xpath(".//div[contains(text(),'Decision Support - New')]"));
        actions = new Actions(driver);
        actions.moveToElement(decisionSupport).click().build().perform();

        Thread.sleep(10000);

        if(!queryReport()) {
            submitNewReport();
            queryReport();
        }

    }

    private void submitNewReport() throws InterruptedException {

        Actions actions = new Actions(driver);

        //Si no hay reportes disponibles, submitiar uno nuevo
        ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(1));
        //driver.close();
        //driver.switchTo().window(tabs2.get(0));

        //Thread.sleep(5000);

        //driver.get("https://retaillink.wal-mart.com/decision_support/?ukey=W5741");

        Thread.sleep(5000);

        WebElement reports = driver.findElements(By.className("homepage_toplink")).get(2);
        actions = new Actions(driver);
        actions.moveToElement(reports).click().build().perform();

        Thread.sleep(5000);

        driver.switchTo().frame("ifrContent");

        Thread.sleep(5000);

        actions.moveToElement(driver.findElement(By.id("IMG27305673"))).click().build().perform();

        Thread.sleep(3000);

        WebElement nutrisaCTL = driver.findElement(By.id("CD39040995"));
        actions.contextClick(nutrisaCTL).perform();

        Thread.sleep(3000);

        actions.moveToElement(nutrisaCTL).build().perform();

        Thread.sleep(3000);

        driver.findElement(By.xpath(".//*[contains(text(),'Submitir')]")).click();

        Thread.sleep(10000);
    }

    private boolean queryReport() throws Exception {
        //Buscar si hay reportes disponibles para el proceso actual
        ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(1));

        Actions actions = new Actions(driver);

        Thread.sleep(5000);

        WebElement reports = driver.findElements(By.className("homepage_toplink")).get(1);
        actions = new Actions(driver);
        actions.moveToElement(reports).click().build().perform();

        Thread.sleep(5000);

        driver.switchTo().frame("ifrContent");

        Thread.sleep(5000);

        driver.switchTo().frame("JobTable");

        Thread.sleep(3000);

        String status = "";
        String date = "";

        boolean flag = true;

        while(flag) {
            try {
                int nutrisaCTLReports = driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).size();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
                String fecha = formatter.format(ProcessHelper.getInstance().getProcessDate().plusDays(1));

                if(nutrisaCTLReports == 0) {
                    logger.log(Level.WARNING, "Reporte 'Nutrisa CTL (No Modificar)' no está discponible. Se va a solicitar un nuevo reporte");
                    return false;
                }

                for (int i = 0; i < nutrisaCTLReports; ++i) {
                    status = driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).get(0).findElement(By.xpath("parent::td/preceding-sibling::td/following-sibling::td/following-sibling::td")).findElement(By.xpath("span")).getAttribute("innerHTML");
                    date = driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).get(0).findElement(By.xpath("parent::td/preceding-sibling::td/following-sibling::td/following-sibling::td/following-sibling::td/following-sibling::td")).findElement(By.xpath("span")).getAttribute("innerHTML");

                    switch(status) {
                        case "Hecho":
                        case "Salvados":
                            if(date.contains(fecha)) {
                                driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).get(i).click();
                                Thread.sleep(3000);
                                return true;
                            }
                            break;
                        case "Activo":
                            Thread.sleep(10000);
                        case "Esperando":
                            flag = false;
                            throw new Exception("Este reporte ha sido encolado para ser generado en la próxima ventana Operativa. Intentar nuevamente la descarga más tarde");
                        default:
                            flag = false;
                            throw new Exception("Status de Reporte '" + status + "' no soportado! Contacte al Administrador");
                    }

                }

                return false;
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        return false;
    }


}
