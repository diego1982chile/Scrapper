package cl.ctl.scrapper.controllers;

import cl.ctl.scrapper.helpers.LogHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;

import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.temporal.ChronoField.*;

/**
 * Created by root on 22-04-21.
 */
public class ScrapTask extends TimerTask {

    String holding;
    Date date;

    private static final Logger logger = Logger.getLogger(TimerTask.class.getName());

    static LogHelper fh = LogHelper.getInstance();

    public ScrapTask(String holding, Date date) {
        this.holding = holding;
        this.date = date;

        logger.addHandler(fh);
    }


    @Override
    public void run() {
        try {
            Date date = new Date();

            int hour = date.getHours();
            int minute = date.getMinutes();
            int second = date.getSeconds();

            int sched_hour = this.date.getHours();
            int sched_minute = this.date.getMinutes();
            int sched_second = this.date.getSeconds();

            if(hour == sched_hour && minute == sched_minute /*&& second == sched_second*/) {
                logger.log(Level.INFO, "Ejecutando proceso para holding '" + holding + "' en horario programado '" + date + "'");
                ProcessHelper.getInstance().process(holding);
            }
            else {
                logger.log(Level.WARNING, "Se está intentando ejecutar proceso para holding '" + holding + "' en un horario no programado!, Se omite ejecución");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.getMessage());
            //cancel();
        }
    }

}
