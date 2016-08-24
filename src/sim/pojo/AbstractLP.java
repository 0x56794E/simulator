package sim.pojo;

import sim.Simulator;

import java.util.*;

/**
 * Created by VyNguyen on 2/10/2016.
 */
public abstract class AbstractLP implements ILP
{
    //LP properties
    protected final int id;
    protected final long transTime;
    protected final long switchTime;
    protected final Map<Integer, ILP> neiMap = new HashMap<Integer, ILP>();
    private final long lookahead;    
    private final Simulator simulator;
    private final List<Integer> neiIds = new ArrayList<Integer>();
    
    //Generators
    private final Random nextStopGen; //For next stop gen
    
    //LP state
    private IEvent lastProcEvent; //Used for keeping track of the predecessor relation
    private int totalEventProc = 0; //number of events processed so far
    private final Queue<IEvent> fel = new PriorityQueue<IEvent>(100); //future event list
    private long currentTime = 0;
    private final List<IEvent> currentEpochEvents = new LinkedList<IEvent>(); //all events that've been processed in current epoch.
    
    protected AbstractLP(int id, long transTime, long switchTime, long lookahead, Simulator simulator)
    {
        this.id = id;
        this.lookahead = lookahead;
        this.transTime = transTime;
        this.switchTime = switchTime;

        this.nextStopGen = new Random(System.currentTimeMillis());
        this.simulator = simulator;
    }
    
    @Override 
    public final int getTotalEventProc()
    {
        return totalEventProc;
    }
    
    @Override
    public final boolean hasEvent()
    {
        return !fel.isEmpty();
    }

    @Override
    public final void resetCurrentEpochEventCount()
    {
        currentEpochEvents.clear();
    }
    
    @Override
    public final int getCurrentEpochEventCount()
    {
        return currentEpochEvents.size();
    }

    @Override
    public final List<IEvent> getCurrentEpochEvents()
    {
        return currentEpochEvents;
    }
    
    @Override
    public final long getLPLBTS()
    {
        //If no events in the FEL
        //then this LP can process event
        //of any timestamp => thus the LBTS is max possible
        if (fel.isEmpty())
            return Long.MAX_VALUE;
        return fel.peek().getTimestamp() + lookahead;

    }

    public final void addNeighbor(ILP nei)
    {
        if (!neiMap.containsKey(nei.getId()))
        {
            neiMap.put(nei.getId(), nei);
            neiIds.add(nei.getId());
        }
    }

    @Override
    public final Queue<IEvent> getFEL()
    {
        return fel;
    }

    @Override
    public final int getId()
    {
        return id;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public final ArrayList<ILP> getAllNeis()
    {
        return new ArrayList<ILP>(neiMap.values());
    }

    @Override
    public final ILP getNeiById(int id)
    {
        return neiMap.get(id);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public final void scheduleEvent(IEvent event)
    {
        fel.add(event);
    }

    @Override
    public final int nextNeiId()
    {
        ArrayList<ILP> neis = getAllNeis();
        return neis.get(nextStopGen.nextInt(getAllNeis().size())).getId();
    }
    
    @Override
    public final int genHopCount(int maxHop, int minHop)
    {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextInt(maxHop) + minHop;
    }
    
    @Override
    public final long getCurrentTime()
    {
        return currentTime;
    }
    
    protected final void onEventProcessed(IEvent event, IEvent newEvent)
    {
        //Record event for current epoch
        currentEpochEvents.add(event);
        
        //Count event in total number of events processed by this LP
        ++totalEventProc;   
        
        //Increment time
        currentTime = event.getTimestamp();    
        
        //If new event is created, record the relation
        if (newEvent != null)
            recordRelation(event, newEvent);
    }
    
    private void recordRelation(IEvent event, IEvent newEvent)
    {        
        //Add the new event to the graph.
        //As soon as an event is created, it needs to be added to the graph
        //as vertex.
        //Initial events are added to this graph via addInitialEvent method in the Simulator class
        //The link - repping the relationship - will be added later.
        simulator.getRelGraph().addVertex(newEvent);
        
        //Set the ante rel
        simulator.getRelGraph().addEdge(event, newEvent);
        
        //Set the pred rel
        if (lastProcEvent != null)
            simulator.getRelGraph().addEdge(lastProcEvent, event);
        
        lastProcEvent = event;
    }
    
    @Override
    public long nextArrival()
    {
        throw new UnsupportedOperationException("Not supported");
    }
        
    //Object stuff
    @Override
    public int hashCode()
    {
        return this.id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !obj.getClass().equals(this.getClass()))
            return false;
        AbstractLP that = (AbstractLP) obj;
        return this.id == that.id;
    }    
}
