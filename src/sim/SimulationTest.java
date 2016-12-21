package sim;

import java.awt.*;

import javax.swing.*;

import org.jbox2d.testbed.framework.*;
import org.jbox2d.testbed.framework.AbstractTestbedController.MouseBehavior;
import org.jbox2d.testbed.framework.AbstractTestbedController.UpdateBehavior;
import org.jbox2d.testbed.framework.j2d.DebugDrawJ2D;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;
import org.jbox2d.testbed.framework.j2d.TestbedSidePanel;

public class SimulationTest {

	public SimulationTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		TestbedModel model = new TestbedModel();
		final AbstractTestbedController controller = new TestbedController(model, UpdateBehavior.UPDATE_CALLED, MouseBehavior.NORMAL, new TestbedErrorHandler() {
			@Override
			public void serializationError(Exception e, String message) {
				JOptionPane.showMessageDialog(null, message, "Serialization Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		TestPanelJ2D panel = new TestPanelJ2D(model, controller);
		model.setPanel(panel);
		model.setDebugDraw(new DebugDrawJ2D(panel, true));
		model.addTest(new LaunchTest());
		JFrame testbed = new JFrame();
		testbed.setLayout(new BorderLayout());
	    TestbedSidePanel side = new TestbedSidePanel(model, controller);
	    testbed.add((Component) panel, "Center");
	    testbed.add(new JScrollPane(side), "East");
	    testbed.pack();
	    testbed.setVisible(true);
	    testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    System.out.println(System.getProperty("java.home"));

	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        controller.playTest(0);
	        controller.start();
	      }
	    });
	  }

	}


