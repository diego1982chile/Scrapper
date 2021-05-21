package cl.ctl.scrapper.model.exceptions;

public class ConcurrentAccessException extends Exception {

    public ConcurrentAccessException(String msg) {
        super(msg);
    }
}
