package sim.pojo.impl;

import sim.pojo.AbstractLP;
import sim.pojo.IEvent;
import sim.util.InterarrivalTimeGenerator;


/**
 * To be used in traffic gen
 * @author VyNguyen
 */
public class SimpleLP extends AbstractLP
{
    private final InterarrivalTimeGenerator arrivalTimeGen;
    public SimpleLP(int id, int arrivalRate)
    {
        super(id, 0, 0, 0, null);
        arrivalTimeGen = new InterarrivalTimeGenerator(1.0/arrivalRate);
    }
    
    @Override
    public void handleEvent(IEvent event)
    {
        throw new UnsupportedOperationException("Not supported");
    }    
    
    @Override
    public long nextArrival()
    {
        return (long)arrivalTimeGen.nextTime();
    }
}
