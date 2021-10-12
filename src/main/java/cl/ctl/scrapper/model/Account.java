package cl.ctl.scrapper.model;


/**
 * Created by root on 09-08-21.
 */
public class Account {

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id;

    private String company;

    private String user;

    private String password;

    private Client client;

    private Holding holding;

    public Account() {
    }

    public Account(long id, String company, String user, String password, Client client, Holding holding) {
        this.id = id;
        this.company = company;
        this.user = user;
        this.password = password;
        this.client = client;
        this.holding = holding;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Holding getHolding() {
        return holding;
    }

    public void setHolding(Holding holding) {
        this.holding = holding;
    }
}
