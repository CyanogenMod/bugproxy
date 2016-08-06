package com.cyngn.bugproxy;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BugProxyConfiguration extends Configuration {
    @NotEmpty
    private String port;
    private String jiraproject;
    private String jiradefaultauth;
    private String jiradogeauth;
    private int purgeDelay;
    private long maxAge;

    @Valid
    @NotNull
    @JsonProperty
    private HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();

    @JsonProperty
    public String getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(String port) {
        this.port = port;
    }

    public HttpClientConfiguration getHttpClientConfiguration() {
        return httpClientConfiguration;
    }

    @Valid
    @JsonProperty
    private BugFilterConfiguration bugFilterConfiguration = new BugFilterConfiguration();

    @JsonProperty
    public BugFilterConfiguration getBugFilterConfiguration(){
        return bugFilterConfiguration;
    }

    @JsonProperty
    public String getjiraproject(){ return this.jiraproject; }

    @JsonProperty
    public void setjiraproject(String jiraProject){ this.jiraproject = jiraProject; }

    @JsonProperty
    public String getjiradefaultauth(){ return this.jiradefaultauth; }

    @JsonProperty
    public void setjiradefaultauth(String jiraauth){ this.jiradefaultauth = jiraauth; }

    @JsonProperty
    public String getjiradogeauth(){ return this.jiradogeauth; }

    @JsonProperty
    public void setjiradogeauth(String jiraauth){ this.jiradogeauth = jiraauth; }

    @JsonProperty
    public int getpurgeDelay(){
        return purgeDelay;
    }
    @JsonProperty
    public void setpurgeDelay(int pD){
        this.purgeDelay = pD;
    }
    @JsonProperty
    public long getmaxAge(){
        return maxAge;
    }
    @JsonProperty
    public void setmaxAge(long mA){
        this.maxAge = mA;
    }
}

