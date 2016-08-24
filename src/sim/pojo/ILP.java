package sim.pojo;

import java.util.ArrayList;
import java.util.List;
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
    public void resetCurrentEpochEventCount();
    public int getCurrentEpochEventCount();
    public List<IEvent> getCurrentEpochEvents();
    public int getTotalEventProc();
    public boolean hasEvent();
    public long getCurrentTime();
    
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
