package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.ScrapTask;
import cl.ctl.scrapper.model.Account;
import cl.ctl.scrapper.model.Parameter;
import cl.ctl.scrapper.model.Schedule;
import cl.ctl.scrapper.model.exceptions.MissingParameterException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

    private static String PARAMETERS_ENDPOINT;

    private static String SCHEDULES_ENDPOINT;

    private List<Parameter> parameters;

    private List<Schedule> schedules;

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ParamsHelper() {

        PARAMETERS_ENDPOINT = BASE_URL + "parameters";
        SCHEDULES_ENDPOINT = BASE_URL + "schedules/" + ConfigHelper.getInstance().getParameter(RETAILER.getParameter());

        timer  = new Timer();
        fh = LogHelper.getInstance();
        logger = Logger.getLogger(ParamsHelper.class.getName());
        logger.addHandler(fh);

    }

    public void loadParameters() throws Exception {

        Thread.sleep(5000);

        populateSchedules();
        populateParameters();

        String captchaApiKey = parameters.stream()
                .filter(e -> e.getName().equals(CAPTCHA_API_KEY.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + CAPTCHA_API_KEY.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(CAPTCHA_API_KEY.getParameter(), captchaApiKey);

        String errorTo = parameters.stream()
                .filter(e -> e.getName().equals(ERROR_TO.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + ERROR_TO.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(ERROR_TO.getParameter(), errorTo);

        String fileDownloadPath = parameters.stream()
                .filter(e -> e.getName().equals(FILE_DOWNLOAD_PATH.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + FILE_DOWNLOAD_PATH.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(FILE_DOWNLOAD_PATH.getParameter(), fileDownloadPath);

        String mailFromPassword = parameters.stream()
                .filter(e -> e.getName().equals(MAIL_FROM_PASSWORD.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + MAIL_FROM_PASSWORD.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(MAIL_FROM_PASSWORD.getParameter(), mailFromPassword);

        String mailFromUser = parameters.stream()
                .filter(e -> e.getName().equals(MAIL_FROM_USER.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + MAIL_FROM_USER.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(MAIL_FROM_USER.name(), mailFromUser);

        String mailTo = parameters.stream()
                .filter(e -> e.getName().equals(MAIL_TO.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + MAIL_TO.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(MAIL_TO.getParameter(), mailTo);

        String uploadHost = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_HOST.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_HOST.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_HOST.getParameter(), uploadHost);

        String uploadPassword = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_PASSWORD.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_PASSWORD.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_PASSWORD.name(), uploadPassword);

        String uploadPath = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_PATH.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_PATH.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_PATH.getParameter(), uploadPath);

        String uploadServer = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_SERVER.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_SERVER.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_SERVER.getParameter(), uploadServer);

        String uploadTarget = parameters.stream()
                .filter(e -> e.getName().equals(UPLOAD_TARGET.getParameter()))
                .map(Parameter::getValue)
                .findFirst()
                .orElseThrow(() -> new MissingParameterException("No parameter " + UPLOAD_TARGET.getParameter() + " found"));

        ConfigHelper.getInstance().setParameter(UPLOAD_TARGET.getParameter(), uploadTarget);

        // Leyendo schedules
        SchedulerHelper.getInstance().schedule(schedules);

    }

    public static ParamsHelper getInstance() {
        return instance;
    }

    private void populateParameters() throws IOException, MissingParameterException {

        URL url = new URL(PARAMETERS_ENDPOINT);
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
            ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<Parameter>>() {
            });

            parameters = objectReader.readValue(output);

            if(parameters.isEmpty()) {
                throw new MissingParameterException("Empty parameter list retrieved from ScrapperConfig!!");
            }
        }

        conn.disconnect();

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


