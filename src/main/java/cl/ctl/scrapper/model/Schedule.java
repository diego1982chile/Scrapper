package cl.ctl.scrapper.model;

import org.apache.james.mime4j.field.datetime.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 22-04-21.
 */
public class Schedule {

    String holding;
    Date schedule;

    public Schedule(String holding, Date schedule) {
        this.holding = holding;
        this.schedule = schedule;
    }

    public String getHolding() {
        return holding;
    }

    public void setHolding(String holding) {
        this.holding = holding;
    }

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "{" + holding + "," + schedule + "}";
    }
}
