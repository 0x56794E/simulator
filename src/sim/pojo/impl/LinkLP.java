package sim.pojo.impl;

import sim.pojo.AbstractLP;
import sim.pojo.IEvent;
import sim.Simulator;
import sim.enums.EventType;
import sim.pojo.ILP;

public class LinkLP extends AbstractLP
{
    private int node1Id;
    private int node2Id;

    public LinkLP(int id, int node1Id, int node2Id, long transTime, long switchTime, Simulator simulator)
    {
        super(id, transTime, switchTime, switchTime, simulator);

        this.node1Id = node1Id;
        this.node2Id = node2Id;
    }

    public int getNode1Id()
    {
        return node1Id;
    }

    public int getNode2Id()
    {
        return node2Id;
    }

    @Override
    public void handleEvent(IEvent e)
    {
        MaxStopAwareEvent event = (MaxStopAwareEvent) e;
        IEvent newEvent = null;
        
        //Departure from a link == switching
        //Schedule arrival at another link
        if (event.getType() == EventType.DEPARTURE)
        {
            //Schedule arrival at another
            if (!event.lastStop())
            {
                ILP nextStop = neiMap.get(event.getNextStopId());
                newEvent = new MaxStopAwareEvent(nextStop.getCurrentTime() + switchTime,
                                                           EventType.ARRIVAL, 
                                                           event.getCurrentStopIndex() + 1,
                                                           event.getStops());
                nextStop.scheduleEvent(newEvent);
            }
        }
        //Arrival = will schedule departure == process of transmitting across the link => longer time
        else
        {
            newEvent = new MaxStopAwareEvent(event.getTimestamp() + transTime, 
                    EventType.DEPARTURE, event.getCurrentStopIndex(), event.getStops());
            
            //Schedule departure
            scheduleEvent(newEvent);
        }
        
        onEventProcessed(event, newEvent);        
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof LinkLP))
            return false;

        LinkLP that = (LinkLP) other;
        return that.id == this.id;
    }

    @Override
    public int hashCode()
    {
        return this.id;
    }
    
    @Override 
    public String toString()
    {
        return String.format("LinkLP(id=%s,node1=%s,node2=%s)",
                id, node1Id, node2Id);
    }
}
