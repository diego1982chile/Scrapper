package cl.ctl.scrapper;

import cl.ctl.scrapper.helpers.FilesHelper;
import cl.ctl.scrapper.scrappers.ConstrumartScrapper;
import cl.ctl.scrapper.scrappers.EasyScrapper;
import cl.ctl.scrapper.scrappers.SodimacScrapper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

/**
 * Created by root on 07-12-20.
 */
public class MouseGlider {

    public static void main(String... args) throws Exception {


        ConstrumartScrapper construmartScrapper = new ConstrumartScrapper();

        construmartScrapper.scrap();

        EasyScrapper easyScrapper = new EasyScrapper();

        easyScrapper.scrap();

        SodimacScrapper sodimacScrapper = new SodimacScrapper();

        sodimacScrapper.scrap();

        FilesHelper.getInstance().processFiles();


    }


}
