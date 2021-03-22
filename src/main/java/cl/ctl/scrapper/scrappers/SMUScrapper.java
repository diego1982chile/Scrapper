package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.CaptchaHelper;
import cl.ctl.scrapper.model.DateOutOfRangeException;
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
public class SMUScrapper extends AbstractScrapper {

    public SMUScrapper() throws IOException {
        super();
        holding = "Nutrisa";
        cadena = "SMU";
        url = "https://sso.bbr.cl/auth/realms/unimarc/protocol/openid-connect/auth?response_type=code&client_id=unimarc-client-prod&redirect_uri=https%3A%2F%2Fb2b.smu.cl%2FBBRe-commerce%2Fmain&state=175f2d2f-36ee-4575-aae0-28075fd437ab&login=true&scope=openid";
    }

    void login() throws Exception {
        try {
            // *SolveCaptcha
            CaptchaHelper captchaHelper = new CaptchaHelper(driver, url);
            captchaHelper.solveCaptcha();
            Thread.sleep(2000);
            driver.findElement(By.id("username")).sendKeys("proyectos@nutrisa.cl");
            driver.findElement(By.id("password")).sendKeys("Nutrisa.2021");
            driver.getPageSource();
            driver.findElement(By.id("kc-login")).click();

            Thread.sleep(5000);

            driver.get("https://b2b.smu.cl/BBRe-commerce/main");

            Thread.sleep(10000);

            Actions actions;

            driver.findElements(By.xpath("//div[@class='v-window-closebox']")).get(2).click();

            Thread.sleep(2000);

            driver.findElements(By.xpath("//div[@class='v-window-closebox']")).get(1).click();

            Thread.sleep(2000);

            driver.findElements(By.xpath("//div[@class='v-window-closebox']")).get(0).click();

            Thread.sleep(2000);

            //Thread.sleep(5000);

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

                WebElement submenuCommerce = driver.findElement(By.xpath("//div[@class='v-menubar-submenu v-widget mainMenuBar v-menubar-submenu-mainMenuBar v-has-width']")).findElements(By.cssSelector("span:nth-child(2)")).get(0);
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
        while(cont < 10) {

            cont++;

            try {
                String script = "document.getElementsByClassName('v-textfield v-datefield-textfield')[0].removeAttribute('disabled')";
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(script);

                WebElement sinceInput = driver.findElement(By.xpath("//input[@class='v-textfield v-datefield-textfield']"));
                sinceInput.clear();
                js.executeScript(script);
                sinceInput.sendKeys(since);

                Thread.sleep(1000);

                if(!driver.findElements(By.xpath("//div[@class='v-datefield v-datefield-popupcalendar v-widget v-has-width v-has-height v-datefield-error-error v-datefield-error v-datefield-day']")).isEmpty()) {
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
                    throw new DateOutOfRangeException("La Fecha está fuera del rango permitido");
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

                Thread.sleep(60000);

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
