package com.cyngn.bugproxy.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrashWrapper {
    private Map<String, Object> fields;
    private Crash crashContents;

    public CrashWrapper() {

    }

    public CrashWrapper(Crash fields) {
        this.crashContents = fields;
    }

    @JsonProperty
    public Crash getfields() {
        return crashContents;
    }

    @JsonProperty
    public String getProject(){
        return fields.get("project").toString();
    }

    public Crash toCrash(){

        try {
            String summary = new String(((String)fields.get("summary")).getBytes(), "UTF-8");
            String description = new String(((String)fields.get("description")).getBytes(), "UTF-8");
            String buildId = fields.get("customfield_10800").toString();
            String kernel = fields.get("customfield_10104").toString();
            List<String> labelList = (ArrayList <String>) fields.get("labels");
            crashContents = new Crash(summary, description, buildId, kernel, labelList );
            return crashContents;
        } catch (Exception e) {
            System.out.print ("whoops");
            return null;
        }

    }

}
