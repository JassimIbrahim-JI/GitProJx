package com.gitpro.gitidea.models;

import java.util.List;

public class Hit {

    private List<Gallery>hits;
    private int total,totalHits;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<Gallery> getHits() {
        return hits;
    }

    public void setHits(List<Gallery> hits) {
        this.hits = hits;
    }
}
