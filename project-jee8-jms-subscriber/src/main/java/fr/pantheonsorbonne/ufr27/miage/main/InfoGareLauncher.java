package fr.pantheonsorbonne.ufr27.miage.main;

import javax.jms.JMSException;

public class InfoGareLauncher {

	public static void main(String[] args) throws InterruptedException, JMSException {

		String[] gare = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles" };
		int infogareId = 0;

		Thread.sleep(5000);

		while (infogareId < gare.length) {
			InfoGare infoGare = new InfoGare(gare[infogareId]);
			Thread thread = new Thread(infoGare);
			thread.start();
			infogareId++;
		}
	}
}
