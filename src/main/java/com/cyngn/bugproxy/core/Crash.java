package com.cyngn.bugproxy.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Crash {

    //private String project  = "{ \"id\" : \"11800\" }";
    private String summary;
    private String description;
    private List<String> labels;
    private String customfield_10800; //buildID
    private String customfield_10104; //kernel
    private int customfield_10900;    //number of dupes

    public Crash() {
        //jackson deseralizerifications
    }

    public Crash(String summary, String description,
                 String buildId, String kernel, List<String> labels) {

        this.summary = summary;
        this.description = description;
        this.customfield_10800 = buildId;
        this.customfield_10104 = kernel;
        this.labels = labels;
    }

    public Crash(String summary, String description,
                 String buildId, String kernel, int dupes, List<String> labels) {
        this.summary = summary;
        this.description = description;
        this.customfield_10800 = buildId;
        this.customfield_10104 = kernel;
        this.customfield_10900 = dupes;
        this.labels = labels;
    }

    @JsonProperty
    public String getSummary() {
        return summary;
    }

    @JsonProperty
    public void setSummary(String sumry) {
        this.summary = sumry;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public void setDescription(String descr) {
        this.description = descr;
    }

    @JsonProperty
    public String getcustomfield_10800() {
        return customfield_10800;
    } //buildid

    @JsonProperty
    public void setCustomfield_10800(String buildID) {
        this.customfield_10800 = buildID;
    } //buildid

    @JsonProperty
    public String getcustomfield_10104() {
        return customfield_10104;
    } //kernel

    @JsonProperty
    public List<String> getlabels() {
        return labels;
    }

    @JsonProperty
    public int getCustomfield_10900() {
        return customfield_10900;
    }  //number of dupes

    @JsonProperty
    public void setCustomfield_10900(int dupes) {
        this.customfield_10900 = dupes;
    }


}

