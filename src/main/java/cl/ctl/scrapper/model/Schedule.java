package cl.ctl.scrapper.model;

import org.apache.james.mime4j.field.datetime.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 22-04-21.
 */
public class Schedule {

    String client;
    Date schedule;

    public Schedule(String client, Date schedule) {
        this.client = client;
        this.schedule = schedule;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }
}
