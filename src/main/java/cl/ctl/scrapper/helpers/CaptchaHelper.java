package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by des01c7 on 17-12-20.
 */
public class CaptchaHelper {

    /** Logger para la clase */
    Logger logger = Logger.getLogger(ConstrumartScrapper.class.getName());
    LogHelper fh = LogHelper.getInstance();

    WebDriver driver;
    WebClient client;

    final String API_BASE_URL = "http://2captcha.com/" ;
    //public static final String API_KEY = "78dba8221a8530a80ab8ac1b676389ad";
    public static final String API_KEY = ConfigHelper.getInstance().CONFIG.get("captcha.api_key");

    String BASE_URL;

    public CaptchaHelper(WebDriver driver, String url) throws GeneralSecurityException {
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.addHandler(fh);
        this.driver = driver;
        this.BASE_URL = url;
        this.client = new WebClient();
        this.client.setJavaScriptEnabled(false);
        this.client.setCssEnabled(false);
        this.client.setUseInsecureSSL(true);
    }

    public void solveCaptcha() throws Exception {

        logger.log(Level.INFO, "Resolviendo Captcha...");

        String siteId = "" ;

        Thread.sleep(10000);

        WebElement elem = driver.findElement(By.xpath("//div[@class='g-recaptcha']"));
        if(elem.isDisplayed()) {
            // do something
        }
        try {
            siteId = elem.getAttribute("data-sitekey");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Catpcha's div cannot be found or missing attribute data-sitekey");
            System.err.println("Catpcha's div cannot be found or missing attribute data-sitekey");
            e.printStackTrace();
        }
        String QUERY = String.format("%sin.php?key=%s&method=userrecaptcha&googlekey=%s&pageurl=%s&here=now", API_BASE_URL, API_KEY, siteId, BASE_URL);
        Page response = client.getPage(QUERY);
        String stringResponse = response.getWebResponse().getContentAsString();
        String jobId = "";
        if(!stringResponse.contains("OK")) {
            logger.log(Level.WARNING, "Error with 2captcha.com API, received : " + stringResponse);
            throw new Exception("Error with 2captcha.com API, received : " + stringResponse);
        } else {
            jobId = stringResponse.split("\\|")[1];
        }

        boolean captchaSolved = false ;
        while(!captchaSolved) {
            try {
                response = client.getPage(String.format("%sres.php?key=%s&action=get&id=%s", API_BASE_URL, API_KEY, jobId));
            }
            catch(HttpHostConnectException e) {
                logger.log(Level.WARNING, e.getMessage());
                Thread.sleep(3000);
            }
            if (response.getWebResponse().getContentAsString().contains("CAPCHA_NOT_READY")){
                Thread.sleep(3000);
                System.out.println("Waiting for 2Captcha.com ...");
            } else {
                captchaSolved = true ;
                System.out.println("Captcha solved !");
            }
        }

        String captchaToken = response.getWebResponse().getContentAsString().split("\\|")[1];
        JavascriptExecutor js = (JavascriptExecutor) driver ;
        js.executeScript("document.getElementById('g-recaptcha-response').style.display = 'block';");
        WebElement textarea = driver.findElement(By.xpath("//textarea[@id='g-recaptcha-response']"));

        textarea.sendKeys(captchaToken);
        js.executeScript("document.getElementById('g-recaptcha-response').style.display = 'none';");

        //driver.findElement(By.id("name")).sendKeys("Kevin");

        if(driver.getPageSource().contains("your captcha was successfully submitted")) {
            System.out.println("Captcha successfuly submitted !");
        }else {
            System.out.println("Error while submitting captcha");
        }

        System.out.println();


    }
}
