package cl.ctl.scrapper.controllers;

import cl.ctl.scrapper.helpers.*;
import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.scrappers.AbstractScrapper;
import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import cl.ctl.scrapper.scrappers.EasyScrapper;
import cl.ctl.scrapper.scrappers.SodimacScrapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by root on 07-12-20.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static LogHelper fh = LogHelper.getInstance();

    public static void main(String... args) throws Exception {

        // This block configure the logger with handler and formatter
        try {
            logger.addHandler(fh);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            logger.log(Level.INFO, "Testeando Proceso Scrap");
            ProcessHelper.getInstance().process(yesterday.toString(), Arrays.asList("WalMart"));
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        /*
        LocalDate localDate = LocalDate.of(2021, 4, 9);
        LocalDate today = LocalDate.now();

        while(localDate.isBefore(today)) {
            //ProcessHelper.getInstance().setProcessDate(localDate);
            scrap();
            localDate = localDate.plusDays(1);
            return;
        }
        */


    }

    public static void scrap() throws Exception {

        int max = 3;

        for (int i = 0; i < max; i++) {
            int cont = i + 1;
            logger.log(Level.INFO, "Descargando scraps -> iteración " + cont + " de " + max);

            for (AbstractScrapper scrapper : ProcessHelper.getInstance().getScrappers().values()) {
                scrapper.process(false);
                //ProcessHelper.getInstance().getExecutor().execute(scrapper);
            }

            int errors = 0;

            for (AbstractScrapper scrapper : ProcessHelper.getInstance().getScrappers().values()) {
                for (FileControl fileControl : scrapper.getFileControlList()) {
                    if(!fileControl.getErrors().isEmpty()) {
                       errors++;
                    }
                }
            }

            if(errors == 0) {
                break;
            }
        }


        logger.log(Level.INFO, "Descomprimiendo y renombrando archivos");

        FilesHelper.getInstance().processFiles();

        logger.log(Level.INFO, "Subiendo archivos a servidor DivePort");

        UploadHelper.getInstance().uploadFiles();

        logger.log(Level.INFO, "Moviendo archivos en servidor DivePort");

        UploadHelper.getInstance().moveFiles();

        logger.log(Level.INFO, "Proceso finalizado con éxito. Enviando correo");

        MailHelper.getInstance().sendMail();

    }
    
}
