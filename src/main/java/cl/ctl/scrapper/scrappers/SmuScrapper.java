package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.CaptchaHelper;
import cl.ctl.scrapper.helpers.ConfigHelper;
import cl.ctl.scrapper.model.exceptions.BadDateException;
import cl.ctl.scrapper.model.exceptions.DateOutOfRangeException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class SmuScrapper extends AbstractScrapper {

    boolean flag = false;
    boolean dateOutOfRangeFlag = false;

    public SmuScrapper() throws IOException {
        super();
        holding = "Nutrisa";
        cadena = "SMU";
        url = "https://sso.bbr.cl/auth/realms/unimarc/protocol/openid-connect/auth?response_type=code&client_id=unimarc-client-prod&redirect_uri=https%3A%2F%2Fb2b.smu.cl%2FBBRe-commerce%2Fmain&state=175f2d2f-36ee-4575-aae0-28075fd437ab&login=true&scope=openid";
        logo = "smu.jpg";
        fileExt = ".xlsx";
    }

    public SmuScrapper(String holding) throws IOException {
        this();
        this.holding = holding;
    }

    void login() throws Exception {

        try {
            // *SolveCaptcha
            CaptchaHelper captchaHelper = new CaptchaHelper(driver, url);
            captchaHelper.solveCaptcha();
            Thread.sleep(2000);

            String holding = getHolding().toLowerCase();
            driver.findElement(By.id("username")).sendKeys(ConfigHelper.getInstance().CONFIG.get(holding + ".smu.user"));
            driver.findElement(By.id("password")).sendKeys(ConfigHelper.getInstance().CONFIG.get(holding + ".smu.password"));
            driver.getPageSource();
            driver.findElement(By.id("kc-login")).click();

            Thread.sleep(5000);

            driver.get("https://b2b.smu.cl/BBRe-commerce/main");

            Thread.sleep(10000);

            Actions actions;
            //Thread.sleep(5000);

        }
        catch(Throwable e) {
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

    void doScrap(String since, String until) throws Exception {

        // GoTo Comercial
        int cont = 0;

        while(cont < 10) {

            cont++;

            try {

                // Cerrar popups!!! (si es la 1a vez)
                if(!driver.findElements(By.xpath("//div[@class='v-window-closebox']")).isEmpty()) {

                    for(int i = driver.findElements(By.xpath("//div[@class='v-window-closebox']")).size(); i > 0; --i ) {
                        driver.findElements(By.xpath("//div[@class='v-window-closebox']")).get(i-1).click();
                        Thread.sleep(2000);
                    }

                    flag = true;
                }

                WebElement menuCommerce = driver.findElement(By.xpath("//div[@class='v-menubar v-widget mainMenuBar v-menubar-mainMenuBar v-has-width']")).findElements(By.cssSelector("span:nth-child(3)")).get(0);
                WebDriverWait wait = new WebDriverWait(driver, 10);
                wait.until(ExpectedConditions.elementToBeClickable(menuCommerce));

                Thread.sleep(2000);

                menuCommerce.click();

                Thread.sleep(2000);

                WebElement submenuCommerce = driver.findElement(By.xpath("//div[@class='v-menubar-submenu v-widget mainMenuBar v-menubar-submenu-mainMenuBar v-has-width']")).findElements(By.cssSelector("span:nth-child(2)")).get(0);
                wait = new WebDriverWait(driver, 10);
                wait.until(ExpectedConditions.elementToBeClickable(submenuCommerce));

                Thread.sleep(2000);

                submenuCommerce.click();

                break;
            }
            catch(Throwable e) {
                if(cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
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
                /*
                WebElement checkDisplayStock = driver.findElement(By.xpath(".//*[contains(text(),'Mostrar inventario al')]/parent::div/preceding-sibling::div"));
                actions = new Actions(driver);
                actions.moveToElement(checkDisplayStock).click().build().perform();

                Thread.sleep(1000);
                */

                WebElement excludeProductsWithoutStock = driver.findElement(By.xpath(".//div[contains(text(),'Excluir productos sin ventas')]/parent::div/parent::div/parent::div/preceding-sibling::div/preceding-sibling::div/child::span"));
                actions = new Actions(driver);
                actions.moveToElement(excludeProductsWithoutStock).click().build().perform();

                Thread.sleep(1000);

                break;
            }
            catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.WARNING, e.getMessage());
                if(cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }
        }


        cont = 0;

        // *SelectParameters
        while(cont < 20) {

            cont++;

            try {

                if(dateOutOfRangeFlag) {
                    throw new DateOutOfRangeException(since);
                }

                String script = "document.getElementsByClassName('v-textfield v-datefield-textfield')[0].removeAttribute('disabled')";
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(script);

                WebElement sinceInput = driver.findElement(By.xpath("//input[@class='v-textfield v-datefield-textfield']"));
                sinceInput.clear();
                js.executeScript(script);
                sinceInput.sendKeys(since);

                Thread.sleep(1000);

                if(!driver.findElements(By.xpath("//div[@class='v-datefield v-datefield-popupcalendar v-widget v-has-width v-has-height v-datefield-error-error v-datefield-error v-datefield-day']")).isEmpty()) {
                    dateOutOfRangeFlag = true;
                    throw new DateOutOfRangeException("La Fecha está fuera del rango permitido");
                }

                script = "document.getElementsByClassName('v-textfield v-datefield-textfield')[1].removeAttribute('disabled')";
                js = (JavascriptExecutor) driver;
                js.executeScript(script);

                WebElement untilInput = driver.findElement(By.xpath("//input[@class='v-textfield v-datefield-textfield']"));
                untilInput.clear();
                js.executeScript(script);
                untilInput.sendKeys(until);

                Thread.sleep(1000);

                if(!driver.findElements(By.xpath("//div[@class='v-datefield v-datefield-popupcalendar v-widget v-has-width v-has-height v-datefield-error-error v-datefield-error v-datefield-day']")).isEmpty()) {
                    dateOutOfRangeFlag = true;
                    throw new DateOutOfRangeException("La Fecha está fuera del rango permitido");
                }

                if(sinceInput.getAttribute("value").trim().equals("") || untilInput.getAttribute("value").trim().equals("")) {
                    throw new Exception("Alguna de las fechas está vacía!!");
                }

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

        Thread.sleep(10000);

        // GenerateFile

        cont = 0;

        while(true) {

            cont++;

            try {
                WebElement generateReport = driver.findElement(By.xpath("//div[@class='v-button v-widget btn-filter-search v-button-btn-filter-search']"));
                actions = new Actions(driver);
                actions.moveToElement(generateReport).click().build().perform();

                Thread.sleep(2000);

                // Si se producen errores de fecha levantar excepción
                if(!driver.findElements(By.xpath(".//div[contains(text(),'Ingrese una fecha Desde válida')]")).isEmpty() ||
                        !driver.findElements(By.xpath(".//div[contains(text(),'Ingrese una fecha Hasta válida')]")).isEmpty()) {
                    throw new BadDateException("Alguna de las fechas ingresadas no es válida");
                }

                Thread.sleep(40000);

                WebElement downloadReportMenu = driver.findElement(By.xpath("//div[@class='v-button v-widget toolbar-button v-button-toolbar-button bbr-popupbutton']"));
                actions = new Actions(driver);
                actions.moveToElement(downloadReportMenu).click().build().perform();

                Thread.sleep(2000);

                WebElement downloadReportOption = driver.findElement(By.xpath("//div[@class='v-verticallayout v-layout v-vertical v-widget v-has-width v-margin-right v-margin-left']")).findElements(By.cssSelector("div:nth-child(2)")).get(0);
                actions = new Actions(driver);
                actions.moveToElement(downloadReportOption).click().build().perform();

                Thread.sleep(2000);

                /*
                WebElement excludeProductsWithoutStock = driver.findElement(By.xpath(".//label[contains(text(),'Archivo de Texto (csv)')]"));
                actions = new Actions(driver);
                actions.moveToElement(excludeProductsWithoutStock).click().build().perform();

                Thread.sleep(2000);
                */

                WebElement downloadReportButton = driver.findElement(By.xpath("//div[@class='v-button v-widget primary v-button-primary btn-generic v-button-btn-generic v-has-width']"));
                actions = new Actions(driver);
                actions.moveToElement(downloadReportButton).click().build().perform();

                Thread.sleep(90000);

                WebElement downloadReportLink = driver.findElement(By.xpath("//div[@class='v-horizontallayout v-layout v-horizontal v-widget']")).findElements(By.xpath("//div[@class='v-slot']")).get(0).findElements(By.xpath("//div[@class='v-link v-widget']")).get(0);
                actions = new Actions(driver);
                actions.moveToElement(downloadReportLink).click().build().perform();

                break;
            }
            catch (Throwable e) {
                e.printStackTrace();
                logger.log(Level.WARNING, e.getMessage());
                if(cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }
        }

        Thread.sleep(2000);
        // Cerrar Tab
        closeTab();
    }
}
