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

            ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();
            EasyScrapper easyScrapper = new EasyScrapper();
            SodimacScrapper sodimacScrapper = new SodimacScrapper();
            CencosudScrapper cencosudScrapperLegrand = new CencosudScrapper("Legrand");

            SmuScrapper smuScrapper = new SmuScrapper();
            TottusScrapper tottusScrapper = new TottusScrapper();
            CencosudScrapper cencosudScrapper = new CencosudScrapper();
            WalMartScrapper walMartScrapper = new WalMartScrapper();

            CencosudScrapper cencosudScrapperBless = new CencosudScrapper("Bless");
            TottusScrapper tottusScrapperBless = new TottusScrapper("Bless");

            SmuScrapper smuScrapperBless = new SmuScrapper("Bless");
            WalMartScrapper walMartScrapperBless = new WalMartScrapper("Bless");

            CencosudScrapper cencosudScrapperSoho = new CencosudScrapper("Soho");
            SmuScrapper smuScrapperSoho = new SmuScrapper("Soho");
            WalMartScrapper walMartScrapperSoho = new WalMartScrapper("Soho");

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

        }
        catch(IOException e) {

        }

    }

    public static ScrapperHelper getInstance() {
        return instance;
    }

    public List<AbstractScrapper> getScrappersByHolding(String holding) {

        List<AbstractScrapper> scrappers = new ArrayList<>();

        for (AbstractScrapper scrapper : this.scrappers.values()) {
            if(scrapper.getCadena().equalsIgnoreCase(holding)) {
                scrappers.add(scrapper);
            }
        }

        return scrappers;
    }
}
