package sim.pojo;

import java.util.ArrayList;
import java.util.Queue;

public interface ILP
{
    public Queue<IEvent> getFEL();
    public int getId();
    public void addNeighbor(ILP nei);
    public ArrayList<ILP> getAllNeis();
    public ILP getNeiById(int id);
    public void handleEvent(IEvent event);
    public void scheduleEvent(IEvent event);
    public long getLPLBTS();
    public void resetEventCountPerEpoch();
    public int getCurrentEpochEventCount();
    public int getTotalEventProc();
    public boolean hasEvent();
    
    /**
     * Randomly generates the next arrival time such that the interarrival times
     * follow a predefined distribution.
     * @return 
     */
    public long nextArrival();

    /**
     * Randomly choose the next neighbor
     * @return the ID of the nei
     */
    public int nextNeiId();

    public int genHopCount(int maxHop, int minHop);
}
