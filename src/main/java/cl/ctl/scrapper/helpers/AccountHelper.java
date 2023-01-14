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

    public Account getAccountByClientAndHolding(String retailer, String holding) {

        Account account = new Account();

        retailer = retailer.toLowerCase();
        holding = holding.toLowerCase();

        try {

            URL url = new URL(ENDPOINT + retailer.toLowerCase() + "/" + holding.toLowerCase());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

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
                account = mapper.treeToValue(tree, Account.class);

                System.out.println(output);
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
