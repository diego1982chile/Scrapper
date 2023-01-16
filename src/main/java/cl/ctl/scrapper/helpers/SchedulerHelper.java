package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.Schedule;
import cl.ctl.scrapper.model.exceptions.MissingParameterException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cl.ctl.scrapper.model.ParameterEnum.BASE_URL_CONFIG;
import static cl.ctl.scrapper.model.ParameterEnum.RETAILER;
import static cl.ctl.scrapper.model.ParameterEnum.TOKEN;

/**
 * Created by des01c7 on 18-12-20.
 */
public class SchedulerHelper {

    private static final SchedulerHelper instance = new SchedulerHelper();

    private static Timer timer;

    /** Logger para la clase */
    private static Logger logger;

    static LogHelper fh;

    private static String SCHEDULES_ENDPOINT;

    private List<Schedule> schedules;


    /**
     * Constructor privado para el Singleton del Factory.
     */
    private SchedulerHelper() {

        SCHEDULES_ENDPOINT = ConfigHelper.getInstance().getParameter(BASE_URL_CONFIG.getParameter()) + "schedules/" + ConfigHelper.getInstance().getParameter(RETAILER.getParameter());

        timer  = new Timer();

        fh = LogHelper.getInstance();

        logger = Logger.getLogger(SchedulerHelper.class.getName());

        logger.addHandler(fh);

    }

    public void loadSchedules() throws Exception {

        populateSchedules();

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

    private void populateSchedules() throws IOException, MissingParameterException {

        URL url = new URL(SCHEDULES_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + ConfigHelper.getInstance().getParameter(TOKEN.getParameter()));

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        String output;

        while ((output = br.readLine()) != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<Schedule>>() {
            });

            schedules = objectReader.readValue(output);

            if(schedules.isEmpty()) {
                throw new MissingParameterException("Empty schedule list retrieved from ScrapperConfig!!");
            }

        }

        conn.disconnect();

    }


}


