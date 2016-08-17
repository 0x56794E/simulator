package sim.pojo.impl;

import sim.pojo.AbstractLP;
import sim.pojo.IEvent;
import sim.Simulator;
import sim.enums.EventType;

import java.util.Random;

/**
 * Created by VyNguyen on 2/10/2016.
 */
public class NodeLP extends AbstractLP
{
    public NodeLP(int id, long transTime, long switchTime, Simulator simulator)
    {
        this.id = id;
        this.lookahead = transTime;
        this.transTime = transTime;
        this.switchTime = switchTime;

        nextStopGen = new Random(System.currentTimeMillis());
        this.simulator = simulator;
    }

    @Override
    public void handleEvent(IEvent e)
    {
        MaxStopAwareEvent event = (MaxStopAwareEvent) e;
        currentTime = e.getTimestamp();
        eventInEpoch += 1;
        totalEventProc++;

        IEvent newEvent = null;
        
        //Departure from a node == transmitting => longer time
        //Schedule arrival at another node
        if (event.getType() == EventType.DEPARTURE)
        {
            newEvent = new MaxStopAwareEvent(event.getTimestamp() + transTime,
                                   EventType.ARRIVAL, event.getCurrentStopIndex() + 1, event.getStops());
            neiMap.get(event.getNextStopId()).scheduleEvent(newEvent);

        }
        //Arrival = will schedule departure == process of switching => shorter time
        else
        {
            //Schedule departure
            //If this is the last stop then the package has arrived
            //at its final destination => no need for departure event
            if (!event.lastStop())
            {
                newEvent = new MaxStopAwareEvent(event.getTimestamp() + switchTime,
                        EventType.DEPARTURE, event.getCurrentStopIndex(), event.getStops());
                
                scheduleEvent(newEvent);
            }
            else
            {
                return;
            }
        }
        
        recordRelation(event, newEvent);
    }

    @Override
    public String toString()
    {
        return String.format("NodeLP(id=%d)", id);
    }
}
