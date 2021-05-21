package cl.ctl.scrapper.helpers;

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

    private String client;

    private Map<String, AbstractScrapper> scrappers = new TreeMap<>();

    private ExecutorService executor;

    private CyclicBarrier barrier;

    private Semaphore semaphore = new Semaphore(1);

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(ProcessHelper.class.getName());

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private ProcessHelper() {

        try {

            if(scrappers.isEmpty()) {
                processDate  = LocalDate.now().minusDays(1);
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

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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
                if(scrapper.equalsIgnoreCase("WalMart")) {
                    scrapper = "WalMart";
                }
                className = scrapper + "Scrapper";
                abstractScrapper = (AbstractScrapper) createObject(packageName + "." + className);
            }
            this.scrappers.put(abstractScrapper.toString(), (AbstractScrapper) createObject(packageName + "." + className));
        }
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

    public void process(String client) throws Exception {

        try {
            if(!semaphore.tryAcquire()) {
                throw new ConcurrentAccessException("Se está intentando cambiar la fecha de proceso mientras hay un proceso en curso!!");
            }

            setClient(WordUtils.capitalize(client));

            if(UploadHelper.getInstance().signalExists(client)) {
                throw new SignalExistsException("Ya se generó el signal para el proceso " + FilesHelper.getInstance().PROCESS_NAME + " " + this.client + ". Se omite el proceso");
            }

            List<String> chains = new ArrayList<>();

            initScrappers();

            // Setear los scrappers de las cadenas correspondientes al cliente
            for (AbstractScrapper scrapper : scrappers.values()) {
                if(scrapper.getHolding().equalsIgnoreCase(client)) {
                    chains.add(scrapper.getCadena());
                }
            }

            setScrappers(chains);

            // Repasar los útltimos 3 días por si hay scraps pendientes
            LocalDate today = LocalDate.now();

            LocalDate date = today.minusDays(1);

            while(date.isBefore(today)) {

                setProcessDate(date);

                logger.log(Level.INFO, "Ejecutando Scrap proceso " + FilesHelper.getInstance().PROCESS_NAME + " para cliente " + client);

                scrap(true);

                logger.log(Level.INFO, "Fin del proceso " + FilesHelper.getInstance().PROCESS_NAME + " para cliente " + client);

                date = date.plusDays(1);

                //semaphore.release();
            }

            //logger.log(Level.INFO, "Fin del proceso general " + FilesHelper.getInstance().PROCESS_NAME + ", Se procede a subir los scraps");

            // Si no se ha generado el signal, subir archivos y enviar correo
            //if(!UploadHelper.getInstance().signalExists(client)) {
                //UploadHelper.getInstance().upload();
                //UploadHelper.getInstance().sendSignal(client);
            //}

            //UploadHelper.getInstance().sendSignal(client);

            UploadHelper.getInstance().generateSignal(client);

            // Cerrar la sesión explicitamente
            //UploadHelper.getInstance().closeSession();

            semaphore.release();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            semaphore.release();
            throw e;
        }

    }

    public void process(String process, List<String> chains) throws Exception {

        try {

            if(!semaphore.tryAcquire()) {
                throw new ConcurrentAccessException("Se está intentando cambiar la fecha de proceso mientras hay un proceso en curso!!");
            }

            if(chains == null) {
                return;
            }

            if(chains.isEmpty()) {
                return;
            }

            LocalDate date = getLocalDate(process);

            setProcessDate(date);

            validateChains(chains);

            setScrappers(chains);

            scrap(true);

            UploadHelper.getInstance().upload();

            // Cerrar la sesión explicitamente
            //UploadHelper.getInstance().closeSession();

            semaphore.release();

        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            semaphore.release();
            throw e;
        }

    }

    public void getScraps(String process) throws Exception {

        if(!semaphore.tryAcquire()) {
            throw new ConcurrentAccessException("Se está intentando cambiar la fecha de proceso mientras hay un proceso en curso!!");
        }

        if(process == null) {
            return;
        }

        LocalDate date = getLocalDate(process);

        setProcessDate(date);

        scrap(false);

        semaphore.release();

    }

    private void scrap(boolean flag) throws Exception {

        int max = 2;

        int downloads = 0;

        for (int i = 0; i < max; i++) {

            int cont = i + 1;

            logger.log(Level.INFO, "Descargando scraps -> Intento " + cont + " de " + max);

            for (AbstractScrapper scrapper : getScrappers().values()) {
                if (scrapper != null) {
                    //scrapper.setDownloads(0);
                    scrapper.process(flag);
                }
                //ProcessHelper.getInstance().getExecutor().execute(scrapper);
            }

            //ProcessHelper.getInstance().getBarrier().await();

            int errors = 0;

            for (AbstractScrapper scrapper : getScrappers().values()) {
                for (FileControl fileControl : scrapper.getFileControlList()) {
                    if (!fileControl.getErrors().isEmpty()) {
                        errors++;
                    }
                }
            }

            if (errors == 0) {
                break;
            }
        }

        for (AbstractScrapper scrapper : getScrappers().values()) {
            downloads = downloads + scrapper.getDownloads();
        }

        //Si hubo alguna descarga de algún scrapper subir los scraps para proceso actual
        //if(downloads > 0) {
            logger.log(Level.INFO, downloads + " nuevas descargas para proceso " + FilesHelper.getInstance().PROCESS_NAME + " " + client + "... ");
            logger.log(Level.INFO, "Se procede a subir los Scraps...");
            //UploadHelper.getInstance().upload();
            UploadHelper.getInstance().copy();
            //UploadHelper.getInstance().sendSignal(client);
        //}
    }


    private void validateChains(List<String> chains) throws Exception {

        for (String chain : chains) {
            if (chain.equals("")) {
                throw new Exception("Nombre de cadena '" + chain + "' no válido. Cadenas válidas: " + ProcessHelper.getInstance().getScrappers().keySet().toString());
            }
        }

        List<String> validChains = new ArrayList<>();

        for (AbstractScrapper abstractScrapper : ProcessHelper.getInstance().getScrappers().values()) {
            validChains.add(abstractScrapper.getCadena());
        }

        for (String chain : chains) {
            if(!validChains.contains(chain) && !validChains.contains(chain.toUpperCase())) {
                throw new Exception("Nombre de cadena '" + chain + "' no válido. Cadenas válidas: " + ProcessHelper.getInstance().getScrappers().keySet().toString());
            }
        }
    }

    private LocalDate getLocalDate(String process) {

        DateTimeFormatter dtf;
        LocalDate localDate;

        try {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            localDate = LocalDate.parse(process, dtf);
        }
        catch (Exception e) {
            try {
                dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                localDate = LocalDate.parse(process, dtf);
            }
            catch (Exception e1) {
                try {
                    dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    localDate = LocalDate.parse(process, dtf);
                }
                catch (Exception e2) {
                    try {
                        dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                        localDate = LocalDate.parse(process, dtf);
                    }
                    catch (Exception e3) {
                        throw e3;
                    }
                }
            }
        }

        return localDate;
    }

}


