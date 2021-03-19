package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.CaptchaHelper;
import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.BusinessException;
import cl.ctl.scrapper.model.DateOutOfRangeException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class ConstrumartScrapper extends AbstractScrapper {

    public ConstrumartScrapper() throws IOException {
        super();
        holding = "Legrand";
        cadena = "Construmart";
        url = "https://sso.bbr.cl/auth/realms/construmart/protocol/openid-connect/auth?response_type=code&client_id=construmart-client-prod&redirect_uri=https%3A%2F%2Fb2b.construmart.cl%2FBBRe-commerce%2Fmain&state=5d08ee52-2336-4ed0-abc4-b431ee1e3a55&login=true&scope=openid";
    }

    public ConstrumartScrapper(LocalDate processDate) throws IOException {
        super();
    }

     void login() throws Exception {
        try {
            // *SolveCaptcha
            CaptchaHelper captchaHelper = new CaptchaHelper(driver, url);
            captchaHelper.solveCaptcha();
            Thread.sleep(2000);

            driver.findElement(By.id("username")).sendKeys("brenda.gimenez@legrand.cl");
            Thread.sleep(2000);
            driver.findElement(By.id("password")).sendKeys("diy012021");
            Thread.sleep(2000);
            driver.getPageSource();
            driver.findElement(By.id("kc-login")).click();

            Thread.sleep(5000);
            //Redirigir al home
            // https://b2b.construmart.cl/BBRe-commerce/main
            driver.get("https://b2b.construmart.cl/Construccion/BBRe-commerce/access/login.do");
        }
        catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
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
                    e.printStackTrace();
                    if(cont >= 10) {
                        logger.log(Level.SEVERE, e.getMessage());
                        throw e;
                    }
                }

            }

            Thread.sleep(3000);

            Actions actions;

            cont = 0;

            // *SelectParameters
            while(cont < 10) {

                cont++;

                try {

                    Thread.sleep(1000);

                    String script = "document.getElementsByClassName('v-textfield v-datefield-textfield')[0].removeAttribute('disabled')";
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript(script);

                    WebElement sinceInput = driver.findElement(By.xpath("//input[@class='v-textfield v-datefield-textfield']"));
                    sinceInput.clear();
                    js.executeScript(script);
                    sinceInput.sendKeys(since);

                    Thread.sleep(1000);

                    if(!driver.findElements(By.xpath("//div[@class='v-datefield v-datefield-popupcalendar v-widget v-has-width v-has-height v-datefield-error-error v-datefield-error v-datefield-day']")).isEmpty()) {
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
                        throw new DateOutOfRangeException(until);
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

            Thread.sleep(1000);

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

                    WebElement downloadReportButton = driver.findElement(By.xpath("//div[@class='v-button v-widget yesIcon v-button-yesIcon messageBoxIcon v-button-messageBoxIcon']"));
                    actions = new Actions(driver);
                    actions.moveToElement(downloadReportButton).click().build().perform();

                    Thread.sleep(30000);

                    WebElement downloadReportLink = driver.findElement(By.xpath("//div[@class='v-link v-widget']"));
                    actions = new Actions(driver);
                    actions.moveToElement(downloadReportLink).click().build().perform();

                    break;
                }
                catch (Throwable e) {
                    e.printStackTrace();
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

