package edu.ucsc.loggingservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.ucsc.loggingservice.configuration.ServerConfiguration;
import edu.ucsc.loggingservice.models.ServerInfo;
import edu.ucsc.loggingservice.server.consensus.ConsensusServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsensusServerUnitTest {

    public static int CLUSTER_ID = 0;
    public static int REPLICA1_ID = 0;
    public static int REPLICA2_ID = 1;
    public static ConsensusServer cServer1, cServer2;
    public static ServerInfo serverInfo1, serverInfo2;
    public static ServerConfiguration serverConfiguration;

    @Before
    public void setup(){
        try {
            serverConfiguration = new ServerConfiguration();
            serverInfo1 = serverConfiguration.getServerInfo(CLUSTER_ID, REPLICA1_ID);
            cServer1 = new ConsensusServer(serverInfo1, serverConfiguration);
            serverInfo2 = serverConfiguration.getServerInfo(CLUSTER_ID, REPLICA1_ID);
            cServer2 = new ConsensusServer(serverInfo2, serverConfiguration);
        }
        catch(Exception e){

        }
    }

    @Test
    public void addLogEntry (){
        String testEntryKey = "test1Key";
        String testEntryVal = "test1Val";
        String testEntryTag = "testTag";

        LogEntry testEntry = LogEntry.newBuilder()
                .setKey(testEntryKey)
                .setValue(testEntryVal)
                .addTags(testEntryTag)
                .build();

        cServer1.addLogEntry(testEntry);

        LogEntry getEntry = cServer1.getEntry(testEntryKey, testEntryVal);

        assertEquals(testEntryKey, getEntry.getKey());
        assertEquals(testEntryVal, getEntry.getValue());
        assertEquals(testEntryTag, getEntry.getTags(0));
    }

    @Test
    public void crossServerGet (){
        String testEntryKey = "test2Key";
        String testEntryVal = "test2Val";
        String testEntryTag = "testTag";

        LogEntry testEntry = LogEntry.newBuilder()
                .setKey(testEntryKey)
                .setValue(testEntryVal)
                .addTags(testEntryTag)
                .build();

        cServer1.addLogEntry(testEntry);

        LogEntry getEntry = cServer2.getEntry(testEntryKey, testEntryVal);

        assertEquals(testEntryKey, getEntry.getKey());
        assertEquals(testEntryVal, getEntry.getValue());
        assertEquals(testEntryTag, getEntry.getTags(0));
    }

    @After
    public void cleanup(){
        cServer1.closeServer();
        cServer2.closeServer();
    }
}
