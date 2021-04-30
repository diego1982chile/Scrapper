package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.Schedule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ParamsHelper {

    private static final ParamsHelper instance = new ParamsHelper();

    private static Timer timer;

    /** Logger para la clase */
    private static Logger logger;

    static LogHelper fh;


    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ParamsHelper() {

        timer  = new Timer();
        fh = LogHelper.getInstance();
        logger = Logger.getLogger(ParamsHelper.class.getName());
        logger.addHandler(fh);

    }

    public void loadParameters() {

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("parameters.json")) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject params = (JSONObject) obj;

            String downloadPath = params.get("downloads").toString();

            ConfigHelper.getInstance().setParameter("file.download_path",downloadPath);

            JSONArray schedules = (JSONArray) params.get("schedules");

            List<Schedule> scheduleList = new ArrayList<>();

            //Iterate over employee array
            for (int i=0; i < schedules.size(); i++) {
                Schedule schedule = parseScheduleObject( (JSONObject) schedules.get(i) );
                scheduleList.add(schedule);
            }

            SchedulerHelper.getInstance().schedule(scheduleList);


        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static ParamsHelper getInstance() {
        return instance;
    }

    private Schedule parseScheduleObject(JSONObject scheduleJson)
    {
        //Get employee object within list
        String client = scheduleJson.get("client").toString();

        String schedule = scheduleJson.get("schedule").toString();

        int hour = Integer.parseInt(schedule.split(":")[0]);
        int minute = Integer.parseInt(schedule.split(":")[1]);

        Calendar date = GregorianCalendar.getInstance(Locale.forLanguageTag("es-ES"));

        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute );
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return new Schedule(client, date.getTime());

    }

}


