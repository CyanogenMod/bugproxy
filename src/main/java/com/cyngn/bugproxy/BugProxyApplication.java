package com.cyngn.bugproxy;

import com.cyngn.bugproxy.core.CrashFilter;
import com.cyngn.bugproxy.core.CrashMap;
import com.cyngn.bugproxy.resources.BugProxyResource;
import com.cyngn.bugproxy.services.BugDumpClient;
import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.lifecycle.setup.ScheduledExecutorServiceBuilder;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;

import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BugProxyApplication extends Application<BugProxyConfiguration> {


    public static void main(String[] args) throws Exception {
        new BugProxyApplication().run(args);
    }

    @Override
    public String getName() {
        return "BugProxy";
    }

    @Override
    public void initialize(Bootstrap<BugProxyConfiguration> bootstrap) {
        //nothing
    }

    @Override
    public void run(BugProxyConfiguration configuration,
                    Environment environment) {

        final HttpClient httpClient = new HttpClientBuilder(environment)
                .using(configuration.getHttpClientConfiguration()).build("poop");

        final String projectID = configuration.getjiraproject();
        final String jiraAuth = configuration.getjiradefaultauth();
        final String jiradogeAuth = configuration.getjiradogeauth();
        final CrashMap newMap = new CrashMap(jiraAuth, projectID, configuration.getmaxAge());
        final BugDumpClient bdClient = new BugDumpClient(httpClient);
        final CrashFilter crashFilter = new CrashFilter(configuration.getBugFilterConfiguration());
        environment.jersey().register(bdClient);
        final BugProxyResource resource =
                new BugProxyResource(newMap, bdClient, crashFilter, projectID, jiraAuth, jiradogeAuth);

        environment.jersey().register(resource);
        environment.admin().addTask(new PurgeTask(newMap));


        ScheduledExecutorServiceBuilder purgeServiceBuilder = environment.lifecycle().scheduledExecutorService("purging");
        ScheduledExecutorService purgeService = purgeServiceBuilder.build();
        purgeService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.print("checking for old crashes ... ");
                if( !newMap.removeOldest() ){
                   System.out.print("No old crashes removed \n");
                }
            }
        }  , 2, configuration.getpurgeDelay(), TimeUnit.SECONDS);

    }

    public class PurgeTask extends Task {
        CrashMap theMap;
        public PurgeTask(CrashMap theMap){
            super("purge");
            this.theMap = theMap;
        }

        @Override
        public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
            System.out.println("purging");
            if (theMap.purgeCrashes()){
                System.out.println("completed sucsessfully");
            } else {
                System.out.println("didn't complete?");
            }

        }
    }



}
