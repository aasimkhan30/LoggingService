package edu.ucsc.loggingservice.ycsb;

import com.google.gson.Gson;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import edu.ucsc.loggingservice.client.LoggingServiceClient;
import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class YCSBClient extends DB {
    private static final Logger logger = Logger.getLogger(YCSBClient.class.getName());

    private static AtomicInteger counter = new AtomicInteger();
    private  static ServerConfiguration serverConfig;
    private static ThreadLocal<String> tag = new ThreadLocal<>();
    private static ThreadLocal<Integer> threadID  = new ThreadLocal<>();
    private static ThreadLocal<ServerInfo> serverInfo  = new ThreadLocal<>();
    private static ThreadLocal<LoggingServiceClient> grpcClient  = new ThreadLocal<>();
    private static Gson gson;
    @Override
    public void init() throws DBException {
        super.init();
        try {
            serverConfig = new ServerConfiguration();
            int totalServers = serverConfig.totalServers();
            threadID.set(counter.addAndGet(1));
            //logger.info(String.format("------\n\n\n TreadID = %d \n\n\n", threadID.get()));
            int threadServer = threadID.get() % totalServers;
            serverInfo.set(serverConfig.getServerInfo(0, threadServer));
            tag.set(String.format("device%d", threadServer));
            //logger.info(String.format("Thread using server %s:%s", serverInfo.get().host, serverInfo.get().port));
            grpcClient.set(new LoggingServiceClient(serverInfo.get()));
            if(gson == null)
                gson = new Gson();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    @Override
    public int read(String s, String s1, Set<String> set, HashMap<String, ByteIterator> hashMap) {
        grpcClient.get().getLog(s1, tag.get());
        return 1;
    }


    @Override
    public int scan(String s, String s1, int i, Set<String> set, Vector<HashMap<String, ByteIterator>> vector) {
        grpcClient.get().getLogFile(tag.get());
        return 1;
    }

    @Override
    public int update(String s, String s1, HashMap<String, ByteIterator> hashMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int insert(String s, String s1, HashMap<String, ByteIterator> hashMap) {
        grpcClient.get().addLogEntry(s1, gson.toJson(hashMap), new String[]{tag.get()});
        return 1;
    }

    @Override
    public int delete(String s, String s1) {
        throw new UnsupportedOperationException();
    }
}


