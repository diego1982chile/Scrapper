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

    public Account getAccountByClientAndHolding(String client, String holding) {

        Account account = new Account();

        client = client.toLowerCase();
        holding = holding.toLowerCase();

        try {

            URL url = new URL(ENDPOINT + client.toLowerCase() + "/" + holding.toLowerCase());
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

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        finally {
            // Si no se logró traer la account desde el servicio, setearla desde el archivo de configuración
            if(account.getId() == 0) {
                account.setUser(ConfigHelper.getInstance().CONFIG.get(client + "." + holding + ".user"));
                account.setPassword(ConfigHelper.getInstance().CONFIG.get(client + "." + holding + ".password"));
                try {
                    account.setCompany(ConfigHelper.getInstance().CONFIG.get(client + "." + holding + ".company"));
                }
                catch (Exception e) {
                    logger.log(Level.WARNING, "Esta cuenta no posee el atributo Company, se omite");
                }
            }
            return  account;
        }

    }



    public static AccountHelper getInstance() {
        return instance;
    }
}
