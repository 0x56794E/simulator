package sim;

import sim.pojo.ILP;
import sim.pojo.IEvent;
import sim.enums.LPType;

import java.io.*;
import java.util.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import sim.Main.Pair;
import sim.util.CriticalPathLengthCalculator;

/**
 * @author Vy Nguyen
 */
public class Simulator
{
    //Map of LPs
    private Map<Integer, ILP> lpMap;
    private final LPType lpType;
    private final long linkDelay;
    private final long nodeDelay;
    private final String resultDir;
    private final String varyParam;
    List<IEvent> initialEvents = new ArrayList<IEvent>();
    
    private int totalEvent = 0;
    private FileWriter accumFW;
        
    private ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph 
            = new ListenableDirectedWeightedGraph<>(DefaultEdge.class);
    
    public Simulator(FileWriter accumFW, String varyParam, String topologyFile, String trafficFile, String resultDir, long linkDelay, long nodeDelay, LPType type) throws IOException
    {
        this.accumFW = accumFW;
        this.lpType = type;
        this.linkDelay = linkDelay;
        this.nodeDelay = nodeDelay;
        this.varyParam = varyParam;
        this.resultDir =  resultDir;

        lpMap = type.constructLPs(topologyFile, trafficFile, linkDelay, nodeDelay, this);
    }

    public ListenableDirectedWeightedGraph<IEvent, DefaultEdge> getRelGraph()
    {
        return relGraph;   
    }
    
    public void addInitEvent(IEvent e)
    {
        relGraph.addVertex(e);
        initialEvents.add(e);
    }

    private boolean hasEvent()
    {
        for(ILP lp : lpMap.values())
        {
            if (!lp.getFEL().isEmpty())
                return true;
        }
        return false;
    }

    /**
     * Run the simulation with epoch-by-epoch max, min, mean, std dev
     * of number of events per LP
     * @throws IOException 
     */
    public void run() throws IOException
    {
        long LBTS;
        int epoch = 0;
        int eventPerEpoch = 0;
        Queue<IEvent> q;

        //Detailed result per simulation
        FileWriter fw = new FileWriter(String.format("%s\\%s\\detail_%s_L%s_N%s.csv",
                                                     resultDir, varyParam, lpType, linkDelay, nodeDelay));

        String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        int totalEvent = 0, totalTime = 0;

        long maxEventsperLPperEpoch = 0;

        fw.write("Epoch,Total Events,Max events per LP,Mean,Std Dev" + newLine);

        do
        {
            sb.append(epoch).append(",");

            //Determine LBTS
            LBTS = compLBTS();
            long max = 0; //Max num of events per LP in ea epoch
            long min = Long.MAX_VALUE; //Min num of events per LP in ea epoch
            double var = 0, mean = 0;
            int n = 1;
            int lpEventCount;

            for(ILP lp : lpMap.values())
            {
                //Start an epoch
                lp.resetCurrentEpochEventCount();
                q = lp.getFEL();

                if (q.isEmpty())
                    continue;

                //process event (and schedule new event)
                while (!q.isEmpty() && q.peek().getTimestamp() < LBTS)
                {
                    lp.handleEvent(q.remove());
                }

                //Supposed ea even takes 1 unit of time to process
                //max events/LP
                lpEventCount = lp.getCurrentEpochEventCount();
                if (lpEventCount > max)
                    max = lpEventCount;

                if (lpEventCount < min)
                    min = lpEventCount;

                eventPerEpoch += lpEventCount;

                //Calc Var & mean incrementally
                if (n == 1) //First
                {
                    mean = lpEventCount;
                    var = 0;
                }
                else
                {
                    //Update variance first
                    var = (n - 2) * var / (n - 1) + (lpEventCount - mean) * (lpEventCount - mean) / n;

                    //Update mean
                    mean = ((n - 1) * mean + lpEventCount) / n;
                }


                n++;
            }

            //Write to detailed file
            sb.append(eventPerEpoch)
                    .append(",").append(max)
                    .append(",").append(mean)
                    .append(",").append(Math.sqrt(var))
                    .append(newLine);
            fw.write(sb.toString());

            totalEvent += eventPerEpoch;
            totalTime += max;
            eventPerEpoch = 0;
            ++epoch;
            sb.setLength(0);

            //max event/LP/epoch
            if (max > maxEventsperLPperEpoch)
                maxEventsperLPperEpoch = max;
        } while (hasEvent() && epoch <= 100000); //while at least one LP still has event

        
        fw.close();
    }

