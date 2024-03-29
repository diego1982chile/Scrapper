package cl.ctl.scrapper.scrappers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.LogHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.model.exceptions.ScrapAlreadyExistsException;
import cl.ctl.scrapper.model.FileControl;
import cl.ctl.scrapper.model.exceptions.ScrapUnavailableException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by des01c7 on 16-12-20.
 */
public abstract class AbstractScrapper implements Runnable {

    WebDriver driver;

    /** Logger para la clase */
    Logger logger = Logger.getLogger(ConstrumartScrapper.class.getName());
    LogHelper fh = LogHelper.getInstance();

    String holding;

    String cadena;

    String url;

    String logo;

    String fileExt = ".csv";

    boolean onlyDiary = false;

    boolean onlyWeekly = false;

    int downloads = 0;

    boolean readyOnMorning = true;

    List<FileControl> fileControlList = new ArrayList<>();

    List<String> newScraps = new ArrayList<>();


    public AbstractScrapper() throws IOException {
        // This block configure the logger with handler and formatter
        try {
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.addHandler(fh);

        } catch (SecurityException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public FileControl getDailyFileControl() {
        for (FileControl fileControl : fileControlList) {
            if(fileControl.getFrequency().equalsIgnoreCase("Dia")) {
                return  fileControl;
            }
        }
        return null;
    }

    public FileControl getMonthlyFileControl() {
        for (FileControl fileControl : fileControlList) {
            if(fileControl.getFrequency().equalsIgnoreCase("Mes")) {
                return  fileControl;
            }
        }
        return null;
    }

    public FileControl getWeeklyFileControl() {
        for (FileControl fileControl : fileControlList) {
            if(fileControl.getFrequency().equalsIgnoreCase("Dom")) {
                return  fileControl;
            }
        }
        return null;
    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public boolean isOnlyDiary() {
        return onlyDiary;
    }

    public void setOnlyDiary(boolean onlyDiary) {
        this.onlyDiary = onlyDiary;
    }

    public String getHolding() {
        return holding;
    }

    public void setHolding(String holding) {
        this.holding = holding;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public List<FileControl> getFileControlList() {
        return fileControlList;
    }

    public void setFileControlList(List<FileControl> fileControlList) {
        this.fileControlList = fileControlList;
    }

    public List<String> getNewScraps() {
        return newScraps;
    }

    public void setNewScraps(List<String> newScraps) {
        this.newScraps = newScraps;
    }

    void checkScraps() throws ScrapAlreadyExistsException {
        if(FilesHelper.getInstance().checkFiles(this)) {
            throw new ScrapAlreadyExistsException("Scrapper '" + cadena + "' -> Archivos ya fueron generados! se omite el proceso");
        }
    }

    void checkScrap(String freq) throws ScrapAlreadyExistsException {

        if(FilesHelper.getInstance().checkFile(this, freq)) {
            throw new ScrapAlreadyExistsException("Scrapper " + this.getCadena() + " -> Archivo de frecuencia '" + freq + "' ya fue generado! se omite el proceso diario");
        }
    }

    private void initializeDriver() {

        WebDriverManager.chromedriver().setup();

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", FilesHelper.getInstance().getDownloadPath());

        ChromeOptions chrome_options = new ChromeOptions();
        chrome_options.addArguments("--no-sandbox");
        chrome_options.addArguments("--disable-dev-shm-usage");
        //chrome_options.addArguments("--headless");
        chrome_options.addArguments("--start-maximized");

        //DesiredCapabilities cap = DesiredCapabilities.chrome();
        //cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        //cap.setCapability(ChromeOptions.CAPABILITY, chrome_options);

        chrome_options.setExperimentalOption("prefs", chromePrefs);

        driver = new ChromeDriver(chrome_options);

        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
    }

    private void renameFile(String cadena, int count) {

        String freq;

        switch (count) {
            case 1:
                freq = "DAY";
                break;
            case 2:
                freq = "MONTH";
                break;
            case 3:
                freq = "WEEK";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + count);
        }

        FilesHelper.getInstance().renameLastFile(this, freq);
    }

    abstract void doScrap(String since, String until) throws Exception;

    abstract void login() throws Exception;


    private void scrap(boolean flag) throws Exception {

        if(flag) {
            initializeDriver();
        }

        checkScraps();

        if(flag) {

            int cont = 0;

            while(cont < 1) {

                if(!readyOnMorning) {
                    //TODO: Si son antes de las 14:00 omitir el login
                    if(LocalDateTime.now().getHour() <= 14) {
                        //throw new ScrapUnavailableException("Scrap para cliente " + ProcessHelper.getInstance().getClient() + " aún no se encuentra disponible!");
                        logger.log(Level.WARNING, "Scrap para cliente " + ProcessHelper.getInstance().getClient() + " aún no se encuentra disponible!");
                        break;
                    }
                }

                cont++;

                try {
                    Thread.sleep(2000);

                    driver.get(url);

                    Thread.sleep(2000);

                    login();

                    Thread.sleep(2000);

                    break;
                }
                catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage());
                    if(cont >= 1) {
                        logger.log(Level.SEVERE, e.getMessage());
                        //throw e;
                    }
                }

            }

        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");

        String since = formatter.format(ProcessHelper.getInstance().getProcessDate());
        String until = since;

        // Generar Scrap Diario
        if(flag) {
            logger.log(Level.INFO, "Descargando Scrap Diario " + cadena + "...");

            generateScrap(since, until, 1, flag);
            Thread.sleep(2000);
        }

        // Si la cadena genera solo scraps diarios retornar en este punto

        if(onlyDiary) {
            if(flag) {
                driver.quit();
            }
            return;
        }

        // Generar Scrap Mensual

        since = formatter.format(ProcessHelper.getInstance().getProcessDate().minusDays(ProcessHelper.getInstance().getProcessDate().getDayOfMonth()).plusDays(1));

        if(flag) {
            logger.log(Level.INFO, "Descargando Scrap Mensual " + cadena + "...");
            generateScrap(since, until, 2, flag);
            Thread.sleep(2000);
        }

        // Si es proceso de Domingo
        // Generar Scrap Semanal
        if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            since = formatter.format(ProcessHelper.getInstance().getProcessDate().minusDays(6));
            if(flag) {
                logger.log(Level.INFO, "Descargando Scrap Semanal " + cadena + "...");
                generateScrap(since, until, 3, flag);
            }
        }

        if(flag) {
            Thread.sleep(2000);
            driver.quit();
        }

    }


    private void generateScrap(String since, String until, int count, boolean flag) throws Exception {

        String freq = "DAY";

        switch (count) {
            case 1:
                freq = "DAY";
                break;
            case 2:
                freq = "MONTH";
                break;
            case 3:
                freq = "WEEK";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + count);
        }

        try {
            checkScrap(freq);

            if(flag) {
                if(!readyOnMorning) {
                    //TODO: Si son antes de las 14:00 omitir el scrapping
                    if(LocalDateTime.now().getHour() <= 14) {
                        throw new ScrapUnavailableException("Scrap " + freq + " para cliente " + ProcessHelper.getInstance().getClient() + " aún no se encuentra disponible");
                    }
                }
                doScrap(since, until);
                FilesHelper.getInstance().checkLastFile(this, freq);
                FilesHelper.getInstance().registerFileControlNew(this, freq);
                downloads++;

            }
        }
        catch(ScrapAlreadyExistsException e) {
            if(onlyDiary) {
                if(freq.equals("DAY")) {
                    logger.log(Level.WARNING, e.getMessage());
                    FilesHelper.getInstance().registerFileControlOK(this, freq);
                }
            }
            else {
                logger.log(Level.WARNING, e.getMessage());
                FilesHelper.getInstance().registerFileControlOK(this, freq);
            }
        }
        catch(Exception e2) {
            logger.log(Level.SEVERE, e2.getMessage());
            FilesHelper.getInstance().registerFileControlError(this, freq, e2.getMessage());
        }
        finally {
            if(flag) {
                renameFile(cadena, count);
            }
        }
    }

