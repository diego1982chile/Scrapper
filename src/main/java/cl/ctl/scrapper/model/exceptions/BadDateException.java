package cl.ctl.scrapper.model.exceptions;

public class BadDateException extends Exception {

    public BadDateException(String msg) {
        super("Alguna de las fechas ingresadas no es válida");
    }
}