    /**
     * Run the simulation and spit out CPA result
     * @throws IOException 
     */
    public void runCPA() throws IOException
    {
        long LBTS;
        int epoch = 0;
        int eventPerEpoch = 0;
        Queue<IEvent> q;
        
        totalEvent = 0;
        int totalTime = 0;

        long maxEventsperLPperEpoch = 0;

        do
        {
            //Determine LBTS
            LBTS = compLBTS();
            long max = 0; //Max num of events per LP in ea epoch
            long min = Long.MAX_VALUE; //Min num of events per LP in ea epoch
            double var = 0, mean = 0;
            int n = 1;
            int lpEventCount;

            for(ILP lp : lpMap.values())
            {
                //Start an epoch
                lp.resetCurrentEpochEventCount();
                q = lp.getFEL();

                if (q.isEmpty())
                    continue;

                //process SAFE events (and schedule new event)
                while (!q.isEmpty() && q.peek().getTimestamp() < LBTS)
                {
                    lp.handleEvent(q.remove());
                }

                //Supposed ea even takes 1 unit of time to process
                //max events/LP
                lpEventCount = lp.getCurrentEpochEventCount();
                if (lpEventCount > max)
                    max = lpEventCount;

                if (lpEventCount < min)
                    min = lpEventCount;

                eventPerEpoch += lpEventCount;

                //Calc Var & mean incrementally
                if (n == 1) //First
                {
                    mean = lpEventCount;
                    var = 0;
                }
                else
                {
                    //Update variance first
                    var = (n - 2) * var / (n - 1) + (lpEventCount - mean) * (lpEventCount - mean) / n;

                    //Update mean
                    mean = ((n - 1) * mean + lpEventCount) / n;
                }
                n++;
            }
            totalEvent += eventPerEpoch;
            totalTime += max;
            eventPerEpoch = 0;
            ++epoch;
            
            //max event/LP/epoch
            if (max > maxEventsperLPperEpoch)
                maxEventsperLPperEpoch = max;
        } while (hasEvent() && epoch <= 100000); //while at least one LP still has event. 100k lim is "emergency break" to avoid inf loop

        //Write the result from the ACPA here
        int cpl = CriticalPathLengthCalculator.calcPathLength(relGraph, initialEvents);
        
        accumFW.write(String.format(",%d,%d,%d,%d,%f,%f,%d,%d", 
                totalTime, totalEvent, epoch, cpl, 1.0*totalEvent/cpl, 1.0*totalEvent/totalTime, lpMap.size(), maxEventsperLPperEpoch));
    }
    
