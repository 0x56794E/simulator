/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util;

import com.netflix.nicobar.core.module.GraphUtils;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import sim.pojo.IEvent;

/**
 * @author Vy Nguyen
 */
public class CriticalPathLengthCalculator
{
    /**
     * Algorithm starts with initial events
     */
    public static int calcPathLength(ListenableDirectedWeightedGraph<IEvent, DefaultEdge> graph,
                                     Collection<IEvent> initialEvents)
    {
        DirectedNeighborIndex<IEvent, DefaultWeightedEdge> neiIndex 
                = new DirectedNeighborIndex(graph);
        
        ArrayDeque<IEvent> q = new ArrayDeque<IEvent>(initialEvents);
        for (IEvent u : graph.vertexSet())
            u.setCriticalTime(0);
        
        for (IEvent event : initialEvents)
        {
            event.setCriticalTime(1);
        }
        
        //BFS
        int tmpCrit;
        Set<IEvent> added = new HashSet<IEvent>();
        while (!q.isEmpty())
        {
            IEvent event = q.remove();
            added.remove(event);
            tmpCrit = event.getCriticalTime() + 1;
            for (IEvent successor : neiIndex.successorsOf(event))
            {
                if (successor.getCriticalTime() < tmpCrit)
                    successor.setCriticalTime(tmpCrit);
                
                if (!added.contains(successor))
                {
                    q.add(successor);
                    added.add(successor);
                }
            }
        }
        
        tmpCrit = 0;
        for (IEvent event : graph.vertexSet())
            if (event.getCriticalTime() > tmpCrit)
                tmpCrit = event.getCriticalTime();
        
        return tmpCrit;
    }
    
    
    
    /**
     * Algorithm:
     *   1. Begin at the END vertex of the project digraph and assign it a critical time of zero.
     *   2. Move backwards to each vertex that is incident to (having an arrow pointing to)
     *   END and assign it a critical time.
     *   Note: For vertices incident to (having an arrow pointing to) END, the critical time
     *   is the same as the processing time.
     *   3. For each of the vertices in Step 2, move backwards again to each vertex that is
     *   incident to (precedes) it and assign it a critical time.
     *   Note: For each of these vertices, the critical time is its processing time added to
     *   the LARGEST critical time of the vertices incident from (having an arrow pointing
     *   away from) that vertex.
     *   4. Continue this process until each vertex of the project digraph has a critical time.
     *   Note: The critical time for each vertex is its processing time added to the
     *   LARGEST critical time of the vertices incident from (having an arrow pointing
     *   away from) that vertex.
     * @param graph
     * @param initialEvents
     * @return 
     */
    public static int backflowAlg(ListenableDirectedWeightedGraph<IEvent, DefaultEdge> graph,
                                  Collection<IEvent> startVertices)
    {
        DirectedNeighborIndex<IEvent, DefaultEdge> neiIndex = new DirectedNeighborIndex<IEvent, DefaultEdge>(graph);
        Set<IEvent> endVertices = GraphUtils.getLeafVertices(graph);
        ArrayDeque<IEvent> q = new ArrayDeque<IEvent>();
        Set<IEvent> checkedVertices = new HashSet<IEvent>(); //keep track of which vertices have been checked
        
        //Assign critical time to end vertices which is the same as proc time
        for (IEvent endVertex : endVertices)
        {
            endVertex.setCriticalTime(1);
            q.add(endVertex);
        }
        
        //Do BFS - find all vertices preceding end vertices        
        int maxCrit = 1;
        IEvent vertex;
        while (!q.isEmpty())
        {
            vertex = q.remove();
            checkedVertices.remove(vertex);
            
            for (IEvent pred : neiIndex.predecessorsOf(vertex))
            {
                maxCrit = 1;
                //find the max critical time of the successor of the pred
                for (IEvent suc : neiIndex.successorsOf(pred))
                    if (suc.getCriticalTime() > maxCrit)
                        maxCrit = suc.getCriticalTime();
                
                pred.setCriticalTime(maxCrit + 1);
                
                if (!checkedVertices.contains(pred))
                    q.add(pred);
            }
        }
        
        //The critical time of the project is max of the startVertices
        maxCrit = 1;
        for (IEvent event : startVertices)
            if (event.getCriticalTime() > maxCrit)
                maxCrit = event.getCriticalTime();
        
       return maxCrit;        
    }
}
