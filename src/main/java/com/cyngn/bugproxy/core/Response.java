package com.cyngn.bugproxy.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {


    private String id;
    private String key;
    private String self;

    public Response() {

    }

    public Response(String id, String key, String self) {
        this.id = id;
        this.key = key;
        this.self = self;
    }


    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getKey() {
        return key;
    }

    @JsonProperty
    public String getSelf() {
        return self;
    }
}
