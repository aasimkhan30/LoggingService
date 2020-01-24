package edu.ucsc.loggingservice.configuration;

import edu.ucsc.loggingservice.models.ServerInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerConfiguration {
    Properties configurations;
    public ServerConfiguration() throws IOException {
        configurations = new Properties();
        String propFileName = Paths.get(".").toAbsolutePath().normalize().toString() + "/config.properties";
        InputStream input = new FileInputStream(propFileName);
        configurations.load(input);
    }

    public ServerInfo getServerInfo(int clusterId, int replicaId){
        ServerInfo result = new ServerInfo();
        result.host = configurations.getProperty("c." + clusterId + "." + replicaId + ".host");
        result.port = Integer.decode(configurations.getProperty("c."+ clusterId+ "." + replicaId + ".port"));
        result.consensusHost = configurations.getProperty("c." + clusterId + "." + replicaId + ".chost");
        result.consensusPort = Integer.decode(configurations.getProperty("c."+ clusterId+ "." + replicaId + ".cport"));
        return  result;
    }

    public int totalServers(){
        return Integer.parseInt(configurations.getProperty("cluster.count"));
    }
}
