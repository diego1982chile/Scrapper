package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.Schedule;
import org.json.simple.JSONObject;

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

        logger.log(Level.INFO, "Scheduling scrapper instance according to schedule: " + schedules.toString());

        for (Schedule schedule : schedules) {
            String retailer = schedule.getRetailer().getName();
            Date time = parseSchedule(schedule.getSchedule());
            timer.scheduleAtFixedRate(new ScrapTask(retailer, time), time, period );
        }

    }

    public static SchedulerHelper getInstance() {
        return instance;
    }

    private Date parseSchedule(String schedule)
    {
        schedule = schedule.replace("T","");

        int hour = Integer.parseInt(schedule.split(":")[0]);
        int minute = Integer.parseInt(schedule.split(":")[1]);

        Calendar date = GregorianCalendar.getInstance(Locale.forLanguageTag("es-ES"));

        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute );
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTime();

    }

}


