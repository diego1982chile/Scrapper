package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.Parameter;
import cl.ctl.scrapper.model.Schedule;
import cl.ctl.scrapper.model.Token;
import cl.ctl.scrapper.model.exceptions.MissingParameterException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cl.ctl.scrapper.model.ParameterEnum.*;

/**
 * Created by des01c7 on 18-12-20.
 */
public class TokenHelper {

    private static final TokenHelper instance = new TokenHelper();

    private static Timer timer;

    /** Logger para la clase */
    private static Logger logger;

    static LogHelper fh;

    private static String TOKEN_ENDPOINT;

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private TokenHelper() {

        TOKEN_ENDPOINT = ConfigHelper.getInstance().getParameter(BASE_URL_TOKEN.getParameter()) + "auth";

        timer  = new Timer();
        fh = LogHelper.getInstance();
        logger = Logger.getLogger(TokenHelper.class.getName());
        logger.addHandler(fh);

        /*
        try {
            updateToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }

    public static TokenHelper getInstance() {
        return instance;
    }

    public void start() {
        timer.schedule( new TimerTask() {
            public void run() {
                try {
                    updateToken();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            }
        }, 0, 30*60*1000);
    }

    private void updateToken() throws IOException {

        URL url = new URL(TOKEN_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // For POST only - START
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();

        String username = ConfigHelper.getInstance().getParameter(USER_NAME.getParameter());
        String password = ConfigHelper.getInstance().getParameter(PASSWORD.getParameter());

        //String jsonInputString = "{\"username\": \"" + user + "\", \"password\": \"" + password + "\"}";

        JSONObject user = new JSONObject();

        user.put("username", username);
        user.put("password", password);

        os.write(user.toString().getBytes("utf-8"));
        os.flush();
        os.close();

        // For POST only - END

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        String output;

        while ((output = br.readLine()) != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(output);

            Token token = mapper.treeToValue(tree, Token.class);

            ConfigHelper.getInstance().setParameter(TOKEN.getParameter(), token.getToken());
        }

        conn.disconnect();

    }



}


