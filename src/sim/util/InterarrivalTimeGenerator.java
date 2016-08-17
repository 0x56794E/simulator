/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util;

import java.util.Random;

/**
 * Generate random timings for a Poisson process
 * Background: 
 * (1) What's a Poisson process? Any time you have events which occur individually 
 * at random moments, but which tend to occur at an average rate when viewed as
 * a group, you have a Poisson process.
 * e.g., USGS estimates that each year, there are approx. 13,000 earthquakes of
 * magnitude 4+ around the world. Those earthquakes scatter randomly throughout 
 * the year, which means on average, one earthquake happens every 40 minutes.
 * 
 * (2) The exponential distribution.
 * From the example above, let's define a variable lambda = 1/40 - called rate parameter.
 * The rate param lambda is a measure of frequency - the avg rate of events per unit time.
 * Knowing this, we can ask question like what's the probability that an earthquake 
 * will happen in the next 10 minutes?
 * The cumulative distribution function for the exponential distribution gives answer
 * to this.
 * The function looks like this:
 * F(x) = 1 - exp(-lambda*x)
 * 
 * This implies the more time passes, the more likely it is that there will be an earthquake.
 * e.g., F(1) is definitely less than F(10) => it's less likely to have an earthquake in the 
 * next 1 minute than in the next 10.
 * 
 * (3) Simulate sequence of occurrences given rate param lambda
 * nextTime = -lnU/lambda where U is a random value between 0 and 1.
 * @author VyNguyen
 */
public class InterarrivalTimeGenerator 
{
    private Random randGen;
    private double lambda; //lambda
    
    //The point in time
    private double currentTime;
    
    //The last interval generated
    //The idea is that if nextTime is called enough times,
    //The avg of the intervals generated should be close to 1/lambda
    private double lastInterval;
    
    public InterarrivalTimeGenerator(double rateParam)
    {
         randGen = new Random(System.currentTimeMillis());
         lambda = rateParam;
         currentTime = 0;
    }
    
    private void doGen()
    {        
        lastInterval = -Math.log(1.0 - randGen.nextDouble())*lambda;
        currentTime += lastInterval;
    }
    
    /**
     * @return the next interarrival time
     */
    public double nextTime()
    {
        doGen();
        return currentTime;
    }
    
    public double nextInterval()
    {
        doGen();
        return lastInterval;
    }

    public double getLastInterval()
    {
        return lastInterval;
    }
}
