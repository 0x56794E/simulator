package sim;

import sim.enums.LPType;

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
        String resultDir = TrafficGenerator.getResultDirectory(topologyFile, numPacket, arrivalRate);
        
        long start = System.currentTimeMillis();
        for (Pair<Long, Long> delay : delays)
        {
            //Vary link
            if (varyParam.equals("varyLink"))
            {
                Simulator sim = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getKey(), delay.getValue(), LPType.Link);
                sim.run();
                Simulator sim2 = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getKey(), delay.getValue(), LPType.Node);
                sim2.run();

            }
            else
            {
                Simulator sim = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getValue(), delay.getKey(), LPType.Link);
                sim.run();
                Simulator sim2 = new Simulator(null, varyParam, topologyFile, trafficFileName, resultDir, delay.getValue(), delay.getKey(), LPType.Node);
                sim2.run();
            }
        }
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
