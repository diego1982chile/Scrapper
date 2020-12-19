package cl.ctl.scrapper.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import umontreal.ssj.functionfit.BSpline;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;

import static umontreal.ssj.functionfit.BSpline.createInterpBSpline;

/**
 * Created by des01c7 on 15-12-20.
 */
public class InteractionHelper {

    /*
     public void humanLikeMouseMove(WebDriver driver) throws Exception {

        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Random r = new Random();

        int low = 100;
        int high = 1000;

        int MAX_X = (int) screenSize.getWidth();
        int MAX_Y = (int) (0.9*screenSize.getHeight());

        MAX_X = 304;
        MAX_Y = 78;

        System.out.println("MAX_X = " + MAX_X + " MAX_Y = " + MAX_Y);

        java.util.List<Integer> lowerLimit = Arrays.asList(0,0);
        java.util.List<Integer> upperLimit = Arrays.asList(MAX_X,MAX_Y);

        GenerationalGeneticAlgorithmDistanceRunner runner = new GenerationalGeneticAlgorithmDistanceRunner();

        int xTo = MAX_X/2 - 150;
        int yTo = 156 + 300 + 50 + 30;

         PointerInfo a = MouseInfo.getPointerInfo();
         Point b = a.getLocation();
         int xFrom = (int) b.getX();
         int yFrom = (int) b.getY();

        WebElement ele= driver.findElement(By.id("recaptcha-anchor"));

        org.openqa.selenium.Point point = ele.getLocation();
        xTo = point.getX();
        yTo = point.getY();

        //Rectangle captchaArea = new Rectangle(xTo,yTo,300,74);
        Rectangle captchaArea = new Rectangle(xTo,yTo,30,7);

        java.util.List<Evaluation> evaluations = runner.run(captchaArea,lowerLimit,upperLimit);

         Point center = new Point(xTo, yTo);

         int xOffset = 0;
         int yOffset = 0;

         Curve curve = new Curve();

         List<PointF> points = curve.generateCurve(new PointF(xFrom, yFrom), new PointF(xTo, yTo), 350f, 7f, true, true);

        Actions actionProvider = new Actions(driver);

        Thread.sleep(5000);

         center = new Point(xTo, yTo);

         Collections.reverse(points);

         for(int k = 0; k < evaluations.size(); ++k) {

             if(k == 0) {
                 //xOffset = (int) (points.get(k).x);
                 //yOffset = (int) (points.get(k).y);
                 xOffset = (int) (evaluations.get(k).getPoint().getX());
                 yOffset = (int) (evaluations.get(k).getPoint().getY());
             }
             else {
                 //xOffset = (int) (points.get(k).x - points.get(k-1).x);
                 //yOffset = (int) (points.get(k).y - points.get(k-1).y);
                 xOffset = (int) (evaluations.get(k).getPoint().getX() - evaluations.get(k-1).getPoint().getX());
                 yOffset = (int) (evaluations.get(k).getPoint().getY() - evaluations.get(k-1).getPoint().getY());
             }

             System.out.println("xOffset = " + xOffset + " yOffset = " + yOffset);
             System.out.println("(" + evaluations.get(k).getPoint().getX() + "," + evaluations.get(k).getPoint().getY() + ")");

             // (304, 78)

             // Performs mouse move action onto the element
             try {
                 actionProvider.moveByOffset(xOffset % MAX_X, yOffset % MAX_Y).build().perform();
                 actionProvider.click().build().perform();
                 actionProvider.release().build().perform();
             }
             catch(MoveTargetOutOfBoundsException e) {
                 System.out.println("OUCH!!");
             }

             Point p = new Point((int)(evaluations.get(k).getPoint().getX()), (int)(evaluations.get(k).getPoint().getY()));

             System.out.println("distance = " + p.distance(center));

             if(p.distance(center) < 5) {
                 break;
             }


             Thread.sleep(r.nextInt(high-low) + low);
         }

        /*
        for (Evaluation evaluation : evaluations) {
            System.out.println("Evaluation{" +
                    "point= (" + evaluation.getPoint().getX() + "," + evaluation.getPoint().getY() + ")" +
                    ", distance=" + evaluation.getDistance() +
                    '}');

            //robot.mouseMove(evaluation.getPoint().x, evaluation.getPoint().y);


            if(xOffset != 0) {
                xOffset = evaluation.getPoint().x + xOffset;
                //xOffset = (int) bSpline.getX()[i] - xOffset;
            }
            else {
                xOffset = evaluation.getPoint().x;
                //xOffset = (int) bSpline.getX()[i];
            }

            if(yOffset != 0) {
                yOffset = evaluation.getPoint().y + yOffset;
                //yOffset = (int) bSpline.getY()[i] - yOffset;
            }
            else {
                yOffset = evaluation.getPoint().y;
                //yOffset = (int) bSpline.getY()[i];
            }

            // (304, 78)

            // Performs mouse move action onto the element
            try {
                actionProvider.moveByOffset(xOffset % MAX_X, yOffset % MAX_Y).build().perform();
                actionProvider.click().build().perform();
                actionProvider.release().build().perform();
            }
            catch(MoveTargetOutOfBoundsException e) {
                System.out.println("OUCH!!");
            }

            Thread.sleep(r.nextInt(high-low) + low);
        }


         actionProvider.moveToElement(ele);

         actionProvider.click().build().perform();
    }
    */

    /*
    public void humanLikeMouseMoveRobot() throws Exception {

        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Random r = new Random();

        int low = 100;
        int high = 1000;

        int MAX_X = (int) screenSize.getWidth();
        int MAX_Y = (int) (0.9*screenSize.getHeight());

        System.out.println("MAX_X = " + MAX_X + " MAX_Y = " + MAX_Y);

        java.util.List<Integer> lowerLimit = Arrays.asList(0,0);
        java.util.List<Integer> upperLimit = Arrays.asList(MAX_X,MAX_Y);

        GenerationalGeneticAlgorithmDistanceRunner runner = new GenerationalGeneticAlgorithmDistanceRunner();

        int x = MAX_X/2 - 150;
        int y = 156 + 300 + 50 + 30;

        Rectangle captchaArea = new Rectangle(x,y,300,74);
        //Rectangle captchaArea = new Rectangle(x,y,30,7);

        java.util.List<Evaluation> evaluations = runner.run(captchaArea,lowerLimit,upperLimit);

        double[] X = new double[100];
        double[] Y = new double[100];
        int i = 0;

        for (Evaluation evaluation : evaluations) {
            X[i] = evaluation.getPoint().getX();
            Y[i]= evaluation.getPoint().getY();
        }

        BSpline bSpline = createInterpBSpline(X, Y, 50);

        Robot robot = new Robot();

        Thread.sleep(5000);

        i = 0;

        Point center = new Point(30,7);

        for (Evaluation evaluation : evaluations) {
            System.out.println("Evaluation{" +
                    "point= (" + evaluation.getPoint().getX() + "," + evaluation.getPoint().getY() + ")" +
                    ", distance=" + evaluation.getDistance() +
                    '}');

            robot.mouseMove((int)evaluation.getPoint().getX(), (int)evaluation.getPoint().getY());

            //actionProvider.perform();

            //robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            //robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

            Thread.sleep(r.nextInt(high-low) + low);
        }

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    }
    */
}
