package com.solution.it.newsoft.model;

public class List {

    private String id;
    private String list_name;
    private String distance;

    public List(String id, String list_name, String distance) {
        this.id = id;
        this.list_name = list_name;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
