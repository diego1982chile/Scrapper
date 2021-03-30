package cl.ctl.scrapper.model;

import cl.ctl.scrapper.scrappers.AbstractScrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by des01c7 on 18-12-20.
 */
public class FileControl {

    String process;
    String processDay;
    String dayOfWeekProcess;
    String dayOfWeek;
    String executionDay;
    String frequency;
    String holding;
    String chain;
    String fileName;
    String status;

    List<String> errors = new ArrayList<String>();

    AbstractScrapper scrapper;

    public FileControl(String process, String processDay, String dayOfWeekProcess, String dayOfWeek, String executionDay, String frequency, String holding, String chain, String fileName, String status) {
        this.process = process;
        this.processDay = processDay;
        this.dayOfWeekProcess = dayOfWeekProcess;
        this.dayOfWeek = dayOfWeek;
        this.executionDay = executionDay;
        this.frequency = frequency;
        this.holding = holding;
        this.chain = chain;
        this.fileName = fileName;
        this.status = status;
    }

    public AbstractScrapper getScrapper() {
        return scrapper;
    }

    public void setScrapper(AbstractScrapper scrapper) {
        this.scrapper = scrapper;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getProcessDay() {
        return processDay;
    }

    public void setProcessDay(String processDay) {
        this.processDay = processDay;
    }

    public String getDayOfWeekProcess() {
        return dayOfWeekProcess;
    }

    public void setDayOfWeekProcess(String dayOfWeekProcess) {
        this.dayOfWeekProcess = dayOfWeekProcess;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getHolding() {
        return holding;
    }

    public void setHolding(String holding) {
        this.holding = holding;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileControl that = (FileControl) o;

        return fileName != null ? fileName.equals(that.fileName) : that.fileName == null;

    }

    @Override
    public int hashCode() {
        return fileName != null ? fileName.hashCode() : 0;
    }
}
