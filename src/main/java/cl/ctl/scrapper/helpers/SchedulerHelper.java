package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.Schedule;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 18-12-20.
 */
public class SchedulerHelper {

    private static final SchedulerHelper instance = new SchedulerHelper();

    private static Timer timer;

    /** Logger para la clase */
    private static Logger logger;

    static LogHelper fh;


    /**
     * Constructor privado para el Singleton del Factory.
     */
    private SchedulerHelper() {

        timer  = new Timer();

        fh = LogHelper.getInstance();

        logger = Logger.getLogger(SchedulerHelper.class.getName());

        logger.addHandler(fh);

    }

    public void schedule(List<Schedule> schedules) {

        int period = 1000 * 60 * 60 * 24;

        logger.log(Level.INFO, "Programando Scrapper de acuerdo a programación: " + schedules.toString());

        for (Schedule schedule : schedules) {
            String client = schedule.getClient();
            Date time = schedule.getSchedule();
            timer.scheduleAtFixedRate(new ScrapTask(client, time), time, period );
        }

    }

    public static SchedulerHelper getInstance() {
        return instance;
    }



}


