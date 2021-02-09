package cl.ctl.scrapper.controllers;

import cl.ctl.scrapper.helpers.*;
import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import cl.ctl.scrapper.scrappers.EasyScrapper;
import cl.ctl.scrapper.scrappers.SodimacScrapper;

import java.time.LocalDate;
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
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        LocalDate localDate = LocalDate.of(2021, 2, 5);
        LocalDate today = LocalDate.now();

        while(localDate.isBefore(today)) {
            ProcessHelper.getInstance().setProcessDate(localDate);
            scrap();
            localDate = localDate.plusDays(1);
            return;
        }

    }

    public static void scrap() throws Exception {



        logger.log(Level.INFO, "Scrapper Construmart -> Inicializando");

        ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();
        construmartScrapper.scrap();

        logger.log(Level.INFO, "Scrapper Easy -> Inicializando");

        EasyScrapper easyScrapper = new EasyScrapper();
        easyScrapper.scrap();

        logger.log(Level.INFO, "Scrapper Sodimac -> Inicializando");

        SodimacScrapper sodimacScrapper = new SodimacScrapper();
        sodimacScrapper.scrap();

        logger.log(Level.INFO, "Descomprimiendo y renombrando archivos");

        FilesHelper.getInstance().processFiles();

        logger.log(Level.INFO, "Subiendo archivos a servidor DivePort");

        UploadHelper.getInstance().uploadFiles();

        logger.log(Level.INFO, "Moviendo archivos en servidor DivePort");

        UploadHelper.getInstance().moveFiles();

        logger.log(Level.INFO, "Proceso finalizado con éxito. Enviando correo");

        MailHelper.getInstance().sendMail();

        LogHelper.getInstance().getFileControlList().clear();

    }
    
}
