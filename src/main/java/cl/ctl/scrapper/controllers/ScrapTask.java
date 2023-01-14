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

    String retailer;
    Date date;

    private static final Logger logger = Logger.getLogger(TimerTask.class.getName());

    static LogHelper fh = LogHelper.getInstance();

    public ScrapTask(String retailer, Date date) {
        this.retailer = retailer;
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
                logger.log(Level.INFO, "Running process for retailer '" + retailer + "' at schedule '" + date + "'");
                ProcessHelper.getInstance().process(retailer);
            }
            else {
                logger.log(Level.WARNING, "Attempting to run process for retailer '" + retailer + "' at an unscheduled time!, Execution is skipped");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.getMessage());
            //cancel();
        }
    }

}
