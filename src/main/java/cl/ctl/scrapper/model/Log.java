package cl.ctl.scrapper.model;

public class Log {

    String timestamp;
    String classname;
    String method;
    String message;
    String level;

    public Log(String timestamp, String classname, String method, String message, String level) {
        this.timestamp = timestamp;
        this.classname = classname;
        this.method = method;
        this.message = message;
        this.level = level;

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
