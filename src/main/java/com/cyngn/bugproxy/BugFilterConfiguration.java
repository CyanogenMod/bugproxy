package com.cyngn.bugproxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;


public class BugFilterConfiguration extends Configuration{
    private String oldBuilds;
    private String reallyOldBuilds;
    private String ancientBuilds;
    private String oldCommercial;

    private String invalidCM13;
    private String fixedCM13;
    private String invalidCM12;
    private String fixedCM12;

    public BugFilterConfiguration(){

    }

    @JsonProperty
    public String getInvalidCM13(){
        return invalidCM13;
    }

    @JsonProperty
    public void setInvalidCM13(String invCM13){
        this.invalidCM13 = invCM13;
    }


    public String getAncientBuilds() {
        return ancientBuilds;
    }

    @JsonProperty
    public void setAncientBuilds(String ancientBuilds) {
        this.ancientBuilds = ancientBuilds;
    }

    @JsonProperty
    public String getOldBuilds() {
        return oldBuilds;
    }

    @JsonProperty
    public void setOldBuilds(String oldBuilds) {
        this.oldBuilds = oldBuilds;
    }

    @JsonProperty
    public String getReallyOldBuilds() {
        return reallyOldBuilds;
    }

    @JsonProperty
    public void setReallyOldBuilds(String reallyOldBuilds) {
        this.reallyOldBuilds = reallyOldBuilds;
    }

    @JsonProperty
    public String getOldCommercial() {
        return oldCommercial;
    }

    @JsonProperty
    public void setOldCommercial(String oldCommercial) {
        this.oldCommercial = oldCommercial;
    }

    @JsonProperty
    public String getFixedCM13() {
        return fixedCM13;
    }

    @JsonProperty
    public void setFixedCM13(String fixedCM13) {
        this.fixedCM13 = fixedCM13;
    }

    @JsonProperty
    public String getInvalidCM12() {
        return invalidCM12;
    }

    @JsonProperty
    public void setInvalidCM12(String invalidCM12) {
        this.invalidCM12 = invalidCM12;
    }

    @JsonProperty
    public String getFixedCM12() {
        return fixedCM12;
    }

    @JsonProperty
    public void setFixedCM12(String fixedCM12) {
        this.fixedCM12 = fixedCM12;
    }

}
