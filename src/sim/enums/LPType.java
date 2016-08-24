package sim.enums;

import sim.pojo.ILP;
import sim.Simulator;
import sim.pojo.impl.LinkLP;
import sim.pojo.impl.MaxStopAwareEvent;
import sim.pojo.impl.NodeLP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by VyNguyen on 2/10/2016.
 */
public enum LPType
{
    Link
    {
        @Override
        public Map<Integer, ILP> constructLPs(String topologyFile, String trafficFile, long linkDelay, long nodeDelay, Simulator simulator) throws IOException
        {
            //Generate the traffic (old style thru random walk)
            //TrafficGenerator.genTraffic(packetCount, minHop, maxHop, fileName, linkDelay, nodeDelay, simulator); //can be skipped?
            //System.exit(1);

            Map<Integer, ILP> lpMap = new HashMap<Integer, ILP>();
            BufferedReader br = new BufferedReader(new FileReader(topologyFile));
            String[] toks;
            int id1, id2;

            //node id: all links touching this node
            Map<Integer, Set<LinkLP>> tmpMap = new HashMap<Integer, Set<LinkLP>>();
            Map<String, Integer> linkIDbyNodes = new HashMap<String, Integer>();

            //Construct the LPs
            //Line format: ID of node #1, ID of node #2
            //Each line reps an LP
            //2 lines/links are neighbor if it shares at least one common node
            int id = 0;
            for (String line = br.readLine(); line != null; line = br.readLine())
            {
                toks = line.split(",");
                id1 = Integer.parseInt(toks[0].trim());
                id2 = Integer.parseInt(toks[1].trim());

                //For fast lookup in traffic loading
                linkIDbyNodes.put(String.format("%d_%d", id1, id2), id);

                //Construct new LP
                LinkLP lp = new LinkLP(id, id1, id2, linkDelay, nodeDelay, simulator);
                lpMap.put(id, lp);
                ++id;

                if (!tmpMap.containsKey(id1))
                {
                    tmpMap.put(id1, new HashSet<LinkLP>());
                }

                if (!tmpMap.containsKey(id2))
                {
                    tmpMap.put(id2, new HashSet<LinkLP>());
                }

                tmpMap.get(id1).add(lp);
                tmpMap.get(id2).add(lp);
            }

            //Traverse the tmp map and update LPs' neighbor list
            for (Map.Entry<Integer, ILP> entry : lpMap.entrySet())
            {
                LinkLP link = (LinkLP) entry.getValue();
                for (LinkLP nei1 : tmpMap.get(link.getNode1Id()))
                {
                    link.addNeighbor(nei1);
                    nei1.addNeighbor(link);
                }

                for (LinkLP nei2 : tmpMap.get(link.getNode2Id()))
                {
                    //TODO: a link can be a nei to itself?
                    link.addNeighbor(nei2);
                    nei2.addNeighbor(link);
                }
            }

           // System.out.println("Finish constructing LP map.");

            //loading traffic
            loadTraffic(trafficFile,
                        linkIDbyNodes,
                        lpMap,
                        simulator);

            return lpMap;
        }

        private String getLinkId(String id1, String id2)
        {
            int i1 = Integer.parseInt(id1);
            int i2 = Integer.parseInt(id2);

            return i1 < i2 ? String.format("%d_%d", i1, i2) : String.format("%d_%d", i2, i1);
        }

        private void loadTraffic(String trafficFileName, Map<String, Integer> linkIDbyNodePair, Map<Integer, ILP> lpMap, Simulator simulator) throws IOException
        {
            //Need: given node IDs => get link
            BufferedReader br = new BufferedReader(new FileReader(trafficFileName));
            String[] toks;

            for (String line = br.readLine(); line != null; line = br.readLine())
            {
                toks = line.split(",");
                ArrayList<Integer> stops = new ArrayList<Integer>(toks.length - 2);
                for (int i = 1; i < toks.length - 1; ++i)
                {
                    stops.add(linkIDbyNodePair.get(getLinkId(toks[i], toks[i + 1])));
                }

                MaxStopAwareEvent event = new MaxStopAwareEvent(Long.parseLong(toks[0]),
                                                                EventType.ARRIVAL, 0,
                                                                stops);

                //Schedule arrival event on first link
                lpMap.get(stops.get(0)).scheduleEvent(event);
                simulator.addInitEvent(event);
            }
        }
    },

    Node
    {
        @Override
        public  Map<Integer, ILP> constructLPs(String topologyFile, String trafficFile, long transTime, long switchTime, Simulator simulator) throws IOException
        {
            Map<Integer, ILP> lpMap = getNodeTopology(topologyFile, transTime, switchTime, simulator);

            //Load the traffic
            loadTraffic(lpMap, trafficFile, simulator);
            return lpMap;
        }

        private void loadTraffic(Map<Integer, ILP> lpMap, String trafficFileName, Simulator simulator) throws IOException
        {
            //Need: given node IDs => get link
            BufferedReader br = new BufferedReader(new FileReader(trafficFileName));
            String[] toks;

            for (String line = br.readLine(); line != null; line = br.readLine())
            {
                toks = line.split(",");
                ArrayList<Integer> stops = new ArrayList<Integer>(toks.length - 1);
                for (int i = 1; i < toks.length; ++i)
                {
                    stops.add(Integer.parseInt(toks[i]));
                }

                MaxStopAwareEvent event = new MaxStopAwareEvent(Long.parseLong(toks[0]),
                                                                EventType.DEPARTURE, 0,
                                                                stops);

                
                //Schedule departure event on first node
                lpMap.get(stops.get(0)).scheduleEvent(event);
                simulator.addInitEvent(event);
            }
        }
    };

    public  Map<Integer, ILP> constructLPs(String topologyFile, String trafficFile, long transTime, long switchTime, Simulator simulator) throws IOException
    {
        throw new UnsupportedOperationException("Must override in sub-class");
    };
            
            
    public static Map<Integer, ILP> getNodeTopology(String topologyFile, long transTime, long switchTime, Simulator simulator) throws IOException
    {
        Map<Integer, ILP> lpMap = new HashMap<Integer, ILP>();
        BufferedReader br = new BufferedReader(new FileReader(topologyFile));
        String[] toks;
        int id1, id2;
        ILP lp1, lp2;

        //Construct the LPs
        //Line format: ID of node #1, ID of node #2
        //Each line reps connectivity
        for (String line = br.readLine(); line != null; line = br.readLine())
        {
            toks = line.split(",");
            id1 = Integer.parseInt(toks[0].trim());
            id2 = Integer.parseInt(toks[1].trim());

            if (!lpMap.containsKey(id1))
            {
                lp1 = new NodeLP(id1, transTime, switchTime, simulator);
                lpMap.put(id1, lp1);
            }
            else
            {
                lp1 = lpMap.get(id1);
            }

            if (!lpMap.containsKey(id2))
            {
                lp2 = new NodeLP(id2, transTime, switchTime, simulator);
                lpMap.put(id2, lp2);
            }
            else
            {
                lp2 = lpMap.get(id2);
            }

            //Add nei
            lp1.addNeighbor(lp2);
            lp2.addNeighbor(lp1);
        }
        return lpMap;
    }
}
