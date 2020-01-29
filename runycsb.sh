mvn clean package -DskipTests

#java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
#-db edu.ucsc.loggingservice.ycsb.YCSBClient \
#-P ./workloads/workloada \
#-p measurementtype=timeseries.granularity=1000 \
#-threads 9 \

java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
-db edu.ucsc.loggingservice.ycsb.YCSBClient \
-P ./workloads/workloada \
-p measurementtype=timeseries.granularity=1000 \
-threads 1 > out.csv

java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
-db edu.ucsc.loggingservice.ycsb.YCSBClient \
-P ./workloads/workloada \
-p measurementtype=timeseries.granularity=1000 \
-threads 9 > out2.csv

java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
-db edu.ucsc.loggingservice.ycsb.YCSBClient \
-P ./workloads/workloada \
-p measurementtype=timeseries.granularity=1000 \
-threads 18 > out3.csv

java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
-db edu.ucsc.loggingservice.ycsb.YCSBClient \
-P ./workloads/workloada \
-p measurementtype=timeseries.granularity=1000 \
-threads 27 > out4.csv

java -cp ./target/LoggingService-1.0-SNAPSHOT-jar-with-dependencies.jar:./lib/core-0.17.0.jar com.yahoo.ycsb.Client \
-db edu.ucsc.loggingservice.ycsb.YCSBClient \
-P ./workloads/workloada \
-p measurementtype=timeseries.granularity=1000 \
-threads 36 > out5.csv

