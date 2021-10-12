package cl.ctl.scrapper.controllers;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.helpers.LogHelper;
import cl.ctl.scrapper.helpers.ProcessHelper;
import cl.ctl.scrapper.helpers.UploadHelper;

import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by root on 16-08-21.
 */
public class UploadTask implements Runnable{

    private static final Logger logger = Logger.getLogger(TimerTask.class.getName());

    static LogHelper fh = LogHelper.getInstance();

    public UploadTask() {
        logger.addHandler(fh);
    }


    @Override
    public void run() {

        logger.log(Level.INFO, "Se procede a subir los Scraps...");

        try {
            if(UploadHelper.getInstance().getServer().equalsIgnoreCase("LOCAL")) {
                UploadHelper.getInstance().copy();
                UploadHelper.getInstance().generateSignal(ProcessHelper.getInstance().getClient());
            }
            else {
                UploadHelper.getInstance().upload();
                UploadHelper.getInstance().sendSignal(ProcessHelper.getInstance().getClient());
            }
        }
        catch (Exception ex) {

        }
    }
}
