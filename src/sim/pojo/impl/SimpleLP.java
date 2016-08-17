/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.pojo.impl;

import java.util.Random;
import sim.pojo.AbstractLP;
import sim.pojo.IEvent;
import sim.util.InterarrivalTimeGenerator;


/**
 * To be used in traffic gen
 * @author VyNguyen
 */
public class SimpleLP extends AbstractLP
{

    public SimpleLP(int id, int arrivalRate)
    {
        this.id = id;
        this.nextStopGen = new Random(System.currentTimeMillis());
        this.arrivalTimeGen = new InterarrivalTimeGenerator(1.0/arrivalRate);
    }
    
    @Override
    public void handleEvent(IEvent event)
    {
        //Do nothing
    }    
}
