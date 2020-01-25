package edu.ucsc.loggingservice.ycsb;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
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

/*
Uses HazelCast servers to run the YCSB benchmark. Useful for testing performance of dataStructures
 */
public class YCSBClientHazelCast extends DB {
    private static ServerConfiguration serverConfig;
    private AtomicInteger counter = new AtomicInteger();
    HazelcastInstance client;
    @Override
    public void init() throws DBException {
        super.init();
        try {
            serverConfig = new ServerConfiguration();
            int totalServers = serverConfig.totalServers();
            int threadServer = counter.addAndGet(1) % totalServers;
            ServerInfo serverInfo = serverConfig.getServerInfo(0, threadServer);
            System.out.printf("Thread using server %s:%s\n", serverInfo.consensusHost, serverInfo.consensusPort);
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.getGroupConfig().setName("dev").setPassword("dev-pass");
            clientConfig.addAddress(serverInfo.consensusHost+":"+serverInfo.consensusPort);
            client = HazelcastClient.newHazelcastClient(clientConfig);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public Status read(String s, String s1, Set<String> set, Map<String, ByteIterator> map) {

        return null;
    }

    @Override
    public Status scan(String s, String s1, int i, Set<String> set, Vector<HashMap<String, ByteIterator>> vector) {
        return null;
    }

    @Override
    public Status insert(String s, String s1, Map<String, ByteIterator> map) {
        return null;
    }

    @Override
    public Status update(String s, String s1, Map<String, ByteIterator> map) {
        return null;
    }

    @Override
    public Status delete(String s, String s1) {
        return null;
    }
}
