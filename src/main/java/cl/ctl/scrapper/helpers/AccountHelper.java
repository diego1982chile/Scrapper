package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.Account;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cl.ctl.scrapper.model.ParameterEnum.TOKEN;

/**
 * Created by root on 10-09-21.
 */
public class AccountHelper {

    public static final AccountHelper instance = new AccountHelper();

    public Map<String, String> CONFIG = new HashMap<>();

    private static final String ENDPOINT = "http://localhost:8181/AccountService/api/accounts/";

    private static final Logger logger = Logger.getLogger(ConfigHelper.class.getName());

    /**
     * Constructor privado para el Singleton del Factory.
     */
    public AccountHelper() {

    }

    public Account getAccountByClientAndRetailer(String client, String retailer) {

        Account account = new Account();

        client = client.toLowerCase();
        retailer = retailer.toLowerCase();

        try {

            URL url = new URL(ENDPOINT + client.toLowerCase() + "/" + retailer.toLowerCase());
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
            System.out.println("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tree = mapper.readTree(output);

                account = mapper.treeToValue(tree, Account.class);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return  account;

    }



    public static AccountHelper getInstance() {
        return instance;
    }
}
