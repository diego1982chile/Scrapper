package cl.ctl.scrapper.model;

public class ConcurrentAccessException extends Exception {

    public ConcurrentAccessException(String msg) {
        super(msg);
    }
}
