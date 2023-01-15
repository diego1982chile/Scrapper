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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static cl.ctl.scrapper.model.ParameterEnum.*;

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

            if(args.length < 10) {
                //logger.log(Level.SEVERE, "You need to provide Retailer and Token!! exiting now");
                throw new IllegalArgumentException("Some required parameters missing, you need to provide: RETAILER, BASE_URL_CONFIG, BASE_URL_TOKEN, USER_NAME and PASSWORD. Exiting now");
            }

            for(int i = 0; i < args.length; ++i) {
                switch (args[i].toUpperCase()) {
                    case "-RETAILER":
                        ConfigHelper.getInstance().setParameter(RETAILER.getParameter(), args[i+1]);
                        break;
                    case "-BASE_URL_CONFIG":
                        ConfigHelper.getInstance().setParameter(BASE_URL_CONFIG.getParameter(), args[i+1]);
                        break;
                    case "-BASE_URL_TOKEN":
                        ConfigHelper.getInstance().setParameter(BASE_URL_TOKEN.getParameter(), args[i+1]);
                        break;
                    case "-USERNAME":
                        ConfigHelper.getInstance().setParameter(USER_NAME.getParameter(), args[i+1]);
                        break;
                    case "-PASSWORD":
                        ConfigHelper.getInstance().setParameter(PASSWORD.getParameter(), args[i+1]);
                        break;
                }
            }

            TokenHelper.getInstance().start();

            logger.log(Level.INFO, "Loading parameters from ScrapperConfig...");
            ParamsHelper.getInstance().loadParameters();

        } catch (SecurityException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

    }


}
