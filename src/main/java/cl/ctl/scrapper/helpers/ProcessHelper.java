package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.scrappers.*;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ProcessHelper {

    private static final ProcessHelper instance = new ProcessHelper();

    private LocalDate processDate  = LocalDate.now().minusDays(1);

    private Map<String, AbstractScrapper> scrappers = new TreeMap<>();

    private ExecutorService executor;

    private CyclicBarrier barrier;

    //private Semaphore semaphore;

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ProcessHelper() {

        try {
            //semaphore = new Semaphore(1);

            processDate  = LocalDate.now().minusDays(1);

            if(scrappers.isEmpty()) {
                //semaphore.tryAcquire();
                initScrappers();
            }
            executor = Executors.newFixedThreadPool(scrappers.size());
            barrier = new CyclicBarrier(scrappers.size() + 1);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProcessHelper getInstance() {
        return instance;
    }

    public LocalDate getProcessDate() {
        return processDate;
    }

    public void setProcessDate(LocalDate processDate) throws IOException {
        if(!this.processDate.equals(processDate)) {
            this.processDate = processDate;
            FilesHelper.getInstance().flushProcessName();
            initScrappers();
        }
    }

    public Map<String, AbstractScrapper> getScrappers() {
        return scrappers;
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
    
    public void setScrappers(List<String> scrappers) {

        this.scrappers.clear();

        String packageName = this.getClass().getPackage().getName();

        packageName = packageName.replace("helpers","scrappers");

        for (String scrapper : scrappers) {
            String className = StringUtils.capitalize(scrapper.toLowerCase());
            className = className + "Scrapper";
            AbstractScrapper abstractScrapper = (AbstractScrapper) createObject(packageName + "." + className);
            if(abstractScrapper == null) {
                className = scrapper + "Scrapper";
                abstractScrapper = (AbstractScrapper) createObject(packageName + "." + className);
            }
            this.scrappers.put(abstractScrapper.toString(), (AbstractScrapper) createObject(packageName + "." + className));
        }
    }

    public void finishProcess() {
        //semaphore.release();
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


    private void initScrappers() throws IOException {
        ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();
        EasyScrapper easyScrapper = new EasyScrapper();
        SodimacScrapper sodimacScrapper = new SodimacScrapper();
        SmuScrapper smuScrapper = new SmuScrapper();
        TottusScrapper tottusScrapper = new TottusScrapper();
        CencosudScrapper cencosudScrapper = new CencosudScrapper();
        WalMartScrapper walMartScrapper = new WalMartScrapper();

        scrappers.put(construmartScrapper.toString(), construmartScrapper);
        scrappers.put(easyScrapper.toString(), easyScrapper);
        scrappers.put(sodimacScrapper.toString(), sodimacScrapper);
        scrappers.put(smuScrapper.toString(), smuScrapper);
        scrappers.put(cencosudScrapper.toString(), cencosudScrapper);
        scrappers.put(tottusScrapper.toString(), tottusScrapper);
        scrappers.put(walMartScrapper.toString(), walMartScrapper);

        executor = Executors.newFixedThreadPool(scrappers.size());

        barrier = new CyclicBarrier(scrappers.size() + 1);
    }


}
