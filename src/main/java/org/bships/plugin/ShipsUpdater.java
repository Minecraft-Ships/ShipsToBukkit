package org.bships.plugin;

import org.array.utils.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.ships.plugin.ShipsPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Optional;

public class ShipsUpdater {

    private enum ReleaseType {

        PRE_ALPHA("PreAlpha", 4),
        ALPHA("Alpha", 3),
        BETA("Beta", 2),
        PRE_RELEASE("PreRelease", 1),
        RELEASE("", 0);

        private final String text;
        private final int value;

        ReleaseType(String name, int value){
            this.text = name;
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }

        public String getText(){
            return this.text;
        }

        public static ReleaseType getType(String type){
            for(ReleaseType type2 : values()){
                if(type2.getText().equalsIgnoreCase(type)){
                    return type2;
                }
            }
            throw new IllegalArgumentException("Unknown Release type of '" + type + "'");
        }
    }

    public static class VersionInfo {

        private final Integer[] mainVersion = {0, 0, 0, 0};
        private ReleaseType type = ReleaseType.RELEASE;
        private double betaVersion = 0.0;

        public int[] getMainVersion(){
            int[] args = new int[4];
            for(int A = 0; A < args.length; A++){
                args[A] = this.mainVersion[A];
            }
            return args;
        }

        public ReleaseType getType(){
            return this.type;
        }

        public double getBetaVersion(){
            return this.betaVersion;
        }

        public String toName(){
            return "Ships -b " + ArrayUtils.toString(".", Object::toString, Arrays.asList(this.mainVersion)) + " R2 " + this.type.getText() + ((this.type.equals(ReleaseType.RELEASE)) ? "" : " " + this.betaVersion);
        }

    }

    private static final int PLUGIN_KEY = 36846;
    private static final String USER_AGENT = "Updater";
    private static final String JSON_TITLE_VALUE = "name";
    private static final String HOST = "https://api.curseforge.com";
    private static final String QUERY = "/servermods/files?projectIds=";

    public Optional<VersionInfo> shouldUpdate(){
        VersionInfo localVersion = this.getLocalVersion();
        VersionInfo remoteVersion;
        try {
            remoteVersion = this.getLatest();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        if (shouldUpdate(localVersion, remoteVersion)){
            return Optional.of(remoteVersion);
        }
        return Optional.empty();
    }

    public VersionInfo getLocalVersion(){
        return getInfo("Ships -b " + ShipsPlugin.getPlugin().getPluginVersion() + " R2 " + ShipsPlugin.PRERELEASE_TAG + " " + ShipsPlugin.PRERELEASE_VERSION);
    }

    private boolean shouldUpdate(VersionInfo local, VersionInfo remote){
        for(int A = 0; A < local.mainVersion.length; A++){
            if(remote.mainVersion[A] > local.mainVersion[A]){
                return true;
            }else if(remote.mainVersion[A] < local.mainVersion[A]){
                return false;
            }
        }
        if(remote.type.getValue() < local.type.getValue()){
            return true;
        }else if(remote.type.getValue() > local.type.getValue()){
            return false;
        }
        return remote.betaVersion > local.betaVersion;
    }

    private VersionInfo getInfo(String remoteName){
        VersionInfo info = new VersionInfo();
        if(!remoteName.startsWith("Ships -b ")){
            throw new IllegalArgumentException("Name doesn't start with 'Ships -b ' but instead found '" + remoteName + "'");
        }
        remoteName = remoteName.substring(9);
        StringBuilder versionName = new StringBuilder();
        int endA = 0;
        for(int I = 0; I < remoteName.length(); I++){
            char at = remoteName.charAt(I);
            if(at == ' '){
                endA = I;
                break;
            }
            versionName.append(at);
        }
        String[] split = versionName.toString().split("\\.");
        try {
            for (int A = 0; A < 4; A++) {
                info.mainVersion[A] = Integer.parseInt(split[A]);
            }
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("Could not parse the numbers from " + ArrayUtils.toString(".", t -> t, split));
        }
        remoteName = remoteName.substring(endA);
        if(!remoteName.startsWith(" R2")){
            return info;
        }
        remoteName = remoteName.substring(4);
        StringBuilder type = new StringBuilder();
        for(int I = 0; I < remoteName.length(); I++){
            char at = remoteName.charAt(I);
            if(at == ' '){
                endA = I;
                break;
            }
            type.append(at);
        }
        info.type = ReleaseType.getType(type.toString());
        remoteName = remoteName.substring(endA);
        try {
            info.betaVersion = Double.parseDouble(remoteName);
            return info;
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("Could not parse version of '" + remoteName + "'");
        }
    }

    private VersionInfo getLatest() throws IOException {
        URL url = new URL(ShipsUpdater.HOST + ShipsUpdater.QUERY + ShipsUpdater.PLUGIN_KEY);
        URLConnection con = url.openConnection();
        con.addRequestProperty("User-Agent", ShipsUpdater.USER_AGENT);
        con.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response = reader.readLine();
        JSONArray jList = (JSONArray) JSONValue.parse(response);
        if(jList.isEmpty()){
            throw new IOException("Could not find any files. Something went really wrong");
        }
        IllegalArgumentException e1 = null;
        for(int A = jList.size() - 1; A >= 0; A--){
            JSONObject jObj = (JSONObject) jList.get(A);
            try{
                return this.getInfo((String)jObj.get(ShipsUpdater.JSON_TITLE_VALUE));
            }catch (IllegalArgumentException e){
                if(A == jList.size() - 1){
                    e1 = e;
                }
            }
        }
        throw new IOException(e1);
    }
}
