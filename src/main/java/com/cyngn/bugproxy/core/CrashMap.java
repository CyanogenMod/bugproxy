package com.cyngn.bugproxy.core;

import com.cyngn.bugproxy.services.BugDumpClient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.*;

public class CrashMap {

    private HashMap<String, Crash> crashHashMap;
    private LinkedList<timeKeyHash> crashTimeList;

    private long MAX_TIME; // time in milliseconds to compare issue age

    private static final Pattern COMMUNITY141_PATTERN = Pattern.compile("^14.1-201[6-8]\\d{4}-(SNAPSHOT|NIGHTLY)-.*");
    private static final Pattern COMMUNITY13_PATTERN = Pattern.compile("^13.0-201[6-7]\\d{4}-(SNAPSHOT|NIGHTLY)-.*");
/*    private static final Pattern COMMERCIAL13_PATTERN = Pattern.compile("^13.(0|1)-Z.*");
    private static final Pattern COMMERCIAL121_PATTERN = Pattern.compile("^12.1-Y.*");*/

    private static final String CM141 = "CM14.1";
    private static final String CM13 = "CM13";
    /*private static final String COS121 = "COS12.1";
    private static final String COS13 = "COS13";*/

    private BugDumpClient myClient;
    private String jiraAuth;
    private String projectID;

    public CrashMap(String jiraAuth, String projectID, long maxAge) {
        crashHashMap = new HashMap<String, Crash>();
        crashTimeList = new LinkedList<timeKeyHash>();
        this.jiraAuth = jiraAuth;
        this.projectID = projectID;
        this.MAX_TIME = maxAge;
    }

    public Response addToCrashMap(Crash aCrash, BugDumpClient bdClient) {
        myClient = bdClient;
        String aKey = aCrash.getDescription() + buildBucket(aCrash.getcustomfield_10800());
        if (crashHashMap.containsKey(aKey)) {
            //dupe found
            int numberOfDupes = crashHashMap.get(aKey).getCustomfield_10900() + 1;
            String buildID = crashHashMap.get(aKey).getcustomfield_10800() + ", " + aCrash.getcustomfield_10800();
            crashHashMap.get(aKey).setCustomfield_10900(numberOfDupes);
            crashHashMap.get(aKey).setCustomfield_10800(buildID);
        } else {
            //no dupe found
            Crash newCrash = new Crash(aCrash.getSummary(), aCrash.getDescription(),
                    aCrash.getcustomfield_10800(), aCrash.getcustomfield_10104(), 1, aCrash.getlabels());
            crashHashMap.put(aKey, newCrash);
            Response rsp = bdClient.sendCrash(newCrash, projectID, jiraAuth);
            crashTimeList.add(new timeKeyHash(System.currentTimeMillis(), rsp.getKey(), aKey));
        }


        return new Response("1234", "added to crashmap", "http://jira.cyanognemod.org/rest/2/api/fake/1234");
    }


    public String buildBucket(String buildid){
        if(COMMUNITY13_PATTERN.matcher(buildid).matches()){
            return CM13;
        } else if (COMMUNITY141_PATTERN.matcher(buildid).matches()) {
            return CM141;
/*        } else if (COMMERCIAL121_PATTERN.matcher(buildid).matches()) {
            return COS121;*/
/*        } else if (COMMERCIAL13_PATTERN.matcher(buildid).matches()) {
            return COS13;*/
        } else {
            return " unknown";
        }
    }

    public boolean removeOldest() {
        if (crashTimeList.size() > 0) {
            boolean hitJIRA = false;
            timeKeyHash oldest = crashTimeList.getFirst(); // this will be the map key of the first thing in the linked list
            double age = System.currentTimeMillis() - oldest.getTime();
            System.out.print("CrashMap contains " + crashTimeList.size() + " crashes and the oldest is " + age / 3600000 + "\n");
            if (age > MAX_TIME) {
                Crash oldCrash = crashHashMap.get(oldest.getHashKey());
                if(oldCrash.getCustomfield_10900() > 1) {
                    myClient.updateCrash(oldCrash.getCustomfield_10900(), oldCrash.getcustomfield_10800(), oldest.getJiraKey(), jiraAuth);
                    hitJIRA = true;
                }
                crashHashMap.remove(oldest.getHashKey());
                crashTimeList.remove(crashTimeList.indexOf(oldest));
                if ( !hitJIRA ) {
                    removeOldest();
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private class timeKeyHash {
        long time;
        String jiraKey;
        String hashKey;

        public timeKeyHash (long time, String jK, String hK){
            this.time = time;
            this.jiraKey = jK;
            this.hashKey = hK;
        }

        String getJiraKey(){
            return jiraKey;
        }
        String getHashKey(){
            return hashKey;
        }
        long getTime(){
            return time;
        }

    }
    public boolean purgeCrashes(){
        this.MAX_TIME = 1;
        System.out.print(MAX_TIME + " " + crashTimeList.size() + "\n");
        boolean finishedCleanly = false;

        if (crashTimeList.size() == 0){
            finishedCleanly = true;
        }

        while (crashTimeList.size() > 0){
            if (removeOldest()){
                System.out.print("one removed " + crashTimeList.size() + "\n");
                finishedCleanly = true;
            } else if (crashTimeList.size() == 0){
                System.out.print(" " + crashTimeList.size() + "\n");
                finishedCleanly = true;
            } else {
                System.out.print("fail " + crashTimeList.size() + "\n");
                finishedCleanly = false;
                break;
            }

        }

        return finishedCleanly;
    }


}
