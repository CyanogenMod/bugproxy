package com.cyngn.bugproxy.services;


import com.cyngn.bugproxy.core.Crash;
import com.cyngn.bugproxy.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;


public class BugDumpClient {
    private HttpClient client;
    private Response response;

    private static final String THEURL = "https://jira.cyanogenmod.org/rest/api/2/issue/";
    private static final String ISSUETYPE = "1";

    public BugDumpClient(HttpClient client) {
        this.client = client;
    }


    public Response sendCrash(Crash crash, String project, String password) {
        String sendString = "";
        if (project.equalsIgnoreCase("11600")){
            System.out.println("dogefood!");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpPost post = new HttpPost(THEURL);
            if (crash.getlabels().contains("user")) {
                 if(crash.getSummary().isEmpty()) {
                      //crash.setSummary("BugProxy says this was blank");  // these have never provided useful report attachments
                      System.out.print("Bugreport summary broken; buildid: " + crash.getcustomfield_10800() + " ");
                      response = new Response("BlankSummary", "BlankSummary", "BlankSummary");
                      return response;

                 }
                 if(crash.getDescription().isEmpty()) {
                     //crash.setDescription("BugProxy says this was blank");
                     System.out.print("Bugreport description broken; buildid: " + crash.getcustomfield_10800() + " ");
                     response = new Response("BlankDescription", "BlankDescription", "BlankDescription");
                     return response;

                 }
                }

            //mapper.writeValueAsString();
            sendString = "{ \"fields\" : { \"project\": { \"id\" : \"" + project + "\"}," +
                    mapper.writeValueAsString(crash).substring(1,
                    mapper.writeValueAsString(crash).length() - 1) +
                    ", \"issuetype\": { \"id\" : \"" + ISSUETYPE + "\"}" + "}}"
            ;
            // Turn the JSONObject being passed into a stringentity for http consumption
            // System.out.println( sendString);
            post.setEntity(new StringEntity(sendString));
            post.setHeader("Accept", "application/json");
            post.setHeader("Authorization", "Basic " + password);
            post.setHeader("Content-Type", "application/json");

            HttpEntity responseEntity = client.execute(post).getEntity();
            String tmpString = EntityUtils.toString(responseEntity);
            System.out.println(tmpString);

            response = mapper.readValue(tmpString, Response.class);


        } catch (IOException e) {
            e.printStackTrace();
            response = new Response("IOerror", "IOerrror", "IOerror");
        } finally {
            return response;
        }

    }

    public Response updateCrash(int dupes, String buildIDs, String jiraKey, String jiraAuth) {
        try {
            String nicebuildIDs = sanitizeBuildIDs(buildIDs);
            String updateString =
                    "{\"fields\":{ \"customfield_10900\":"  + dupes + ", \"customfield_11000\":\""  + nicebuildIDs + "\"}}";

            System.out.println(updateString);

            HttpPut putUpdate = new HttpPut(THEURL  + jiraKey );

            putUpdate.setEntity(new StringEntity(updateString));
            putUpdate.setHeader("Authorization", "Basic " + jiraAuth);
            putUpdate.setHeader("Content-Type", "application/json");

            HttpEntity responseEntity = client.execute(putUpdate).getEntity();

            String tmpString = EntityUtils.toString(responseEntity);
            System.out.println(tmpString);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return null;
        }



    }


    private String sanitizeBuildIDs(String bulkyIDs){
        String cleanIDs = "";
        String[] tmpArray = bulkyIDs.split(", ");
        HashMap<String, Integer> cleanerArray = new HashMap<String, Integer>(tmpArray.length);
        for (int i = 0; i < tmpArray.length; i++){
            String build = tmpArray[i];
            String[] sBuild = build.split("-");
            if(sBuild[1].startsWith("2016")){
                if (sBuild[2].equalsIgnoreCase("NIGHTLY")){
                    build = sBuild[3].trim();
                }else{
                    build = sBuild[4].trim();
                }
            }else if (sBuild[1].startsWith("Y") || sBuild[1].startsWith("Z")){
                build = sBuild[1].substring(7,10) + sBuild[2].substring(0,1);
            }
            // System.out.print(build + " ");

            if(cleanerArray.containsKey(build)) {
                cleanerArray.put(build, cleanerArray.get(build) + 1);
            } else {
                cleanerArray.put(build, 1);
            }
        }
        Iterator it = cleanerArray.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            if (!cleanIDs.isEmpty()){
                cleanIDs = cleanIDs + ", ";
            }
            if ((int)pair.getValue() == 1){
                cleanIDs = cleanIDs + pair.getKey();
            }else {
                cleanIDs = cleanIDs + pair.getKey() + "(" + pair.getValue() + ")";
            }


            it.remove();
        }
        return cleanIDs;
    }
}
