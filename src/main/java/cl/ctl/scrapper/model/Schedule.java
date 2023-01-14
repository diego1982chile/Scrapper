package cl.ctl.scrapper.model;

import org.apache.james.mime4j.field.datetime.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 22-04-21.
 */
public class Schedule {

    Retailer retailer;
    Date schedule;

    public Schedule() {
    }

    public Schedule(Retailer retailer, Date schedule) {
        this.retailer = retailer;
        this.schedule = schedule;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public void setRetailer(Retailer retailer) {
        this.retailer = retailer;
    }

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "{" + retailer.getName() + "," + schedule + "}";
    }
}