    /**
     * Run the simulation with analysis of the top 10 busiest LPs.
     * Similar to run() but only do analysis on top 10 busiest LPs instead of
     * all LPs.
     * @param modelName
     */
    public void runBottleneckAnalysis() throws IOException
    {
        //File names
        //Main result
        String fileName = String.format("%s\\%s\\%s_bottleneck.csv", 
                                        resultDir, varyParam, lpType);
        String eventDistFileName = String.format("%s\\%s\\%s_eventDist.csv", 
                                        resultDir, varyParam, lpType);
        
        String newLine = System.getProperty("line.separator");
        FileWriter fw = new FileWriter(fileName);   
        FileWriter eventDistFw = new FileWriter(eventDistFileName);
        
         //epoch number, total event, max, min, mean, std dev
        fw.write("Epoch,Total Event,Max,Min,Mean,Std Dev");
        fw.write(newLine);
        
        //Epoch, EventCount, Event1, Event2, ... : LP1
        //Epoch, EventCount, Event1, ...., ....  : LP2
        eventDistFw.write("Epoch,Event Count");
        eventDistFw.write(newLine);
        
        long LBTS;
        int epoch = 0;
        Queue<IEvent> q;
        
        totalEvent = 0;
        do
        {
            //Determine LBTS
            LBTS = compLBTS();

            for(ILP lp : lpMap.values())
            {
                //Start an epoch
                lp.resetCurrentEpochEventCount();
                q = lp.getFEL();

                if (q.isEmpty())
                    continue;

                //process SAFE events (and schedule new event)
                while (!q.isEmpty() && q.peek().getTimestamp() < LBTS)
                {
                    lp.handleEvent(q.remove());
                }
            }
            
            //Write summary for this epoch
            //1. Find the top 10 busiest LPs in this epoch
            List<ILP> busiest = getBusiest(new ArrayList<>(lpMap.values()), 10);
            recordEventCount(epoch, busiest, eventDistFw);
            
            //2. Calculate max, min, mean, std dev            
            long max = 0; //Max num of events per LP in ea epoch
            long min = Long.MAX_VALUE; //Min num of events per LP in ea epoch
            double var = 0, mean = 0;
            int n = 1, eventPerEpochBusiest = 0, lpEventCountBusiest;
            for (ILP lp : busiest)
            {
                lpEventCountBusiest = lp.getCurrentEpochEventCount();
               if (lpEventCountBusiest > max)
                    max = lpEventCountBusiest;

                if (lpEventCountBusiest < min)
                    min = lpEventCountBusiest;

                eventPerEpochBusiest += lpEventCountBusiest;

                //Calc Var & mean incrementally
                if (n == 1) //First
                {
                    mean = lpEventCountBusiest;
                    var = 0;
                }
                else
                {
                    //Update variance first
                    var = (n - 2) * var / (n - 1) + (lpEventCountBusiest - mean) * (lpEventCountBusiest - mean) / n;

                    //Update mean
                    mean = ((n - 1) * mean + lpEventCountBusiest) / n;
                }
                ++n;
            }
            
            //3. Write summary
            //epoch number, total event, max, min, mean, std dev
            fw.write(String.format("%d,%d,%d,%d,%f,%f", 
                                   epoch, eventPerEpochBusiest, max, min, mean, Math.sqrt(var)));
            fw.write(newLine);
            ++epoch;            
        } while (hasEvent() && epoch <= 100000); //while at least one LP still has event. 100k lim is "emergency break" to avoid inf loop

        fw.close();
        eventDistFw.close();
    }
    
    /**
     * Generate graph where
     * - Horizontal axis: time
     * - Vertical axis: the LPs
     * 
     * This chart shows how the events are distributed
     * LPs are sorted by the number of events they have to process.
     */
    private void recordEventCount(int epoch, List<ILP> lps, FileWriter file) throws IOException
    {
        String newLine = System.getProperty("line.separator");
        //epoch, lp1's event count, event1's ts, event2's ts, event3's ts,...
        //epoch, lp2's event count, event1's ts, event2's ts, event3's ts,...
        for (ILP lp : lps)
        {
            file.write(String.format("%d,%d,", epoch, lp.getCurrentEpochEventCount()));
            
            for (IEvent event : lp.getCurrentEpochEvents())
                file.write(String.format("%d,", event.getTimestamp()));
            
            file.write(newLine);
        }
    }
    
    /**
     * @param lps
     * @param n
     * @return top n busiest lps in the current epoch
     */
    private List<ILP> getBusiest(List<ILP> lps, int n)
    {
        Collections.sort(lps, new Comparator<ILP>()
        {
            @Override
            public int compare(ILP o1, ILP o2)
            {
                
                return o2.getCurrentEpochEventCount()- o1.getCurrentEpochEventCount();
            }
        });
        
        return new ArrayList<ILP>(lps.subList(0, n));
    }
    
