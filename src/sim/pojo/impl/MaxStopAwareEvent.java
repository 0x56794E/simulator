package sim.pojo.impl;

import sim.pojo.IEvent;
import sim.enums.IEventType;
import sim.enums.EventType;

import java.util.List;
import sim.util.EventIDGenerator;

public class MaxStopAwareEvent implements IEvent
{
    private final int id;
    final private long timestamp;
    final private EventType type;
    final private int currentStopIdx;
    final private List<Integer> stops;
    
    private int criticalTime = 0;
   
    private int predCount = 0;
    
    public MaxStopAwareEvent(long timestamp, EventType type, int currentStopIdx, List<Integer> stops)
    {
        this.timestamp = timestamp;
        this.type = type;
        this.currentStopIdx = currentStopIdx;
        this.stops = stops;
        this.id = EventIDGenerator.getNextId();
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IEventType getType()
    {
        return type;
    }

    @Override
    public boolean lastStop()
    {
        return currentStopIdx == stops.size() - 1;
    }

    @Override
    public List<Integer> getStops()
    {
        return stops;
    }

    @Override
    public int getCurrentStopIndex()
    {
        return currentStopIdx;
    }

    @Override
    public int getNextStopId()
    {
        return stops.get(currentStopIdx + 1);
    }

    @Override
    public String toString()
    {
        return String.format("Event[timestamp=%s, type=%s]", timestamp, type);
    }

    @Override
    public int compareTo(Object o)
    {
        if (!(o instanceof MaxStopAwareEvent))
            return 2; //TODO: fix this

        MaxStopAwareEvent that = (MaxStopAwareEvent) o;
        return this.timestamp > that.timestamp
                    ? 1
                    : this.timestamp == that.timestamp ? 0 : - 1;

    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MaxStopAwareEvent))
            return false;
        
        MaxStopAwareEvent that = (MaxStopAwareEvent) obj;
        return this.id == that.id;
    }
    
    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public int getPredCount() 
    {
        return predCount;
    }

    @Override
    public void setPredCount(int predCount) 
    {
        this.predCount = predCount;
    }
    
    @Override 
    public void decPred()
    {
        --predCount;
    }
    
    @Override
    public void setCriticalTime(int criticalTime)
    {
        this.criticalTime = criticalTime;
    }
    
    @Override
    public int getCriticalTime()
    {
        return criticalTime;
    }
}
