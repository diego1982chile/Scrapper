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

            if(args.length != 2) {
                logger.log(Level.SEVERE, "Número de argumentos no válido. Este programa recibe solo 1 argumento");
                throw new Exception("Número de argumentos no válido. Este programa recibe solo 1 argumento");
            }
            else {
                if(!args[0].equalsIgnoreCase("-client")) {
                    logger.log(Level.SEVERE, "Argumento '" + args[0] + "' no válido. Argumentos válidos: -client");
                    throw new Exception("Argumento '" + args[0] + "' no válido. Argumentos válidos: '-client0");
                }

                String client = args[1];

                logger.log(Level.INFO, "Invocando Scrapper con cliente '" + client + "'");

                ProcessHelper.getInstance().process(client);
            }

        } catch (SecurityException e) {
            logger.log(Level.SEVERE, e.getMessage());
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

    
}
