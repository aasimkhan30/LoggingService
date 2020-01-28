package edu.ucsc.loggingservice.ycsb;

import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Workload;

public class YCSBWorkload extends Workload {

    @Override
    public boolean doInsert(DB db, Object o) {
        return false;
    }

    @Override
    public boolean doTransaction(DB db, Object o) {
        return false;
    }
}
