package com.solution.it.newsoft.model;

import java.util.ArrayList;

public class Listing {

    private ArrayList<List> listing;
    private Status status;

    public ArrayList<List> getListing() {
        return listing;
    }

    public void setListing(ArrayList<List> listing) {
        this.listing = listing;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
