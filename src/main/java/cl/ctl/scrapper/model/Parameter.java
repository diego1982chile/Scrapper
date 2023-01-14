package cl.ctl.scrapper.model;


/**
 * Created by root on 09-08-21.
 */
public class Parameter {

    private Long id;

    private String name;

    private String value;

    public Parameter() {
    }

    public Parameter(Long id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
