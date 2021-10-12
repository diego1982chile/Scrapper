package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.scrappers.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ScrapperHelper {

    public static final ScrapperHelper instance = new ScrapperHelper();

    public Map<String, AbstractScrapper> scrappers = new HashMap<>();

    private static final Logger logger = Logger.getLogger(ScrapperHelper.class.getName());

    /**
     * Constructor privado para el Singleton del Factory.
     */
    public ScrapperHelper() {

        try {
            // Scrappers Legrand
            ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();
            EasyScrapper easyScrapper = new EasyScrapper();
            SodimacScrapper sodimacScrapper = new SodimacScrapper();
            CencosudScrapper cencosudScrapperLegrand = new CencosudScrapper("Legrand");

            // Scrappers Nutrisa
            SmuScrapper smuScrapper = new SmuScrapper();
            TottusScrapper tottusScrapper = new TottusScrapper();
            CencosudScrapper cencosudScrapper = new CencosudScrapper();
            WalMartScrapper walMartScrapper = new WalMartScrapper();

            // Scrappers Bless
            CencosudScrapper cencosudScrapperBless = new CencosudScrapper("Bless");
            TottusScrapper tottusScrapperBless = new TottusScrapper("Bless");
            SmuScrapper smuScrapperBless = new SmuScrapper("Bless");
            WalMartScrapper walMartScrapperBless = new WalMartScrapper("Bless");

            // Scrappers Soho
            CencosudScrapper cencosudScrapperSoho = new CencosudScrapper("Soho");
            SmuScrapper smuScrapperSoho = new SmuScrapper("Soho");
            WalMartScrapper walMartScrapperSoho = new WalMartScrapper("Soho");

            // Scrappers Polar
            TottusScrapper tottusScrapperPolar = new TottusScrapper("Polar");
            WalMartScrapper walMartScrapperPolar = new WalMartScrapper("Polar");

            scrappers.put(construmartScrapper.toString(), construmartScrapper);
            scrappers.put(easyScrapper.toString(), easyScrapper);
            scrappers.put(sodimacScrapper.toString(), sodimacScrapper);
            scrappers.put(cencosudScrapperLegrand.toString(), cencosudScrapperLegrand);

            scrappers.put(smuScrapper.toString(), smuScrapper);
            scrappers.put(cencosudScrapper.toString(), cencosudScrapper);
            scrappers.put(tottusScrapper.toString(), tottusScrapper);
            scrappers.put(walMartScrapper.toString(), walMartScrapper);

            scrappers.put(cencosudScrapperBless.toString(), cencosudScrapperBless);
            scrappers.put(tottusScrapperBless.toString(), tottusScrapperBless);
            scrappers.put(smuScrapperBless.toString(), smuScrapperBless);
            scrappers.put(walMartScrapperBless.toString(), walMartScrapperBless);

            scrappers.put(cencosudScrapperSoho.toString(), cencosudScrapperSoho);
            scrappers.put(smuScrapperSoho.toString(), smuScrapperSoho);
            scrappers.put(walMartScrapperSoho.toString(), walMartScrapperSoho);

            scrappers.put(tottusScrapperPolar.toString(), tottusScrapperPolar);
            scrappers.put(walMartScrapperPolar.toString(), walMartScrapperPolar);

        }
        catch(IOException e) {

        }

    }

    public static ScrapperHelper getInstance() {
        return instance;
    }

    public Map<String, AbstractScrapper> getScrappersByHolding(String holding) {

        Map<String, AbstractScrapper> scrappers = new TreeMap<>();

        for (AbstractScrapper scrapper : this.scrappers.values()) {
            if(scrapper.getCadena().equalsIgnoreCase(holding)) {
                scrappers.put(scrapper.toString(), scrapper);
            }
        }

        return scrappers;
    }

    public Map<String, AbstractScrapper> getScrappersByClient(String client) {

        Map<String, AbstractScrapper> scrappers = new TreeMap<>();

        for (AbstractScrapper scrapper : this.scrappers.values()) {
            if(scrapper.getHolding().equalsIgnoreCase(client)) {
                scrappers.put(scrapper.toString(), scrapper);
            }
        }

        return scrappers;
    }

    static Object createObject(String className) {
        Object object = null;
        try {
            Class classDefinition = Class.forName(className);
            object = classDefinition.newInstance();
        } catch (InstantiationException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return object;
    }
}
