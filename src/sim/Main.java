package sim;

import sim.enums.LPType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import sim.util.TrafficGenerator;

/**
 * @author Vy Nguyen
 */
public class Main
{    
    public static void main (String[] args) throws IOException
    {
        String topologyFile = "g1000_20";
        int numPacket = 50000;
        int arrivalRate = 1;
        String trafficFileName = TrafficGenerator.getPoissonTrafficFileName(
                topologyFile,
                numPacket, arrivalRate);

        //<LinkDelay, NodeDelay>
        List<Pair<Long, Long>> delays = Arrays.asList(
                new Pair<>(1L, 1L)
//                new Pair<Long, Long>(2L, 1L),
//                new Pair<Long, Long>(3L, 1L),
//                new Pair<Long, Long>(4L, 1L),
//                new Pair<Long, Long>(5L, 1L),
//                new Pair<Long, Long>(6L, 1L),
//                new Pair<Long, Long>(7L, 1L),
//                new Pair<Long, Long>(8L, 1L),
//                new Pair<Long, Long>(9L, 1L),
//                new Pair<Long, Long>(10L, 1L),
//                new Pair<Long, Long>(15L, 1L),
//                new Pair<Long, Long>(20L, 1L)
//                new Pair<Long, Long>(25L, 1L),
//                new Pair<Long, Long>(30L, 1L),
//                new Pair<Long, Long>(35L, 1L),
//                new Pair<Long, Long>(40L, 1L),
//                new Pair<Long, Long>(45L, 1L),
//                new Pair<Long, Long>(50L, 1L)
//                new Pair<Long, Long>(55L, 1L),
//                new Pair<Long, Long>(60L, 1L),
//                new Pair<Long, Long>(65L, 1L),
//                new Pair<Long, Long>(70L, 1L),
//                new Pair<Long, Long>(75L, 1L),
//                new Pair<Long, Long>(80L, 1L),
//                new Pair<Long, Long>(85L, 1L),
//                new Pair<Long, Long>(90L, 1L),
//                new Pair<Long, Long>(95L, 1L),
//                new Pair<Long, Long>(100L, 1L),
//                new Pair<Long, Long>(250L, 1L),
//                new Pair<Long, Long>(500L, 1L),
//                new Pair<Long, Long>(750L, 1L),
//                new Pair<Long, Long>(1000L, 1L)
            );

        String varyParam = "varyLink";
        boolean doCPA = true;
        String resultFile = doCPA ? "withCPA" : "withoutCPA";
        String resultDir = TrafficGenerator.getResultDirectory(topologyFile, numPacket, arrivalRate);
        String newLine = System.getProperty("line.separator");
        
//        FileWriter accumFW = new FileWriter(String.format("%s\\%s\\%s.csv", resultDir, varyParam, resultFile));
//        FileWriter bottleneck = new FileWriter(String.format("%s\\%s\\bottleneck.csv", resultDir, varyParam));
//        accumFW.write("Link Delay,Node Delay,Link Total Time,Link Total Event,Link Total Epoch,Link CPA,Link Concurrency(CPA),Link Concurrency (YAWN),Link LP Count,Link Max events per LP per epoch,"
//                                          + "Node Total Time,Node Total Event,Node Total Epoch,Node CPA,Node Concurrecy (CPA),Node Concurrency (YAWN),Node LP Count,Node Max events per LP per epoch" + newLine);
//        bottleneck.write("Model,Total LP,50,75,90" + newLine);
        
        
        
        long start = System.currentTimeMillis();
        for (Pair<Long, Long> delay : delays)
        {
            //Vary link
            if (varyParam.equals("varyLink"))
            {
//                accumFW.write(String.format("%d,%d", delay.getKey(), delay.getValue()));
                Simulator sim = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getKey(), delay.getValue(), LPType.Link);
                sim.runBottleneckAnalysis(String.format("%s\\%s\\link_bottleneck.csv", 
                                               resultDir, varyParam));
//                if (doCPA )
//                    sim.runCPA();
//                else
//                    sim.run();
//                
                
                
//                bottleneck.write("Link,");
//                sim.bottleneckAnalysis(bottleneck);
                
                Simulator sim2 = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getKey(), delay.getValue(), LPType.Node);
                sim2.runBottleneckAnalysis(String.format("%s\\%s\\node_bottleneck.csv", 
                                                resultDir, varyParam));
//                if (doCPA)
//                    sim2.runCPA();
//                else
//                    sim2.run();
                
//                bottleneck.write("Node,");
//                sim2.bottleneckAnalysis(bottleneck);
            }
            else
            {
                //accumFW.write(String.format("%d,%d", delay.getValue(), delay.getKey()));
                Simulator sim = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getValue(), delay.getKey(), LPType.Link);
                sim.run();
                Simulator sim2 = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getValue(), delay.getKey(), LPType.Node);
                sim2.run();
            }

//            accumFW.write(newLine);
        }

//       bottleneck.close();
//       accumFW.close();
       System.out.printf("Time elapsed: %d (ms)\n", System.currentTimeMillis() - start);
    }


    public static class Pair<K,V>
    {
        private final K key;
        private final V value;
        
        public Pair(K key, V value)
        {
            this.key = key;
            this.value = value;
        }
        
        public K getKey()
        {
            return key;
        }
        
        public V getValue()
        {
            return value;
        }
    }
}
