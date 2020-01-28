package edu.ucsc.loggingservice.server.consensus;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import edu.ucsc.loggingservice.Helpers.HashFunctions;
import edu.ucsc.loggingservice.LogEntry;
import edu.ucsc.loggingservice.Tag;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.MetalogData;
import edu.ucsc.loggingservice.models.ServerInfo;
import sun.rmi.runtime.Log;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsensusServer {
    private static List<List<LogEntry>> logs;
    private static Map<String, Tag> tags;
    public  ServerConfiguration serverConfig;


    public ConsensusServer(ServerInfo serverInfo, ServerConfiguration rootConfig){
        serverConfig = rootConfig;
        Config cfg = new Config();
        NetworkConfig networkConfig = cfg.getNetworkConfig();
        networkConfig.setPort(serverInfo.consensusPort);
        JoinConfig join = networkConfig.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true);
        join.getTcpIpConfig().addMember(serverInfo.consensusHost);
        for(int i = 0 ; i < this.serverConfig.totalServers(); i++){
            String replicaIP = rootConfig.getServerInfo(serverInfo.clusterId, i).consensusHost
                    + ":"
                    + rootConfig.getServerInfo(serverInfo.clusterId, i).consensusPort;
             join.getTcpIpConfig().addMember(replicaIP);
        }

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        List<LogEntry> baseLog;
        baseLog = instance.getList("baselog");
        logs.add(baseLog);
        tags = instance.getMap("tag");
    }

    public void addLogEntry(LogEntry entry){
        entry.toBuilder().setTimestamp(currentTimestamp()).build();
        List<String> entryTags = entry.getTagsList();
        try {
            String entrySHA = HashFunctions.SHAsum(entry.toByteArray());
            for(String t: entryTags){
                Tag i = tags.get(t);
                String concat = i.getDigest() + entrySHA;
                String newSHA = HashFunctions.SHAsum(concat.getBytes());
                i.toBuilder().setDigest(newSHA).build();
                entry.toBuilder().setDigest(newSHA).build();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        logs.get(0).add(entry);
    }



    public void addMetaLogEntry(MetalogData log, int level){
        try {
            String metalogJson = log.serialize();
            LogEntry metalogEntry = LogEntry.newBuilder()
                    .setTimestamp(this.currentTimestamp())
                    .setKey(this.currentTimestamp())
                    .addTags("metalog"+(level+1))
                    .setValue(metalogJson).build();
            String entrySHA = HashFunctions.SHAsum(metalogEntry.toByteArray());
            int entryLevel = level ++;
            if (logs.size() < entryLevel){
                List<LogEntry> newLevel = new LinkedList<>();
                logs.add(newLevel);
            }
            logs.get(entryLevel).add(metalogEntry);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*
    getting a ledger
     */
    public List<LogEntry> getTag(Tag requestTag){
        List<LogEntry> result;
        int entryLevel = findLevel(requestTag.getName());
        if(entryLevel > 0) {
            result = logs.get(entryLevel);
        }
        else {
            List<LogEntry> logFile = new LinkedList<>();
            for(LogEntry l : logs.get(0)) {
                if(l.getTagsList().contains(requestTag))
                    logFile.add(l);
            }
            result = logFile;
        }
        MetalogData dataItem = new MetalogData();
        dataItem.userID = "demo";
        dataItem.accessTag = requestTag.getName();
        addMetaLogEntry(dataItem, entryLevel);
        return result;
    }

    /*
    getting a single entry.
     */
    public LogEntry getEntry(String key, int level){
        for(LogEntry l : logs.get(level)){
            if(l.getKey() == key) {
                MetalogData dataItem = new MetalogData();
                dataItem.userID = "demo";
                dataItem.key = l.getKey();
                addMetaLogEntry(dataItem, level);
                return l;
            }
        }
        return null;
    }

    public LogEntry getEntry(String key, String tag){
        int level = findLevel(tag);
        for(LogEntry l : logs.get(level)){
            if(l.getKey() == key){
                if(l.getKey() == key) {
                    MetalogData dataItem = new MetalogData();
                    dataItem.userID = "demo";
                    dataItem.key = l.getKey();
                    addMetaLogEntry(dataItem, level);
                    return l;
                }
            }
        }
        return null;
    }

    /*
    gets a tag from the hashmap
     */
    public Tag getTag(String tag){
        return tags.get(tag);
    }

    /*
    creates a Tag in the shared table
     */
    public boolean createTag (Tag tag){
        tags.put(tag.getName(), tag);
        return true;
    }

    /*
    updates a tag in the shared table
    */
    public void updateTag(Tag tag){
        tags.put(tag.getName(), tag);
    }

    /*
    delete a tag in the table
     */
    public boolean deleteTag(Tag tag){
        tags.remove(tag.getName());
        return true;
    }

    public boolean deleteTag(String tagId){
        tags.remove(tagId);
        return true;
    }

    /*
    finds whether the tag is a metalog tag or baselevel tag and then returns the loglevel
     */
    public static int findLevel(String tag){
        Pattern p = Pattern.compile("metalog\\s+(0-9)");
        Matcher n = p.matcher(tag);
        if(n.find()){
            return Integer.parseInt(n.group(1));
        }
        return 0;
    }

    public static String currentTimestamp(){
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        return ts.toString();
    }
}
