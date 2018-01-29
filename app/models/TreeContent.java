package models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TreeContent implements Serializable {

    private int id;
    private int nodeId;
    private String lang;
    private String name;
    private Set<Product> products = new HashSet<>();

    public TreeContent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
