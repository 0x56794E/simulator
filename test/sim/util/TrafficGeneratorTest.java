/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util;

import java.io.IOException;

/**
 *
 * @author q393832
 */
public class TrafficGeneratorTest 
{
    public static void main(String[] args) throws IOException
    {
        String fileName = "g1000_20"; //300 nodes; min deg == 50
        int numPacket = 50000;
        int arrivalRate = 5; //lambda = 1/5; (unit time)
        int maxEdge = 20;
        TrafficGenerator.genTraffic(fileName, maxEdge, numPacket, arrivalRate);
    }
}
