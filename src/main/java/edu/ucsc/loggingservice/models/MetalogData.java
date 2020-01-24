package edu.ucsc.loggingservice.models;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class MetalogData {
    /*
    The unique ID of the user accessing the dataItems from the ledger.
     */
    public String userID;
    // Stores the keys of all items accessed.
    /*
    TODO: The current implementation can be a bit inefficient as all the keys are stored. A better implementation will be to store just then range of keys.
     */
    public LinkedList<String> accesses;

    public String accessTag;

    /*
    Converts a metalogData object to JsonString.
     */
    public String serialize() throws IOException {
        Gson gson = new Gson();
        String resultJson = gson.toJson(this);
        return resultJson;
    }

    /*
    Converts a JsonString to metalogData object.
     */
    public static MetalogData deserialize(String jsonMetalogData) throws IOException {
        Gson gson = new Gson();
        MetalogData object = gson.fromJson(jsonMetalogData, MetalogData.class);
        return object;
    }
}
