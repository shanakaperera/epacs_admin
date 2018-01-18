package models;

import java.io.Serializable;

public class Tree implements Serializable {

    private int nodeId;
    private String name;

    public Tree() {
    }

    public Tree(int nodeId, String name) {
        this.nodeId = nodeId;
        this.name = name;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
