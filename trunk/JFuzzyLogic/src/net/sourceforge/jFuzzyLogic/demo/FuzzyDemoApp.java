
package net.sourceforge.jFuzzyLogic.demo;


import java.io.FileNotFoundException;
import javax.swing.JFrame;
import net.sourceforge.jFuzzyLogic.demo.IP.IPDemo;

/**
 *
 * @author pjl
 */
public class FuzzyDemoApp {

    public static void main(String args[]) throws FileNotFoundException {
        JFrame frame=new JFrame();
        
        FuzzyDemo demo = new IPDemo(FuzzyDemoApplet.class.getResourceAsStream("ip2.fcl"),false);
     //   FuzzyDemo demo = new IPDemo(new FileInputStream(new File("fcl/ip2.fcl")));    
      //  frame.setJMenuBar(demo.getJMenuBar());
        
        
        frame.setContentPane(demo.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Inverted Pendulum Fuzzy Control Logic Demo");
        demo.start();
        frame.setSize(10000,8000);
     //   frame.validate();
        frame.setVisible(true);
    }

  
}
