package TP2;

import javax.swing.SwingWorker;

public class Worker extends SwingWorker<Integer, Object> {

	private GUIMAP gui;

	public Worker(GUIMAP gui) {
		this.gui = gui;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		for(double i = 0; i < 100; i++){
			System.out.println("lala");
			gui.setAvance(i/100.0);
			Thread.sleep(100);
		}
		return null;
	}

}
