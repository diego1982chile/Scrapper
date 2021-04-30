package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.ConcurrentAccessException;
import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.model.Schedule;
import cl.ctl.scrapper.scrappers.*;
import org.apache.commons.lang.StringUtils;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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


