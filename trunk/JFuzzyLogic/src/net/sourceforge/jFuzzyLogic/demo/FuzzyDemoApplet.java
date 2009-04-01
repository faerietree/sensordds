
package net.sourceforge.jFuzzyLogic.demo;

import java.io.InputStream;
import javax.swing.JMenuBar;
import net.sourceforge.jFuzzyLogic.demo.IP.IPDemo;

/**
 * P.J.Leonard : Fuzzy logic demo Sep 2008
 *
 */
public  class FuzzyDemoApplet extends javax.swing.JApplet {
   
    FuzzyDemo demo;

    /** Initializes the applet IPApplet */
    @Override
    public void init() {
       InputStream str=FuzzyDemoApplet.class.getResourceAsStream("ip2.fcl");
        demo = new IPDemo(str,true);     
        JMenuBar menuBar = demo.getJMenuBar();
        setJMenuBar(menuBar);
        setContentPane(demo.getPanel());
        setSize(800,600);
    }

    
    // start the applet
    @Override
    public void start() {
       demo.start();
     }
    
    // stop the applet
    @Override
    public void stop() {
        demo.stop();
    } 
}



