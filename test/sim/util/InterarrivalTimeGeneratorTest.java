/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util;

/**
 *
 * @author q393832
 */
public class InterarrivalTimeGeneratorTest 
{
    public static void main(String[] args)
    {
        //Expected an event occurs every 5 unit time
        double lambda = 5;
        InterarrivalTimeGenerator gen = new InterarrivalTimeGenerator(lambda);
        
        //If we generate enough samples 
        //it's expected that the avg/mean of the sequence is close to period
        double sum = 0, num;
        int n = 100;
        for (int i = 0; i < n; ++i)
        {
            num = gen.nextTime();
            sum += gen.getLastInterval();
            System.out.printf("Gen'ed: %f\n", num);
        }
        System.out.println(sum/n);
    }
}