    @Override
    public void run() {

        fileControlList.clear();

        try {
            scrap(true);
        }
        catch(ScrapAlreadyExistsException e) {
            logger.log(Level.WARNING, e.getMessage());

            FilesHelper.getInstance().registerFileControlOK(this, "DAY");

            if(!onlyDiary) {
                FilesHelper.getInstance().registerFileControlOK(this, "MONTH");

                if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    FilesHelper.getInstance().registerFileControlOK(this, "WEEK");
                }
            }

            try {
                driver.quit();
            }
            catch(Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void process(boolean flag) throws Exception {

        fileControlList.clear();

        try {
            scrap(flag);
        }
        catch(ScrapAlreadyExistsException e) {
            logger.log(Level.WARNING, e.getMessage());

            FilesHelper.getInstance().registerFileControlOK(this, "DAY");

            if(!onlyDiary) {
                FilesHelper.getInstance().registerFileControlOK(this, "MONTH");

                if(ProcessHelper.getInstance().getProcessDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    FilesHelper.getInstance().registerFileControlOK(this, "WEEK");
                }
            }

            try {
                if(flag) {
                    driver.quit();
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    @Override
    public String toString() {
        return holding + " -> " + cadena;
    }

    String calculateFrequency(String since, String until) {

        String freq = "Dom";

        if(since.equals(until)) {
            freq = "Dia";
        }

        if(since.split("-")[0].equals("01")) {
            freq = "Mes";
        }

        return freq;
    }


}
