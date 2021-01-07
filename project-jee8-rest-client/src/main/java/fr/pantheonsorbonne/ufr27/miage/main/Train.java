package fr.pantheonsorbonne.ufr27.miage.main;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.datatype.XMLGregorianCalendar;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ArretJAXB;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.ItineraireJAXB;
import fr.pantheonsorbonne.ufr27.miage.pojos.ArretTrain;
import fr.pantheonsorbonne.ufr27.miage.pojos.IncidentTrain;
import fr.pantheonsorbonne.ufr27.miage.restClient.GatewayInfocentre;

public class Train implements Runnable {

	private int idTrain;
	private Random random;

	/**
	 * 0 - En attente; 1 - En cours; 2 - En incident
	 */
	private int etatTrain;

	// Itineraire
	List<ArretTrain> arrets;
	int curentIdArret;

	IncidentTrain incident;

	public Train(int idTrain) {
		random = new Random();
		this.idTrain = idTrain;
		this.etatTrain = 0;
		this.curentIdArret = 0;
	}

	@Override
	public void run() {
		while (etatTrain != -1) {
			actionTrain();
      
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

//		actionTrain();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		incident = new IncidentTrain(4);
//		GatewayInfocentre.sendIncident(incident.getXMLIncident(), idTrain);
	}

	private void actionTrain() {
		System.out.println("[" + idTrain + "] - etat train : " + etatTrain);
		switch (etatTrain) {

		case 0:
			if (updateItineraire(GatewayInfocentre.getItineraire(this.idTrain))) {
				curentIdArret = 0;
				etatTrain = 1;
				System.out.println("[" + idTrain + "] - arrets desservis : " + printArrets(arrets));
			}
			break;
		case 1:
			while (arrets.get(curentIdArret).getHeureDepart() != null
					&& !arrets.get(curentIdArret).getHeureDepart().isAfter(LocalDateTime.now())) {
				curentIdArret++;
			}
			GatewayInfocentre.sendCurrenArret(this.arrets.get(curentIdArret).getXMLArret(), this.idTrain);
			System.out.println("[" + idTrain + "] - arret actuel : " + this.arrets.get(curentIdArret).getNomGare());
			if (arrets.get(curentIdArret).getHeureDepart() == null) {
				etatTrain = 0;
			}
			updateItineraire(GatewayInfocentre.getItineraire(this.idTrain));

			// genererRandomIncident();
			break;

		case 2:
			if (solvedRandomIncident()) {
				incident.setEtatIncident(0);
				etatTrain = 1;
				GatewayInfocentre.updateIncident(incident.getXMLIncident().getEtatIncident(), this.idTrain);
			}
			GatewayInfocentre.updateIncident(incident.getXMLIncident().getEtatIncident(), this.idTrain);
			break;
		}
	}

	private void genererRandomIncident() {
		boolean isIncident = bernouilliGenerator(0.001);

		if (isIncident) {
			int typeIncident = random.nextInt(6);
			incident = new IncidentTrain(typeIncident);
			GatewayInfocentre.sendIncident(incident.getXMLIncident(), this.idTrain);
			etatTrain = 2;
		}
	}

	private boolean solvedRandomIncident() {
		boolean isFinIncident = bernouilliGenerator(0.01);
		return isFinIncident;
	}

	private boolean updateItineraire(ItineraireJAXB itineraireJAXB) {
		if (itineraireJAXB == null) {
			return false;
		}
		this.arrets = new LinkedList<ArretTrain>();
		List<ArretJAXB> arretsJAXB = itineraireJAXB.getArrets();

		for (int i = 0; i < arretsJAXB.size(); i++) {
			ArretJAXB arreti = arretsJAXB.get(i);
			this.arrets
					.add(new ArretTrain(arreti.getGare(), xmlGregorianCalendar2LocalDateTime(arreti.getHeureArrivee()),
							xmlGregorianCalendar2LocalDateTime(arreti.getHeureDepart())));
		}
		return true;
	}

	private boolean bernouilliGenerator(double p) {
		if (random.nextDouble() < p) {
			return true;
		}
		return false;
	}

	public String printArrets(List<ArretTrain> arrets) {
		String str = "";
		for (ArretTrain arretTrain : arrets) {
			str += arretTrain.getNomGare() + " / ";
		}
		return str.substring(0, str.length() - 3);
	}

	public static LocalDateTime xmlGregorianCalendar2LocalDateTime(XMLGregorianCalendar xgc) {
		if (xgc == null) {
			return null;
		}
		LocalDateTime ldt = LocalDateTime.of(xgc.getYear(), xgc.getMonth(), xgc.getDay(), xgc.getHour(),
				xgc.getMinute());
		return ldt;

	}

}
