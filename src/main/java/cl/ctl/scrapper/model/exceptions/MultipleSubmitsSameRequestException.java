package cl.ctl.scrapper.model.exceptions;

public class MultipleSubmitsSameRequestException extends Exception {

    public MultipleSubmitsSameRequestException(String msg) {
        super(msg);
    }
}
