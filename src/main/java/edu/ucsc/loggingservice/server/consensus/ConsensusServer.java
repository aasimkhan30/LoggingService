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

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsensusServer {
    private static List<List<LogEntry>> logs;
    private static Map<String, Tag> tags;
    public  ServerConfiguration serverConfig;


    public ConsensusServer(ServerInfo serverInfo, ServerConfiguration rootConfig){
        rootConfig = rootConfig;
        Config cfg = new Config();
        NetworkConfig networkConfig = cfg.getNetworkConfig();
        networkConfig.setPort(serverInfo.consensusPort);
        JoinConfig join = networkConfig.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true);
        join.getTcpIpConfig().addMember(serverInfo.consensusHost);
        for(int i = 0 ; i < this.serverConfig.totalServers(); i++){
            if(i != serverInfo.clusterId)
             join.getTcpIpConfig().addMember(rootConfig.getServerInfo(serverInfo.clusterId, i).consensusHost);
        }
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        List<LogEntry> baseLog;
        baseLog = instance.getList("baselog");
        logs.add(baseLog);
        tags = instance.getMap("tag");
    }

    public void addLogEntry(LogEntry entry){
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

    public void addMetaLogEntry(Tag tag, int level){

    }

    public void addMetaLogEntry(LogEntry log, int level){

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
        addMetaLogEntry(requestTag, entryLevel+1);
        return result;
    }

    /*
    getting a single entry.
     */
    public LogEntry getEntry(String key, int level){
        for(LogEntry l : logs.get(level)){
            if(l.getKey() == key) {
                addMetaLogEntry(l, level);
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
                    addMetaLogEntry(l, level);
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
}
