package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.model.*;
import cl.ctl.scrapper.model.exceptions.MissingParameterException;
import cl.ctl.scrapper.scrappers.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
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
public class ScrapperHelper {

    public static final ScrapperHelper instance = new ScrapperHelper();

    static LogHelper fh;

    public Map<String, AbstractScrapper> scrappers = new HashMap<>();

    private static Logger logger;

    private static String ACCOUNTS_ENDPOINT;

    private static String CLIENTS_ENDPOINT;

    private static String RETAILERS_ENDPOINT;

    private List<Account> accounts;

    private List<Client> clients;

    private List<Retailer> retailers;

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ScrapperHelper() {

        fh = LogHelper.getInstance();
        logger = Logger.getLogger(ScrapperHelper.class.getName());
        logger.addHandler(fh);

        ACCOUNTS_ENDPOINT = ConfigHelper.getInstance().getParameter(BASE_URL_CONFIG.getParameter()) + "accounts";
        CLIENTS_ENDPOINT = ConfigHelper.getInstance().getParameter(BASE_URL_CONFIG.getParameter()) + "clients";
        RETAILERS_ENDPOINT = ConfigHelper.getInstance().getParameter(BASE_URL_CONFIG.getParameter()) + "retailers";

        try {
            populateAccounts();
            populateClients();
            populateRetailers();
        } catch (MissingParameterException | IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return;
        }

        for (Client client : clients) {

            for (Retailer retailer : retailers) {

                if(accounts.stream()
                        .filter(account -> account.getClient().getName().equals(client.getName()))
                        .filter(account -> account.getRetailer().getName().equals(retailer.getName()))
                        .findFirst().isPresent()
                        ) {

                    String packageName = AbstractScrapper.class.getPackage().getName();

                    String className = StringUtils.capitalize(retailer.getName()).concat("Scrapper");

                    String clientName = StringUtils.capitalize(client.getName());

                    AbstractScrapper scrapper = null;

                    try {
                        scrapper = (AbstractScrapper) Class.forName(packageName + "." + className)
                                .getConstructor(String.class)
                                .newInstance(clientName);

                        scrappers.put(scrapper.toString(), scrapper);

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }

                }

            }

        }

    }

    public static ScrapperHelper getInstance() {
        return instance;
    }

    Map<String, AbstractScrapper> getScrappersByRetailer(String retailer) {

        Map<String, AbstractScrapper> scrappers = new TreeMap<>();

        for (AbstractScrapper scrapper : this.scrappers.values()) {
            if(scrapper.getRetailer().equalsIgnoreCase(retailer)) {
                scrappers.put(scrapper.toString(), scrapper);
            }
        }

        return scrappers;
    }

    public Map<String, AbstractScrapper> getScrappersByClient(String client) {

        Map<String, AbstractScrapper> scrappers = new TreeMap<>();

        for (AbstractScrapper scrapper : this.scrappers.values()) {
            if(scrapper.getClient().equalsIgnoreCase(client)) {
                scrappers.put(scrapper.toString(), scrapper);
            }
        }

        return scrappers;
    }

    private void populateAccounts() throws IOException, MissingParameterException {

        URL url = new URL(ACCOUNTS_ENDPOINT);
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
            ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<Account>>() {
            });

            accounts = objectReader.readValue(output);

            if(accounts.isEmpty()) {
                throw new MissingParameterException("Empty account list retrieved from ScrapperConfig!!");
            }
        }

        conn.disconnect();

    }

    private void populateClients() throws IOException, MissingParameterException {

        URL url = new URL(CLIENTS_ENDPOINT);
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
            ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<Client>>() {
            });

            clients = objectReader.readValue(output);

            if(clients.isEmpty()) {
                throw new MissingParameterException("Empty client list retrieved from ScrapperConfig!!");
            }
        }

        conn.disconnect();

    }

    private void populateRetailers() throws IOException, MissingParameterException {

        URL url = new URL(RETAILERS_ENDPOINT);
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
            ObjectReader objectReader = mapper.reader().forType(new TypeReference<List<Retailer>>() {
            });

            retailers = objectReader.readValue(output);

            if(clients.isEmpty()) {
                throw new MissingParameterException("Empty retailer list retrieved from ScrapperConfig!!");
            }
        }

        conn.disconnect();

    }



}
