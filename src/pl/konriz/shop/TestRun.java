package pl.konriz.shop;

public class TestRun {

	public static void main(String[] args) throws ClassNotFoundException {
		
		GUI gui = new GUI(new Shop());
		gui.runGUI();
		
	}

}