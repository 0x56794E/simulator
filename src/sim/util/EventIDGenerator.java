/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util;

/**
 * Singleton
 * Dirty way to get ID
 * (Desperate time calls for desperate measure)
 * @author 0x56794e
 */
public class EventIDGenerator 
{
    private static int id = -1;
    
    private EventIDGenerator()
    {
    }
        
    public static int getNextId()
    {
        ++id;
        return id;
    }
}
