package com.cyngn.bugproxy.resources;

import com.codahale.metrics.annotation.Timed;
import com.cyngn.bugproxy.core.*;
import com.cyngn.bugproxy.services.BugDumpClient;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/crash")
@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
public class BugProxyResource {

    private String PROJECTNAME;
    private CrashMap theMap;
    private BugDumpClient bdClient;
    private final AtomicLong counter;
    private CrashFilter crashFilter;
    private String JIRAAuth;
    private String dogeAuth;


    public BugProxyResource(CrashMap incommingMap, BugDumpClient bdClient, CrashFilter cFilter, String projectID,
                            String defaultAuth, String dogeAuth) {
        this.theMap = incommingMap;
        this.bdClient = bdClient;
        this.counter = new AtomicLong();
        this.crashFilter = cFilter;
        this.PROJECTNAME = projectID;
        this.JIRAAuth = defaultAuth;
        this.dogeAuth = dogeAuth;
    }

    @POST
    @Timed
    public Response showCrash(@Valid CrashWrapper cWrap) {
        //System.out.println(cWrap.getProject());

        Crash crash = cWrap.toCrash();
        if(cWrap.getProject().equalsIgnoreCase("{id=11600}")){
            return bdClient.sendCrash(crash, "11600", dogeAuth);
        }

        if (crash.getlabels().contains("crash")) {

            if (!crashFilter.isValid(crash)) {
                return new Response("invalidCrash", "noThanks", "bye");
            }

            if (crash.getSummary().contains("Native crash")) {
                return bdClient.sendCrash(crash, PROJECTNAME, JIRAAuth);
            }
            System.out.print(" adding ");
            return theMap.addToCrashMap(crashFilter.cleanOutUnique(crash), bdClient);
        }
        System.out.print(" bugreport subject: " + crash.getSummary() + " ");
        return bdClient.sendCrash(crash, PROJECTNAME, JIRAAuth);
    }
}
