package sim.pojo;

import sim.Simulator;

import java.util.*;
import sim.util.InterarrivalTimeGenerator;

/**
 * Created by VyNguyen on 2/10/2016.
 */
public abstract class AbstractLP implements ILP
{
    protected long lookahead;
    protected long transTime;
    protected long switchTime;
    protected Queue<IEvent> fel = new PriorityQueue<IEvent>(100); //future event list
    protected int id;
    protected Map<Integer, ILP> neiMap = new HashMap<Integer, ILP>();
    protected List<Integer> neiIds = new ArrayList<Integer>();
    protected Random nextStopGen; //For next stop gen
    protected long currentTime = 0;
    protected int eventInEpoch = 0; //number of events proc in this epoch
    protected Simulator simulator;
    protected IEvent lastProcEvent; //Used for keeping track of the predecessor relation
    protected InterarrivalTimeGenerator arrivalTimeGen;
    protected int totalEventProc = 0; //number of events processed so far
    
    @Override 
    public int getTotalEventProc()
    {
        return totalEventProc;
    }
    
    @Override
    public final boolean hasEvent()
    {
        return !fel.isEmpty();
    }

    @Override
    public final void resetEventCountPerEpoch()
    {
        eventInEpoch = 0;
    }

    @Override
    public int getCurrentEpochEventCount()
    {
        return eventInEpoch;
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
        simulator.getRelGraph().addVertex(event);
        fel.add(event);
    }

    @Override
    public final int nextNeiId()
    {
        ArrayList<ILP> neis = getAllNeis();
        return neis.get(nextStopGen.nextInt(getAllNeis().size())).getId();
    }

    public final int nextInt(int max)
    {
        return nextStopGen.nextInt(max);
    }
    
    @Override
    public final int genHopCount(int maxHop, int minHop)
    {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextInt(maxHop) + minHop;
    }
    
    protected final void recordRelation(IEvent event, IEvent newEvent)
    {        
        //Set the ante rel
        simulator.getRelGraph().addEdge(event, newEvent);
        
        //Set the pred rel
        if (lastProcEvent != null)
            simulator.getRelGraph().addEdge(lastProcEvent, event);
        
        lastProcEvent = event;
    }
    
    @Override
    public final long nextArrival()
    {
        return (long)arrivalTimeGen.nextTime();
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
