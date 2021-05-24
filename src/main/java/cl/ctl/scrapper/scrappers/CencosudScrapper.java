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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class CencosudScrapper extends AbstractScrapper {

    boolean dateOutOfRangeFlag = false;

    public CencosudScrapper() throws IOException {
        super();
        holding = "Nutrisa";
        cadena = "Cencosud";
        url = "https://www.cenconlineb2b.com/";
        logo = "cencosud.png";

        readyOnMorning = false;
    }

    void login() throws Exception {

        try {

            // Select country
            selectCountry();

            // *SolveCaptcha
            CaptchaHelper captchaHelper = new CaptchaHelper(driver, url);
            captchaHelper.solveCaptcha();
            Thread.sleep(2000);
            driver.findElement(By.id("username")).sendKeys(ConfigHelper.getInstance().CONFIG.get("scrappers.cencosud.user"));
            driver.findElement(By.id("password")).sendKeys(ConfigHelper.getInstance().CONFIG.get("scrappers.cencosud.password"));
            driver.getPageSource();
            driver.findElement(By.id("kc-login")).click();

            Thread.sleep(2000);

            // Redirect Home
            //redirectHome();

            //Thread.sleep(5000);

            //driver.findElement(By.xpath("//div[@class='v-window-closebox']")).click();

            //Thread.sleep(5000);

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
            Select uNegocio = new Select(driver.findElement(By.id("unidad_negocio")));
            Thread.sleep(3000);
            uNegocio.selectByValue("/SuperCL/BBRe-commerce/main");
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

    void doScrap(String since, String until) throws Exception {

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
                if(cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }

        }

        Thread.sleep(3000);

        Actions actions;

        // *SelectParameters

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
                    throw new DateOutOfRangeException(since);
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
                    throw new DateOutOfRangeException(until);
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

                Thread.sleep(20000);

                WebElement downloadReportMenu = driver.findElement(By.xpath("//div[@class='v-button v-widget toolbar-button v-button-toolbar-button bbr-popupbutton']"));
                actions = new Actions(driver);
                actions.moveToElement(downloadReportMenu).click().build().perform();

                Thread.sleep(2000);

                WebElement downloadReportOption = driver.findElement(By.xpath("//div[@class='v-verticallayout v-layout v-vertical v-widget v-has-width v-margin-right v-margin-left']")).findElements(By.cssSelector("div:nth-child(2)")).get(0);
                actions = new Actions(driver);
                actions.moveToElement(downloadReportOption).click().build().perform();

                Thread.sleep(2000);

                WebElement downloadReportButton = driver.findElement(By.xpath("//div[@class='v-button v-widget yesIcon v-button-yesIcon messageBoxIcon v-button-messageBoxIcon v-has-width']"));

                actions = new Actions(driver);
                actions.moveToElement(downloadReportButton).click().build().perform();

                Thread.sleep(30000);

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
