package sim;

import org.jbox2d.testbed.framework.TestbedTest;

public class LaunchTest extends TestbedTest {

	public LaunchTest() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initTest(boolean deserialized) {
		 Simulation sim = new Simulation(m_world);
		
	}

	@Override
	public String getTestName() {
		return "Genetic car";
	}

}
