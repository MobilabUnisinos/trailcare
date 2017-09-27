package br.unisinos.hefestos.pojo;

import java.io.Serializable;
import java.util.Date;

public class PTrail implements Serializable{

    private int id;
    private Resource resource;
    private Date date;
    private User user;

    public PTrail() {
    }

    public PTrail(Resource resource, Date date, User user) {
        this.resource = resource;
        this.date = date;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PTrail{" +
                "id=" + id +
                ", resource=" + resource +
                ", date=" + date +
                '}';
    }
}