    /**
     * In order to prove that the bottleneck caused by hub nodes in scale-free networks
     * is alleviated by the link model, we show that even though there are links that 
     * are also responsible for many more events that other links, the difference in
     * the number of events is small comparing to the difference found in the node model.
     * 
     * Specifically, we show that only 2 (or 1%), for example, node LPs are responsible for 75% of events
     * whereas 100 link LPs (or 10%) are responsible for 75% of events.
     */
    public void bottleneckAnalysis(FileWriter fw) throws IOException
    {
        //Show largest degree nodes (what deg?) and the number of events they have to process
        //In the entire simulation, show how many LPs are responsible for some x percent of events
        
        
        //Hypothesis: node has only 2 lps resp for 75% of events
        //whereas link has 100 lps resp for 75% of events.
        //One may argue one of the link be resp for many more event
        //not true as shown by the std dev of number of events per lp
        
        int[] percents = new int[] {50, 75, 90};
        StringBuilder summary = new StringBuilder().append(lpMap.size()).append(",");
        StringBuilder detail = new StringBuilder();
        String newline = System.getProperty("line.separator");
        
        for (int percent : percents)
        {
            List<Pair<Integer, Integer>> result = countTop(totalEvent, percent);           
            summary.append(result.size()).append(",");
            
            detail.append(percent).append(",eventCount,");
            for (Pair<Integer, Integer> eventCountToDeg : result)
                detail.append(eventCountToDeg.getKey()).append(",");
            detail.append(newline).append(percent).append(",degree,");
            for (Pair<Integer, Integer> eventCountToDeg : result)
                detail.append(eventCountToDeg.getValue()).append(",");
            detail.append(newline);
        }
        
        summary.append(newline);
        
        //Consider hub nodes and linkes
        StringBuilder degs = new StringBuilder("Degree,");
        StringBuilder eventC = new StringBuilder("EventCount,");
        List<ILP> sorted = new ArrayList<ILP>(lpMap.values());
         //Sort LP by degrees; decreasing
        Collections.sort(sorted, new Comparator<ILP>()
        {

            @Override
            public int compare(ILP o1, ILP o2)
            {
                return o2.getAllNeis().size()- o1.getAllNeis().size();
            }
        });
        
        //Top 20 busiest LPs
        for (int i = 0; i < 20; ++i)
        {
            degs.append(sorted.get(i).getAllNeis().size()).append(",");
            eventC.append(sorted.get(i).getTotalEventProc()).append(",");
        }
        degs.append(newline);
        eventC.append(newline);
        
        
        fw.write(summary.toString());
        
        fw.write(degs.toString());
        fw.write(eventC.toString());
        
        fw.write(detail.toString());
    }
    
    
    //Count number of LPs responsible for >= some percent of the total events
    //Key: event, Value: degree
    private List<Pair<Integer, Integer>> countTop(int totalEvents, int percent)
    {
        List<ILP> sorted = new ArrayList<ILP>(lpMap.values());
        List<Pair<Integer, Integer>> eventPerLP = new LinkedList<Pair<Integer, Integer>>(); //number of events proced in ea lp
        
        //Sort LP by events proc'ed; decreasing
        Collections.sort(sorted, new Comparator<ILP>()
        {

            @Override
            public int compare(ILP o1, ILP o2)
            {
                return o2.getTotalEventProc() - o1.getTotalEventProc();
            }
        });
        
        int threshold = totalEvents * percent / 100;
        int sum = 0;
        for (ILP lp : sorted)
        {
            sum += lp.getTotalEventProc();
            eventPerLP.add(new Pair(lp.getTotalEventProc(), lp.getAllNeis().size()));
            if (sum > threshold)
                break;
        }

        return eventPerLP;
    }

    
    private long compLBTS()
    {
        //Find min of ALL LP's LBTS
        long lbts = Long.MAX_VALUE;
        long lpLBTS;

        for (ILP lp : lpMap.values())
        {
            lpLBTS = lp.getLPLBTS();
            if (lpLBTS < lbts)
                lbts = lpLBTS;
        }

        return lbts;
    }
}
