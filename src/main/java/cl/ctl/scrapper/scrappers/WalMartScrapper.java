package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.DateOutOfRangeException;
import cl.ctl.scrapper.model.NoReportsException;
import cl.ctl.scrapper.model.TimeOutException;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.util.Pair;
import org.joda.primitives.list.DoubleList;
import org.joda.primitives.list.impl.ArrayDoubleList;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * Created by des01c7 on 16-12-20.
 */
public class WalMartScrapper extends AbstractScrapper {

    Robot robot;

    float m;
    int x0;
    int y0;

    public WalMartScrapper() throws IOException {
        super();
        cadena = "WalMart";
        holding = "Nutrisa";
        url = "https://retaillink.login.wal-mart.com/?ServerType=IIS1&CTAuthMode=BASIC&language=en&utm_source=retaillink&utm_medium=redirect&utm_campaign=FalconRelease&CT_ORIG_URL=/&ct_orig_uri=/ ";
        logo = "walmart.jpg";
        fileExt = ".xlsx";
    }

    void login() throws Exception {

        int cont = 0;

        Actions actions = null;

        while(cont < 10) {

            cont++;

            try {

                driver.findElements(By.className("form-control__formControl___3uDUX")).get(0).sendKeys("scrappers.walmart.user");
                Thread.sleep(2000);
                driver.findElements(By.className("form-control__formControl___3uDUX")).get(1).sendKeys("scrappers.walmart.password");
                Thread.sleep(2000);
                driver.findElement(By.className("spin-button-children")).click();

                Thread.sleep(5000);

                if(true) {
                    solveCustomChallenge();
                    Thread.sleep(10000);
                }

                actions = new Actions(driver);
                WebElement decisionSupport = driver.findElement(By.xpath(".//div[contains(text(),'Decision Support - New')]"));
                actions.moveToElement(decisionSupport).click().build().perform();

                Thread.sleep(10000);

                break;


            } catch (Throwable e) {
                try {
                    driver.findElements(By.className("form-control__formControl___3uDUX")).get(0).sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
                    Thread.sleep(2000);
                    driver.findElements(By.className("form-control__formControl___3uDUX")).get(1).sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
                    Thread.sleep(2000);
                }
                catch (Throwable e2) {
                    logger.log(Level.SEVERE, e2.getMessage());
                    throw e;
                }

                if (cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }
        }

        // GoTo Comercial
        cont = 0;


        cont = 0;

        while(cont < 10) {

            cont++;

            try {

                //Si no hay reportes disponibles, submitiar uno nuevo
                ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
                driver.switchTo().window(tabs2.get(1));

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

                driver.findElement(By.xpath(".//*[contains(text(),'Modificar')]")).click();

                Thread.sleep(20000);

                break;

            }
            catch(Throwable e) {
                if(cont >= 10) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }
        }

    }

    void doScrap(String since, String until) throws Exception {

        Actions actions = null;

        int cont = 0;

        while(cont < 10) {

            cont++;

            try {
                // GoTo Decision Support

                /*
                WebElement decisionSupport = driver.findElement(By.xpath(".//div[contains(text(),'Decision Support - New')]"));
                actions = new Actions(driver);
                actions.moveToElement(decisionSupport).click().build().perform();

                Thread.sleep(10000);
                */
                
                driver.get("https://retaillink.wal-mart.com/decision_support/Report_Builder.aspx?reopen=true&AppId=300&jobid=39040995&getSavedRequests=1&isShared=N&isScheduled=N&country_cd=K2&divid=1");

                Thread.sleep(5000);

                driver.switchTo().frame("menu");

                Thread.sleep(3000);

                driver.findElement(By.xpath(".//a[contains(text(),'Selección de Tiempo')]")).click();

                Thread.sleep(3000);

                driver.switchTo().parentFrame();

                Thread.sleep(3000);

                driver.switchTo().frame("content_4");

                Thread.sleep(3000);

                //driver.switchTo().frame("data_frame");

                //Thread.sleep(5000);

                driver.switchTo().frame("left");

                driver.findElement(By.xpath(".//*[contains(text(),'Rango 1')]")).click();

                Thread.sleep(3000);

                driver.switchTo().parentFrame();

                Thread.sleep(3000);

                driver.switchTo().frame("mid");

                Thread.sleep(3000);

                driver.switchTo().frame("F1000010_frame");

                Thread.sleep(3000);

                WebElement fechaPOS = driver.findElement(By.name("204F1000010Tree"));
                fechaPOS.click();

                Thread.sleep(3000);

                driver.findElement(By.xpath(".//*[contains(text(),'Rango 1 Está Entre')]")).click();

                Thread.sleep(3000);

                driver.switchTo().parentFrame();

                Thread.sleep(3000);

                driver.switchTo().parentFrame();

                Thread.sleep(3000);

                driver.switchTo().frame("right");

                Thread.sleep(3000);

                driver.switchTo().frame("filter_values");

                Thread.sleep(3000);

                Select dateCondition = new Select(driver.findElement(By.id("filllist")));

                Thread.sleep(3000);

                dateCondition.selectByIndex(0);

                Thread.sleep(2000);

                driver.switchTo().parentFrame();

                Thread.sleep(2000);

                driver.findElement(By.id("Delete")).click();

                Thread.sleep(3000);

                WebElement sinceInput = driver.findElement(By.xpath("//input[@id='firstValue']"));
                sinceInput.clear();
                sinceInput.sendKeys(convertDate(since));

                Thread.sleep(3000);

                WebElement untilInput = driver.findElement(By.xpath("//input[@id='lastValue']"));
                untilInput.clear();
                untilInput.sendKeys(convertDate(until));

                Thread.sleep(3000);

                driver.findElement(By.xpath(".//input[@id='btnAnd']")).click();

                Thread.sleep(2000);

                driver.switchTo().parentFrame();

                Thread.sleep(2000);

                driver.switchTo().parentFrame();

                Thread.sleep(2000);

                driver.switchTo().frame("menu");

                Thread.sleep(2000);

                driver.findElement(By.xpath(".//a[contains(text(),'Submitir')]")).click();

                Thread.sleep(2000);

                driver.switchTo().parentFrame();

                Thread.sleep(2000);

                driver.switchTo().frame("submit");

                Thread.sleep(2000);

                driver.findElement(By.xpath(".//input[@id='subnow']")).click();

                Thread.sleep(2000);

                driver.findElement(By.xpath(".//input[@id='viewStatus']")).click();

                Thread.sleep(2000);

                driver.switchTo().frame("JobTable");

                int nutrisaCTLReports = driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).size();

                //Si no existe la solicitud del reporte lanzar excepción
                if(nutrisaCTLReports == 0) {
                    throw new NoReportsException("No hay reportes para 'Nutrisa CTL (No Modificar)'");
                }

                int numberOfTries = 15;

                // Obtener el JobId para posteriormene realizar la busqueda por JobId

                //Actualizar status reporte
                // Ojo: Se asume que el reporte mas reciente es el que se generó por el robot
                Thread.sleep(2000);
                driver.switchTo().parentFrame();
                driver.findElement(By.xpath(".//span[contains(text(),'Actualizar')]")).click();
                Thread.sleep(2000);
                driver.switchTo().frame("JobTable");
                String jobId = driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).get(0).findElement(By.xpath("parent::td/preceding-sibling::td/following-sibling::td")).findElement(By.xpath("span")).getAttribute("innerHTML");

                for (int i = 0; i < numberOfTries; ++i) {

                    //Actualizar status reporte
                    Thread.sleep(2000);
                    driver.switchTo().parentFrame();
                    driver.findElement(By.xpath(".//span[contains(text(),'Actualizar')]")).click();
                    Thread.sleep(2000);
                    driver.switchTo().frame("JobTable");

                    //String status = driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).get(0).findElement(By.xpath("parent::td/preceding-sibling::td/following-sibling::td/following-sibling::td")).findElement(By.xpath("span")).getAttribute("innerHTML");

                    // Se busca por JobId
                    String status = driver.findElement(By.xpath(".//span[contains(text(),'" + jobId + "')]")).findElement(By.xpath("parent::td/preceding-sibling::td/following-sibling::td/following-sibling::td")).findElement(By.xpath("span")).getAttribute("innerHTML");

                    switch(status) {
                        case "Hecho":
                        case "Salvados":
                            driver.findElements(By.xpath(".//span[contains(text(),'Nutrisa CTL (No Modificar)')]")).get(0).click();
                            Thread.sleep(5000);
                            return;
                        case "Activo":
                        case "Formateando":
                        case "Esperando":
                            logger.log(Level.INFO, "Reporte en estado '" + status + "', se esperará 1 min antes de la próxima consulta");
                            Thread.sleep(60000);
                            break;
                        //flag = false;
                        //throw new Exception("Este reporte ha sido encolado para ser generado en la próxima ventana Operativa. Intentar nuevamente la descarga más tarde");
                        default:
                            throw new Exception("Status de Reporte '" + status + "' no soportado! Contacte al Administrador");
                    }

                }

                // Si se llega a este punto es porque se superó el límite de tiempo de espera, levantar excepción de timeout
                throw new TimeOutException("Se superó el tiempo de espera para el reporte ''Nutrisa CTL (No Modificar)' con JobId = " + jobId);

                //break;

                //filllist
            }
            catch(NoReportsException e) {
                throw e;
            }
            catch (TimeOutException e2) {
                throw e2;
            }
            catch(Throwable e3) {
                if(cont >= 10) {
                    logger.log(Level.SEVERE, e3.getMessage());
                    throw e3;
                }
            }

        }
    }

    private String convertDate(String date) {

        String[] tokens = date.split("-");

        return tokens[1] + "-" + tokens[0] + "-" + tokens[2];
    }

    //Desafio consistente en mantener boton del mouse presionado sobre una región específica de la pantalla durante una cantidad de tiempo
    //Se emula movimiento del mouse mediante puntos que siguen la trayectoria de la recta que pasa por los puntos:
    //P0 = posicion actual del mouse y Pn = posicion dentro de la región (determinada empiricamente)
    //Se utiliza la libreria Robot, por lo que la pantalla debe estar visible durante la ejecución de esta función
    private void solveCustomChallenge() throws InterruptedException, AWTException {

        robot = new Robot();

        //Límites inferiores región captcha (determinados empiricamente => puede variar en otras resoluciones)
        int xMin = 350;
        int yMin = 420;

        //Delta que se sumará a límites inferiores para agregar aleatoriedad a la posicion
        int x = ThreadLocalRandom.current().nextInt(0, 80);
        int y = ThreadLocalRandom.current().nextInt(0, 80);

        //P0 = posición actual mouse
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();

        int x0 = (int) b.getX();
        int y0 = (int) b.getY();

        //Pn = posición final mouse
        int xn = xMin + x;
        int yn = yMin + y;

        //Crear parámetros de la recta que pasa por los puntos P0 y Pn
        createLinearParameters(new int[]{x0,xn}, new int[]{y0,yn});

        int rate = 1;

        if(xn < x0) {
            rate = -1;
        }

        int[] X = new int[Math.abs(xn-x0)];
        int[] Y = new int[Math.abs(xn-x0)];

        int cont = 0;

        //Generar puntos de la recta
        while(x0 != xn) {
            x0 = x0 + rate;
            X[cont] = x0;
            Y[cont] = getLinearValue(x0);
            cont++;
        }

        //Mover mouse siguiendo los puntos de la recta previamente calculada
        for(int i = 0; i < X.length; ++i) {
            robot.mouseMove(X[i], Y[i]);
            int k = ThreadLocalRandom.current().nextInt(10, 200);
            Thread.sleep(k);
        }

        a = MouseInfo.getPointerInfo();
        b = a.getLocation();

        //robot.mouseMove(xMin + x, yMin + y);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        int t = ThreadLocalRandom.current().nextInt(3000, 5000);

        Thread.sleep(t);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private void createLinearParameters(int[] x, int[] y) {
        m =  (float) (y[1] - y[0])/(x[1] - x[0]);
        x0 = x[0];
        y0 = y[0];
    }

    private int getLinearValue(int x) {
        return  (int)((float)m*x - (float)m*x0 + y0);
    }




}
