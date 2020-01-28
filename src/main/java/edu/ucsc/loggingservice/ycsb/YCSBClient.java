package edu.ucsc.loggingservice.ycsb;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import edu.ucsc.loggingservice.client.LoggingServiceClient;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class YCSBClient extends DB {
    private static final Logger logger = Logger.getLogger(YCSBClient.class.getName());

    private static AtomicInteger counter = new AtomicInteger();
    private  static ServerConfiguration serverConfig;
    private static String tag;
    private static int threadID;
    private static ServerInfo serverInfo;
    private LoggingServiceClient grpcClient;
    @Override
    public void init() throws DBException {
        super.init();
        try {
            serverConfig = new ServerConfiguration();
            int totalServers = serverConfig.totalServers();
            int threadId = counter.addAndGet(1);
            int threadServer = threadId % totalServers;
            serverInfo = serverConfig.getServerInfo(0, threadServer);
            tag = String.format("device%d", threadServer);
            logger.info(String.format("Thread using server %s:%s", serverInfo.host, serverInfo.port));
            grpcClient = new LoggingServiceClient(serverInfo);
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    @Override
    public int read(String s, String s1, Set<String> set, HashMap<String, ByteIterator> hashMap) {
        logger.info(String.format("Trying to read %s from %s", s, s1));
        return 1;
    }


    @Override
    public int scan(String s, String s1, int i, Set<String> set, Vector<HashMap<String, ByteIterator>> vector) {
        logger.info(String.format("Trying to read from %s to %s", s, s1));
        return 1;
    }

    @Override
    public int update(String s, String s1, HashMap<String, ByteIterator> hashMap) {
        logger.info(String.format("Trying to update %s from %s", s, s1));
        for(String key : hashMap.keySet()){
            System.out.println(key);
            System.out.println(hashMap.get(key).toString());
        }
        return 1;
    }

    @Override
    public int insert(String s, String s1, HashMap<String, ByteIterator> hashMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(String s, String s1) {
        throw new UnsupportedOperationException();
    }
}


