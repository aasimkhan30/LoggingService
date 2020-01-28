mvn clean package
java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
-db edu.ucsc.loggingservice.ycsb.YCSBClient \
-P ./workloads/workloada \
-p measurementtype=timeseries.granularity=1000 \
-threads 9
