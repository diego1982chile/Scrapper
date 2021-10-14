package cl.ctl.scrapper.helpers;

import cl.ctl.scrapper.controllers.UploadTask;
import cl.ctl.scrapper.model.exceptions.ConcurrentAccessException;
import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.model.exceptions.SignalExistsException;
import cl.ctl.scrapper.scrappers.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 18-12-20.
 */
public class ProcessHelper {

    private static final ProcessHelper instance = new ProcessHelper();

    private LocalDate processDate  = LocalDate.now().minusDays(1);

    private String holding;

    private ExecutorService executor;

    private CyclicBarrier barrier;

    private Semaphore semaphore = new Semaphore(1);

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(ProcessHelper.class.getName());

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ProcessHelper() {
        processDate  = LocalDate.now().minusDays(1);

    }

    public static ProcessHelper getInstance() {
        return instance;
    }

    public LocalDate getProcessDate() {
        return processDate;
    }

    private void setProcessDate(LocalDate processDate) throws IOException, ConcurrentAccessException {
        if(!this.processDate.equals(processDate)) {
            //if(semaphore.tryAcquire()) {
                this.processDate = processDate;
                FilesHelper.getInstance().flushProcessName();
                //initScrappers();
            //}
            //else {
                //throw new ConcurrentAccessException("Se está intentando cambiar la fecha de proceso mientras hay un proceso en curso!!");
            //}
        }
    }


    public String getHolding() {
        return holding;
    }

    public void setHolding(String holding) {
        this.holding = holding;
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public ExecutorService getExecutor() {
        return executor;
    }


    public void process(String holding) throws Exception {

        try {
            if(!semaphore.tryAcquire()) {
                throw new ConcurrentAccessException("Se está intentando cambiar la fecha de proceso mientras hay un proceso en curso!!");
            }

            setHolding(WordUtils.capitalize(holding));

            /*
            if(UploadHelper.getInstance().signalExists(client)) {
                throw new SignalExistsException("Ya se generó el signal para el proceso " + FilesHelper.getInstance().PROCESS_NAME + " " + this.client + ". Se omite el proceso");
            }
            */

            // Repasar los útltimos 3 días por si hay scraps pendientes
            LocalDate today = LocalDate.now();

            //LocalDate date = today.minusDays(40);

            LocalDate date = today.minusDays(2);

            while(date.isBefore(today)) {

                setProcessDate(date);

                logger.log(Level.INFO, "Ejecutando Scrap proceso " + FilesHelper.getInstance().PROCESS_NAME + " para holding " + holding);

                scrap(true);

                logger.log(Level.INFO, "Fin del proceso " + FilesHelper.getInstance().PROCESS_NAME + " para holding " + holding);

                date = date.plusDays(1);

                //semaphore.release();
            }

            semaphore.release();
        }
        catch(SignalExistsException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            semaphore.release();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            semaphore.release();
            throw e;
        }

    }

    private void scrap(boolean flag) throws Exception {

        int max = 1;

        for (int i = 0; i < max; i++) {

            int cont = i + 1;

            logger.log(Level.INFO, "Descargando scraps -> Intento " + cont + " de " + max);

            Map<String, AbstractScrapper> scrappers = ScrapperHelper.getInstance().getScrappersByHolding(holding);

            //Map<String, AbstractScrapper> scrappers = ScrapperHelper.getInstance().getScrappersByClientAndHolding("Polar", holding);

            executor = Executors.newFixedThreadPool(scrappers.size());
            barrier = new CyclicBarrier(scrappers.size() + 1, new UploadTask());

            for (AbstractScrapper scrapper : scrappers.values()) {
                if (scrapper != null) {
                    //scrapper.setDownloads(0);
                    //scrapper.process(flag);
                    executor.execute(scrapper);
                    scrapper.getNewScraps().clear();
                }
                //ProcessHelper.getInstance().getExecutor().execute(scrapper);
            }

            barrier.await();

            //ProcessHelper.getInstance().getBarrier().await();

        }


    }

}


