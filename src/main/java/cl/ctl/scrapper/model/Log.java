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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Log log = (Log) o;

        if (timestamp != null ? !timestamp.equals(log.timestamp) : log.timestamp != null) return false;
        if (classname != null ? !classname.equals(log.classname) : log.classname != null) return false;
        if (method != null ? !method.equals(log.method) : log.method != null) return false;
        if (message != null ? !message.equals(log.message) : log.message != null) return false;
        return level != null ? level.equals(log.level) : log.level == null;

    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (classname != null ? classname.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        return result;
    }
}
