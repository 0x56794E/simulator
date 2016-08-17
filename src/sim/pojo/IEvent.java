package sim.pojo;

import java.util.List;
import sim.enums.IEventType;

/**
 * User: VyNguyen
 * Date: 2/3/16
 * Time: 2:58 PM
 */
public interface IEvent extends Comparable
{
    public long getTimestamp();
    public IEventType getType();
    public boolean lastStop();
    public List<Integer> getStops();
    
    public int getCurrentStopIndex();
    public int getNextStopId();
    
    
    public void setCriticalTime(int weight);
    public int getCriticalTime();
    
    
    public int getPredCount();
    public void setPredCount(int predCount);
    public void decPred();
}
