package edu.ucsc.loggingservice.ycsb;


import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.Status;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class YCSBClient extends DB {
    private static AtomicInteger counter = new AtomicInteger();
    private  static ServerConfiguration serverConfig;
    @Override
    public void init() throws DBException {
        super.init();
        try {
            serverConfig = new ServerConfiguration();
            int totalServers = serverConfig.totalServers();
            int threadServer = counter.addAndGet(1) % totalServers;
            ServerInfo serverInfo = serverConfig.getServerInfo(0, threadServer);

            System.out.println("Thread using server");
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    @Override
    public Status read(String s, String s1, Set<String> set, Map<String, ByteIterator> map) {
        System.out.println("Table: " + s);
        System.out.println("Key: "+ s1);
        System.out.println(set.toString());
        System.out.println(map.toString());
        return null;
    }

    @Override
    public Status scan(String s, String s1, int i, Set<String> set, Vector<HashMap<String, ByteIterator>> vector) {
        return null;
    }

    @Override
    public Status update(String s, String s1, Map<String, ByteIterator> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status insert(String s, String s1, Map<String, ByteIterator> map) {
        return null;
    }

    @Override
    public Status delete(String s, String s1) {
        throw new UnsupportedOperationException();
    }
}
