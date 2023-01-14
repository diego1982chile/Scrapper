package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.Account;
import cl.ctl.scrapper.model.Parameter;
import cl.ctl.scrapper.model.Schedule;
import cl.ctl.scrapper.model.exceptions.MissingParameterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cl.ctl.scrapper.model.ParameterEnum.*;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ParamsHelper {

    private static final ParamsHelper instance = new ParamsHelper();

    private static Timer timer;

    /** Logger para la clase */
    private static Logger logger;

    static LogHelper fh;

    private static final String BASE_URL = "http://localhost:8080/ScrapperService/api/";

    private static final String PARAMETERS_ENDPOINT = BASE_URL + "parameters";

    private static final String SCHEDULES_ENDPOINT = BASE_URL + "schedules/" + ConfigHelper.getInstance().getParameter(RETAILER.name());

    private List<Parameter> parameters;

    private List<Schedule> schedules;

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ParamsHelper() {

        timer  = new Timer();
        fh = LogHelper.getInstance();
        logger = Logger.getLogger(ParamsHelper.class.getName());
        logger.addHandler(fh);

        populateParameters();
        populateSchedules();
    }

    public void loadParameters() throws Exception {

        String captchaApiKey = parameters.stream()
                .filter(e -> e.getName().equals(CAPTCHA_API_KEY.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + CAPTCHA_API_KEY.name() + " found"));

        ConfigHelper.getInstance().setParameter(CAPTCHA_API_KEY.name(), captchaApiKey);

        String errorTo = parameters.stream()
                .filter(e -> e.getName().equals(ERROR_TO.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + ERROR_TO.name() + " found"));

        ConfigHelper.getInstance().setParameter(ERROR_TO.name(), errorTo);

        String fileDownloadPath = parameters.stream()
                .filter(e -> e.getName().equals(FILE_DOWNLOAD_PATH.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + FILE_DOWNLOAD_PATH.name() + " found"));

        ConfigHelper.getInstance().setParameter(FILE_DOWNLOAD_PATH.name(), fileDownloadPath);

        String mailFromPassword = parameters.stream()
                .filter(e -> e.getName().equals(MAIL_FROM_PASSWORD.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + MAIL_FROM_PASSWORD.name() + " found"));

        ConfigHelper.getInstance().setParameter(MAIL_FROM_PASSWORD.name(), mailFromPassword);

        String mailFromUser = parameters.stream()
                .filter(e -> e.getName().equals(MAIL_FROM_USER.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + MAIL_FROM_USER.name() + " found"));

        ConfigHelper.getInstance().setParameter(MAIL_FROM_USER.name(), mailFromUser);

        String mailTo = parameters.stream()
                .filter(e -> e.getName().equals(MAIL_TO.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + MAIL_TO.name() + " found"));

        ConfigHelper.getInstance().setParameter(MAIL_TO.name(), mailTo);

        String uploadHost = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_HOST.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_HOST.name() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_HOST.name(), uploadHost);

        String uploadPassword = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_PASSWORD.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_PASSWORD.name() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_PASSWORD.name(), uploadPassword);

        String uploadPath = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_PATH.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_PATH.name() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_PATH.name(), uploadPath);

        String uploadServer = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_SERVER.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_SERVER.name() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_SERVER.name(), uploadServer);

        String uploadTarget = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_TARGET.name()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_TARGET.name() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_TARGET.name(), uploadTarget);

        // Leyendo schedules

        SchedulerHelper.getInstance().schedule(schedules);

    }

    public static ParamsHelper getInstance() {
        return instance;
    }

    private void populateParameters() {

        try {

            URL url = new URL(PARAMETERS_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer " + ConfigHelper.getInstance().getParameter("TOKEN"));

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tree = mapper.readTree(output);

                //JsonNode node = tree.at("/glossary/GlossDiv/GlossList/GlossEntry");
                Parameter parameter = mapper.treeToValue(tree, Parameter.class);

                parameters.add(parameter);

                System.out.println(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void populateSchedules() {

        try {

            URL url = new URL(SCHEDULES_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer " + ConfigHelper.getInstance().getParameter("TOKEN"));

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tree = mapper.readTree(output);

                //JsonNode node = tree.at("/glossary/GlossDiv/GlossList/GlossEntry");
                Schedule schedule = mapper.treeToValue(tree, Schedule.class);

                schedules.add(schedule);

                System.out.println(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}


