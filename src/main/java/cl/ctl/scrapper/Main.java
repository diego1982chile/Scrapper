package cl.ctl.scrapper;

import cl.ctl.scrapper.helpers.*;
import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import cl.ctl.scrapper.scrappers.EasyScrapper;
import cl.ctl.scrapper.scrappers.SodimacScrapper;

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

        int cont = 1;

        logger.log(Level.INFO, "Scrapper Construmart -> Inicializando");

        ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();

        while(true) {
            try {
                logger.log(Level.INFO, "Scrapper Construmart -> Scrap (intento " + cont + ")");
                cont++;
                construmartScrapper.scrap();
                break;
            }
            catch(Exception e) {
                if(cont < 10) {
                    logger.log(Level.WARNING, e.getMessage());
                }
                else {
                    ErrorHelper.getInstance().sendMail();
                    throw e;
                }
            }
        }

        /*
        cont = 1;

        logger.log(Level.INFO, "Scrapper Easy -> Inicializando");

        EasyScrapper easyScrapper = new EasyScrapper();

        while(true) {
            try {
                logger.log(Level.INFO, "Scrapper Easy -> Scrap (intento " + cont + ")");
                cont++;
                easyScrapper.scrap();
                break;
            }
            catch(Exception e) {
                if(cont < 10) {
                    logger.log(Level.WARNING, e.getMessage());
                }
                else {
                    ErrorHelper.getInstance().sendMail();
                    throw e;
                }
            }
        }

        cont = 1;

        logger.log(Level.INFO, "Scrapper Sodimac -> Inicializando");

        SodimacScrapperOld sodimacScrapperOld = new SodimacScrapperOld();

        while(true) {
            try {
                logger.log(Level.INFO, "Scrap Sodimac -> Scrap (intento " + cont + ")");
                cont++;
                sodimacScrapperOld.scrap();
                break;
            }
            catch(Exception e) {
                if(cont < 10) {
                    logger.log(Level.WARNING, e.getMessage());
                }
                else {
                    ErrorHelper.getInstance().sendMail();
                    throw e;
                }
            }
        }

        logger.log(Level.INFO, "Descomprimiendo y renombrando archivos");

        try {
            FilesHelper.getInstance().processFiles();
        }
        catch(Exception e) {
            ErrorHelper.getInstance().sendMail();
            throw e;
        }


        logger.log(Level.INFO, "Subiendo archivos a servidor DivePort");

        try {
            UploadHelper.getInstance().uploadFiles();
        }
        catch(Exception e) {
            ErrorHelper.getInstance().sendMail();
            throw e;
        }

        logger.log(Level.INFO, "Moviendo archivos en servidor DivePort");

        try {
            UploadHelper.getInstance().moveFiles();
        }
        catch(Exception e) {
            ErrorHelper.getInstance().sendMail();
            throw e;
        }

        logger.log(Level.INFO, "Proceso finalizado con éxito. Enviando correo");

        MailHelper.getInstance().sendMail();
        */
    }
    
}
