package cl.dsoto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by des01c7 on 18-12-20.
 */
public class FileControl {

    String processDay;
    String dayOfWeekProcess;
    String dayOfWeek;
    String frequency;
    String holding;
    String fileName;
    String status;

    List<FileControlError> errors = new ArrayList<FileControlError>();

    public FileControl(String processDay, String dayOfWeekProcess, String dayOfWeek, String frequency, String holding, String fileName, String status) {
        this.processDay = processDay;
        this.dayOfWeekProcess = dayOfWeekProcess;
        this.dayOfWeek = dayOfWeek;
        this.frequency = frequency;
        this.holding = holding;
        this.fileName = fileName;
        this.status = status;
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

    public List<FileControlError> getErrors() {
        return errors;
    }

    public void setErrors(List<FileControlError> errors) {
        this.errors = errors;
    }
}
