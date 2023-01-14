package cl.ctl.scrapper.controllers;

import cl.ctl.scrapper.helpers.*;
import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.model.Schedule;
import cl.ctl.scrapper.scrappers.AbstractScrapper;
import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import cl.ctl.scrapper.scrappers.EasyScrapper;
import cl.ctl.scrapper.scrappers.SodimacScrapper;
import org.apache.james.mime4j.field.address.parser.Token;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cl.ctl.scrapper.model.ParameterEnum.TOKEN;

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

            if(args.length < 2) {
                logger.log(Level.SEVERE, "You need to provide Retailer and Token!!");
            }

            String retailer = args[1];

            ConfigHelper.getInstance().setParameter("RETAILER", retailer);

            String token = args[1];

            ConfigHelper.getInstance().setParameter(TOKEN.name(), token);

            logger.log(Level.INFO, "Loading parameters from ScrapperService...");
            ParamsHelper.getInstance().loadParameters();

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
