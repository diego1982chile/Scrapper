package cl.ctl.scrapper.model;

public class DateOutOfRangeException extends Exception {

    public DateOutOfRangeException(String msg) {
        super("La Fecha '" + msg + "' está fuera del rango permitido por el sitio B2B" );
    }
}
