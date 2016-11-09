package com.cyngn.bugproxy.core;


import com.cyngn.bugproxy.BugFilterConfiguration;

import java.util.regex.*;




public class CrashFilter {


    //these shouldn't change often, they can be declared here to keep the config cleaner(ish).
    private static final Pattern COMMUNITY141_PATTERN = Pattern.compile("^14.1-201[6-8]\\d{4}-(SNAPSHOT|NIGHTLY)-.*");
    private static final Pattern COMMUNITY13_PATTERN = Pattern.compile("^13.0-201[6-7]\\d{4}-(SNAPSHOT|NIGHTLY)-.*");
 //  private static final Pattern COMMERCIAL13_PATTERN = Pattern.compile("^13.(0|1)-Z.*");
  //  private static final Pattern COMMERCIAL121_PATTERN = Pattern.compile("^12.1-Y.*");

    private static final Pattern MEMORYADDRESS_PATTERN = Pattern.compile("@[0-9a-f]{6,9}");
    private static final Pattern UID_PATTERN = Pattern.compile("uid.\\d{3,10}");
    private static final Pattern USER_PATTERN = Pattern.compile("user.\\d{3,10}");
    private static final Pattern PID_PATTERN = Pattern.compile("pid.\\d{3,10}");
    private static final Pattern PATH_PATTERN = Pattern.compile("Unsupported path \\/\\d{10,15}");
    private static final Pattern CMUPDATER_PATTERN = Pattern.compile("\\/cmupdater\\/cm-\\d{2}\\.\\d-201\\d{5}-\\w{7,8}(-[0-9A-Z]{10})?-\\w+.zip.partial:");
    private static final Pattern QUERYPACKAGE_PATTERN = Pattern.compile("QUERY_PACKAGE_RESTART dat=package:\\w+(?:\\.\\w+)+ flg");
    private static final Pattern UNKNOWNPACKAGE_PATTERN = Pattern.compile("Unknown package: \\w+(?:\\.\\w+)+");
    private static final Pattern FRAGMENT_PATTERN = Pattern.compile("Fragment\\{[0-9a-f]{6,8}\\}");
    private static final Pattern OOM_PATTERN = Pattern.compile("a \\d+ byte allocation with \\d+ free bytes and \\d+[KM]B");
    private static final Pattern PROCESSRECORD_PATTERN = Pattern.compile("ProcessRecord\\{[a-z0-9]{4,9} \\d{4,6}:com.android.dialer\\/u0a\\d+\\}");
    private static final Pattern ROWCOL_PATTERN = Pattern.compile("row \\d{3,5}, col \\d{1,3}");

    private static Pattern ANCIENTBUILD_PATTERN;
    private static Pattern REALLYOLDBUILD_PATTERN;
    private static Pattern OLDBUILD_PATTERN;
    private static Pattern OLDCOMMERCIAL_PATTERN;

    private static Pattern FIXEDIN13_PATTERN;
    private static Pattern INVALIDIN13_PATTERN;

    private static Pattern FIXEDIN14_PATTERN;
    private static Pattern INVALIDIN14_PATTERN;


    public CrashFilter(BugFilterConfiguration config) {

        this.ANCIENTBUILD_PATTERN = Pattern.compile(config.getAncientBuilds());
        this.REALLYOLDBUILD_PATTERN = Pattern.compile(config.getReallyOldBuilds());
        this.OLDBUILD_PATTERN = Pattern.compile(config.getOldBuilds());
        this.OLDCOMMERCIAL_PATTERN = Pattern.compile(config.getOldCommercial());
        this.FIXEDIN13_PATTERN = Pattern.compile(config.getFixedCM13(), Pattern.CASE_INSENSITIVE);
        this.INVALIDIN13_PATTERN = Pattern.compile(config.getInvalidCM13(), Pattern.CASE_INSENSITIVE);
        this.FIXEDIN14_PATTERN = Pattern.compile(config.getFixedCM14(), Pattern.CASE_INSENSITIVE);
        this.INVALIDIN14_PATTERN = Pattern.compile(config.getInvalidCM14(), Pattern.CASE_INSENSITIVE);
    }

    public static boolean isValid(Crash incomming) {
        String buildID = incomming.getcustomfield_10800();
        String text =  incomming.getSummary() + " " + incomming.getDescription();

        if (OLDBUILD_PATTERN.matcher(buildID).matches() || REALLYOLDBUILD_PATTERN.matcher(buildID).matches() ||
                ANCIENTBUILD_PATTERN.matcher(buildID).matches() || OLDCOMMERCIAL_PATTERN.matcher(buildID).matches() ) {
            System.out.print(buildID + " rejected - old ");
            return false;
        }

        if (COMMUNITY13_PATTERN.matcher(buildID).matches() /* || COMMERCIAL13_PATTERN.matcher(buildID).matches() */) {
            System.out.print(buildID + " matched 13 ");
            if (FIXEDIN13_PATTERN.matcher(text).find()) {
                System.out.print(" rejected - fixed ");
                return false;
            } else if (INVALIDIN13_PATTERN.matcher(text).find()) {
                System.out.print(" rejected - invalid ");
                return false;
            }

        }
        if (COMMUNITY141_PATTERN.matcher(buildID).matches() ) {
            System.out.print(buildID + " matched  141 ");
            if (FIXEDIN14_PATTERN.matcher(text).matches()) {
                return false;
            } else if (INVALIDIN14_PATTERN.matcher(text).matches()) {
                return false;
            }

        }

        if (text.contains("com.moto") ||
                text.contains("com.sony")||
                text.contains("zz_ZZ")||
                text.contains("Can't downgrade")||
                text.contains("too many sql")||
                text.contains("xposed")||
                text.contains("xmodgame")||
                text.contains("com.miui")||
                text.contains("kerneladiutor")||
                text.contains("eu.chainfire")) {
            return false;
        }

        return true;
    }

    public static Crash cleanOutUnique(Crash aCrash){
        String description = aCrash.getDescription();

        description = UID_PATTERN.matcher(description).replaceAll("uid <uid>");
        description = USER_PATTERN.matcher(description).replaceAll("user <uid>");
        description = MEMORYADDRESS_PATTERN.matcher(description).replaceAll("@<address>");
        description = PID_PATTERN.matcher(description).replaceAll("pid <uid>");
        description = PATH_PATTERN.matcher(description).replaceAll("Unsupported path /<numbers>");
        description = CMUPDATER_PATTERN.matcher(description).replaceAll("/cmupdater/cm-<branch>-<date>-<type>-<device>.zip.partial:");
        description = QUERYPACKAGE_PATTERN.matcher(description).replaceAll("QUERY_PACKAGE_RESTART dat=package:package.name.here flg");
        description = UNKNOWNPACKAGE_PATTERN.matcher(description).replaceAll("Unknown package: package.name.here");
        description = FRAGMENT_PATTERN.matcher(description).replaceAll("Fragment{numbers}");
        description = OOM_PATTERN.matcher(description).replaceAll("a <numbers> byte allocation with <numbers> free bytes and <numbers>[KM]B");
        description = PROCESSRECORD_PATTERN.matcher(description).replaceAll("ProcessRecord{<hex> <numbers>:com.android.dialer/u0a<numbers>}");
        description = ROWCOL_PATTERN.matcher(description).replaceAll("row <row>, col <col>");
        if (description.equals(aCrash.getDescription()) ) {
            return aCrash;
        } else {
            return new Crash(aCrash.getSummary(), description, aCrash.getcustomfield_10800(), aCrash.getcustomfield_10104(), aCrash.getlabels());
        }
    }
}
