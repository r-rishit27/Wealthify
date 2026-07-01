package com.example.portfoliomanager.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FinnhubCandleDTO {

    @JsonProperty("status")
    @JsonAlias("s")
    private String status;

    @JsonProperty("timestamps")
    @JsonAlias("t")
    private List<Long> timestamps;

    @JsonProperty("opens")
    @JsonAlias("o")
    private List<Double> opens;

    @JsonProperty("highs")
    @JsonAlias("h")
    private List<Double> highs;

    @JsonProperty("lows")
    @JsonAlias("l")
    private List<Double> lows;

    @JsonProperty("closes")
    @JsonAlias("c")
    private List<Double> closes;

    @JsonProperty("volumes")
    @JsonAlias("v")
    private List<Double> volumes;

    public FinnhubCandleDTO() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }

    public List<Double> getOpens() {
        return opens;
    }

    public void setOpens(List<Double> opens) {
        this.opens = opens;
    }

    public List<Double> getHighs() {
        return highs;
    }

    public void setHighs(List<Double> highs) {
        this.highs = highs;
    }

    public List<Double> getLows() {
        return lows;
    }

    public void setLows(List<Double> lows) {
        this.lows = lows;
    }

    public List<Double> getCloses() {
        return closes;
    }

    public void setCloses(List<Double> closes) {
        this.closes = closes;
    }

    public List<Double> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Double> volumes) {
        this.volumes = volumes;
    }
}
