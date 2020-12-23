package cl.ctl.scrapper;

import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import cl.ctl.scrapper.scrappers.SodimacScrapper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

/**
 * Created by root on 07-12-20.
 */
public class MouseGlider {

    static Random r = new Random();
    static int low = 10;
    static int high = 100;

    public static void main(String... args) throws Exception {


        //EasyScrapper easyScrapper = new EasyScrapper(driver);

        //easyScrapper.scrap();

        //ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();

        //construmartScrapper.scrap();

        SodimacScrapper sodimacScrapper = new SodimacScrapper();

        sodimacScrapper.scrap();

    }


    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.
    private File getFileFromResourceAsStream(String fileName) {

        final String FILE_TO = "buster_captcha_solver_for_humans-1.1.0-an+fx.xpi";

        // The class loader that loaded the class
        InputStream inputStream = getClass().getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            //return inputStream;
            // commons-io
            try {
                File file = new File(FILE_TO);

                FileUtils.copyInputStreamToFile(inputStream, file);

                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}
