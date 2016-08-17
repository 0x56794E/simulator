/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import sim.enums.EventType;
import sim.pojo.IEvent;
import sim.pojo.impl.MaxStopAwareEvent;

/**
 *
 * @author q393832
 */
public class CriticalPathLengthCalculatorTest
{
    public static void main(String[] args)
    {
        test1();
        test2();
        test3();
        test4();
        testSimpleGraph();
        testSimpleGraph1();
        testSimpleGraph2();
        testSimpleGraph3();
    }

    private static void test1()
    {
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph 
            = new ListenableDirectedWeightedGraph<>(DefaultEdge.class);
    
        List<IEvent> initEvents = new ArrayList<>();
        IEvent e1 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e2 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e3 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e4 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e5 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e6 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e7 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
       
        initEvents.add(e1);
        initEvents.add(e2);
        initEvents.add(e3);
        initEvents.add(e4);
        
        //LP1: e1(null, 0); e5(e4, 1); e6(e2, 2); e7(e3, 2); 
        //LP2:       e2(null, 1); 
        //LP3:       e3(null, 1)
        //LP4  e4(null, 0); 
        
        //Construct the relation
        relGraph.addVertex(e1);
        relGraph.addVertex(e2);
        relGraph.addVertex(e3);
        relGraph.addVertex(e4);
        relGraph.addVertex(e5);
        relGraph.addVertex(e6);
        relGraph.addVertex(e7);
        
        //Set antecedent relation (event that caused this one)
        relGraph.addEdge(e2, e6);
        relGraph.addEdge(e3, e7);
        relGraph.addEdge(e4, e5);
        
        
        //Set precedent relation (event that was proc RIGHT before this one)
        relGraph.addEdge(e1, e5);
        relGraph.addEdge(e5, e6);
        relGraph.addEdge(e6, e7);
        
        //Expected 4
        System.out.printf("Test 1: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }
    
    private static void test2()
    {
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph 
            = new ListenableDirectedWeightedGraph<>(DefaultEdge.class);
    
        List<IEvent> initEvents = new ArrayList<>();
        IEvent e1 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e2 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e3 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e4 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e5 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e6 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e7 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e8 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e9 = new MaxStopAwareEvent(3, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e10 = new MaxStopAwareEvent(3, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e11 = new MaxStopAwareEvent(4, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        
        initEvents.add(e1);
        initEvents.add(e2);
        initEvents.add(e3);
        initEvents.add(e4);
        
        //Construct the relation
        relGraph.addVertex(e1);
        relGraph.addVertex(e2);
        relGraph.addVertex(e3);
        relGraph.addVertex(e4);
        relGraph.addVertex(e5);
        relGraph.addVertex(e6);
        relGraph.addVertex(e7);
        relGraph.addVertex(e8);
        relGraph.addVertex(e9);
        relGraph.addVertex(e10);
        relGraph.addVertex(e11);
        
        
        //LP1: e1(null, 0); e5(e4, 1); e6(e2, 2); e7(e3, 2); 
        //LP2:       e2(null, 1); 
        //LP3:       e3(null, 1)
        //LP4  e4(null, 0); e8(e5, 2); e9(e6, 3); e10(e7, 3); e11(e10, 4);
        
        //Set antecedent relation (event that caused this one)
        relGraph.addEdge(e2, e6);
        relGraph.addEdge(e3, e7);
        relGraph.addEdge(e4, e5);
        relGraph.addEdge(e5, e8);
        relGraph.addEdge(e6, e9);
        relGraph.addEdge(e7, e10);
        relGraph.addEdge(e10, e11);
        
        
        //Set precedent relation (event that was proc RIGHT before this one)
        relGraph.addEdge(e1, e5);
        relGraph.addEdge(e5, e6);
        relGraph.addEdge(e6, e7);
        relGraph.addEdge(e4, e8);
        relGraph.addEdge(e8, e9);
        relGraph.addEdge(e9, e10);
        relGraph.addEdge(e10, e11);
        
        //Expected: 5
        System.out.printf("Test 2: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }


    private static void test3()
    {
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph 
            = new ListenableDirectedWeightedGraph<>(DefaultEdge.class);
    
        List<IEvent> initEvents = new ArrayList<>();
        IEvent e1 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e2 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e3 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e4 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e5 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e6 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e7 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e8 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e9 = new MaxStopAwareEvent(3, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e10 = new MaxStopAwareEvent(3, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e11 = new MaxStopAwareEvent(4, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e12 = new MaxStopAwareEvent(4, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        
        initEvents.add(e1);
        initEvents.add(e2);
        
        //Construct the relation
        relGraph.addVertex(e1);
        relGraph.addVertex(e2);
        relGraph.addVertex(e3);
        relGraph.addVertex(e4);
        relGraph.addVertex(e5);
        relGraph.addVertex(e6);
        relGraph.addVertex(e7);
        relGraph.addVertex(e8);
        relGraph.addVertex(e9);
        relGraph.addVertex(e10);
        relGraph.addVertex(e11);
        relGraph.addVertex(e12);
        
        //start from 1
        relGraph.addEdge(e1, e3);
        relGraph.addEdge(e1, e4);
        relGraph.addEdge(e1, e8);
        
        //from 2
        relGraph.addEdge(e2, e5);
        relGraph.addEdge(e2, e6);
        relGraph.addEdge(e2, e7);
        
        //from 3
        relGraph.addEdge(e3, e5);
        
        //from 4
        relGraph.addEdge(e4, e8);
        relGraph.addEdge(e4, e11);
        
        //From 5
        relGraph.addEdge(e5, e8);
        relGraph.addEdge(e5, e9);
        
        //from 6
        relGraph.addEdge(e6, e11);
        relGraph.addEdge(e6, e10);
        
        //from 7
        relGraph.addEdge(e7, e10);
        
        //from 8
        relGraph.addEdge(e8, e12);
        relGraph.addEdge(e8, e9);
        
        //from 9
        relGraph.addEdge(e9, e11);
        
        //Expected: 6
        System.out.printf("Test 3: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }

    private static void test4()
    {
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph 
            = new ListenableDirectedWeightedGraph<>(DefaultEdge.class);
    
        List<IEvent> initEvents = new ArrayList<>();
        IEvent e1 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e2 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e3 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e4 = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e5 = new MaxStopAwareEvent(1, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e6 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e7 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e8 = new MaxStopAwareEvent(2, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        IEvent e9 = new MaxStopAwareEvent(3, EventType.ARRIVAL, 0, Collections.EMPTY_LIST);
        
        initEvents.add(e1);
        
        //Construct the relation
        relGraph.addVertex(e1);
        relGraph.addVertex(e2);
        relGraph.addVertex(e3);
        relGraph.addVertex(e4);
        relGraph.addVertex(e5);
        relGraph.addVertex(e6);
        relGraph.addVertex(e7);
        relGraph.addVertex(e8);
        relGraph.addVertex(e9);
        
        //start from 1
        relGraph.addEdge(e1, e2);
        relGraph.addEdge(e1, e3);
        
        //from 2
        relGraph.addEdge(e2, e3);
        relGraph.addEdge(e2, e4);
        
        //from 3
        relGraph.addEdge(e3, e5);
        relGraph.addEdge(e3, e9);
        
        //from 4
        relGraph.addEdge(e4, e6);
        
        //From 5
        relGraph.addEdge(e5, e6);
        relGraph.addEdge(e5, e7);
        
        //from 6
        relGraph.addEdge(e6, e8);
        relGraph.addEdge(e6, e9);
        
        System.out.printf("Test 4: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }
    
    /**
     * Simple event graph: no inter-LP comm. LP count: 4
     */
    private static void testSimpleGraph()
    {        
        List<IEvent> initEvents = new ArrayList<>();
        Map<Integer, List<IEvent>> eventMap = new HashMap<>(); 
        
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph =
            genSimpleGraph(4, 
                           new int[]{100, 200, 300, 400}, 
                           initEvents, 
                           eventMap);
        
        System.out.printf("Simple Graph: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }
    
    /**
     * Still 4 LPs.
     * Simple but added 2 inter-LP comm
     * Length should be same as testSimpleGraph
     */
    private static void testSimpleGraph1()
    {        
        List<IEvent> initEvents = new ArrayList<>();
        Map<Integer, List<IEvent>> eventMap = new HashMap<>(); 
        
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph =
            genSimpleGraph(4, 
                           new int[]{100, 200, 300, 400},
                           initEvents,
                           eventMap);
        
        //Modify the graph to add inter LP comm
        //LP1's event#5 causes LP3's event#10
        //LP2's event#19 causes LP4's event#91
        relGraph.addEdge(eventMap.get(0).get(5), 
                         eventMap.get(2).get(10));
        
        relGraph.addEdge(eventMap.get(1).get(19),
                         eventMap.get(3).get(91));
        
        
        System.out.printf("Simple Graph 1: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }
    
     /**
     * Still 4 LPs.
     * Simple but added 2 inter-LP comm
     * Length should NOT be same as testSimpleGraph
     */
    private static void testSimpleGraph2()
    {        
        List<IEvent> initEvents = new ArrayList<>();
        Map<Integer, List<IEvent>> eventMap = new HashMap<>(); 
        
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph =
            genSimpleGraph(4, 
                           new int[]{100, 200, 300, 400},
                           initEvents,
                           eventMap);
        
        //Modify the graph to add inter LP comm
        //LP1's first event causes LP4's first event (init events should not contain lp4's first event)
        //LP4's last event causes LP3's last event
        initEvents.remove(3);
        relGraph.addEdge(eventMap.get(0).get(0), 
                         eventMap.get(3).get(0));
        
        relGraph.addEdge(eventMap.get(3).get(399),
                         eventMap.get(2).get(299));
        
        
        System.out.printf("Simple Graph 2: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }
    
    private static void testSimpleGraph3()
    {        
        List<IEvent> initEvents = new ArrayList<>();
        Map<Integer, List<IEvent>> eventMap = new HashMap<>(); 
        
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> relGraph =
            genSimpleGraph(4, 
                           new int[]{100, 200, 300, 400},
                           initEvents,
                           eventMap);
        
        //Modify the graph to add inter LP comm
        //LP1's first event causes LP4's first event (init events should not contain lp4's first event)
        //LP4's last event causes LP3's last event
        //LP3's last causes LP2's last
        //LP2's last causes LP1's last
        initEvents.remove(3);
        relGraph.addEdge(eventMap.get(0).get(0), 
                         eventMap.get(3).get(0));
        
        relGraph.addEdge(eventMap.get(3).get(399),
                         eventMap.get(2).get(299));
        relGraph.addEdge(eventMap.get(2).get(299),
                         eventMap.get(1).get(199));
        relGraph.addEdge(eventMap.get(1).get(199),
                         eventMap.get(0).get(99));
        
        
        System.out.printf("Simple Graph 3: CP length alg1 = %d; alg2 = %d\n", 
                          CriticalPathLengthCalculator.calcPathLength(relGraph, initEvents),
                          CriticalPathLengthCalculator.backflowAlg(relGraph, initEvents));
    }
    
    /**
     * Generates the event graph for 4 lps in which no inter-LP communication occurs 
     * This means the critical time (CP len) should be the largest processing time * num event
     * among the LPs
     * @param nLP
     * @param eventsPerLP array specifying the number of event in each lp
     * @return 
     */
    private static ListenableDirectedWeightedGraph<IEvent, DefaultEdge> genSimpleGraph(int nLP,
                                                                                       int[] eventsPerLP,
                                                                                       List<IEvent> initEvents,
                                                                                       Map<Integer, List<IEvent>> eventMap)
    {
        if (eventsPerLP.length != nLP)
            throw new IllegalArgumentException("array size must match nLP");
        
        ListenableDirectedWeightedGraph<IEvent, DefaultEdge> graph = new ListenableDirectedWeightedGraph<>(DefaultEdge.class);
        IEvent src, dest;
        for (int i = 0; i < nLP; ++i)
        {
            List<IEvent> events = new ArrayList<>(eventsPerLP[i]);
            
            src = new MaxStopAwareEvent(0, EventType.ARRIVAL, 0, null);
            graph.addVertex(src);
            initEvents.add(src);
            events.add(src);
            
            for (int j = 1; j < eventsPerLP[i]; ++j)
            {
                dest = new MaxStopAwareEvent(j, EventType.ARRIVAL, 0, null);
                events.add(dest);
                
                graph.addVertex(dest);
                graph.addEdge(src, dest);
                src = dest;
            }
            
            eventMap.put(i, events);
        }
        return graph;
    }
}
