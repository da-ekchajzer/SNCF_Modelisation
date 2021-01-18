package fr.pantheonsorbonne.ufr27.miage.test.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.GareDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageDAO;
import fr.pantheonsorbonne.ufr27.miage.dao.VoyageurDAO;
import fr.pantheonsorbonne.ufr27.miage.jpa.Arret;
import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.TrainSansResa;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyage;
import fr.pantheonsorbonne.ufr27.miage.jpa.Voyageur;
import fr.pantheonsorbonne.ufr27.miage.repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageRepository;
import fr.pantheonsorbonne.ufr27.miage.repository.VoyageurRepository;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestDatabase;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
public class TestVoyageurRepository {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(VoyageurRepository.class, VoyageRepository.class,
			ItineraireRepository.class, ArretRepository.class, TrajetRepository.class, TrainRepository.class,
			VoyageurDAO.class, VoyageDAO.class, TrajetDAO.class, ItineraireDAO.class, IncidentDAO.class, ArretDAO.class,
			TrainDAO.class, GareDAO.class, TestPersistenceProducer.class, TestDatabase.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	VoyageurRepository voyageurRepository;
	@Inject
	TrainRepository trainRepository;
	@Inject
	TestDatabase testDatabase;

	@BeforeAll
	public void setup() {
		em.getTransaction().begin();

		// --------------------------------- Gares
		String[] nomGares = { "Paris - Gare de Lyon", "Avignon-Centre", "Aix en Provence", "Marseille - St Charles",
				"Dijon-Ville", "Lyon - Pardieu", "Narbonne", "Sete", "Perpignan", "Paris - Montparnasse", "Tours",
				"Bordeaux - Saint-Jean", "Pessac", "Arcachon-Centre", "Nantes" };

		Map<String, Gare> gares = new HashMap<>();
		for (String nomGare : nomGares) {
			Gare g = new Gare(nomGare);
			gares.put(nomGare, g);
			em.persist(g);
		}

		// --------------------------------- Remplissage de la table Train
		Train train1 = new TrainAvecResa("TGV");
		Train train2 = new TrainSansResa("TER");
		Train train3 = new TrainAvecResa("OUIGO");
		Train train4 = new TrainAvecResa("OUIGO");
		Train train5 = new TrainSansResa("TER");
		Train train6 = new TrainAvecResa("TGV");
		Train train7 = new TrainSansResa("TER");
		Train train8 = new TrainAvecResa("TGV");
		Train train9 = new TrainAvecResa("TGV");

		Train[] trains = { train1, train2, train3, train4, train5, train6, train7, train8, train9 };
		for (Train t : trains)
			em.persist(t);

		// --------------------------------- Arrêts

		Arret arret1 = new Arret(gares.get("Paris - Gare de Lyon"), null, LocalDateTime.now());
		Arret arret2 = new Arret(gares.get("Avignon-Centre"), LocalDateTime.now().plusMinutes(1),
				LocalDateTime.now().plusMinutes(1).plusSeconds(30));
		Arret arret3 = new Arret(gares.get("Aix en Provence"), LocalDateTime.now().plusMinutes(3),
				LocalDateTime.now().plusMinutes(3).plusMinutes(1));
		Arret arret4 = new Arret(gares.get("Marseille - St Charles"), LocalDateTime.now().plusMinutes(5),
				LocalDateTime.now().plusMinutes(7));
		Arret arret5 = new Arret(gares.get("Lyon - Pardieu"), LocalDateTime.now().plusMinutes(10), null);

		Arret[] arrets = { arret1, arret2, arret3, arret4, arret5 };

		for (Arret a : arrets) {
			em.persist(a);
		}

		// --------------------------------- Remplissage de la table Itinéraire

		Itineraire itineraire1 = new Itineraire(train1);
		itineraire1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		itineraire1.addArret(arret1);
		itineraire1.addArret(arret2);
		itineraire1.addArret(arret4);
		em.persist(itineraire1);

		Itineraire itineraire2 = new Itineraire(train2);
		itineraire2.setEtat(CodeEtatItinieraire.EN_ATTENTE.getCode());
		itineraire2.addArret(arret4);
		itineraire2.addArret(arret5);
		em.persist(itineraire2);

		// --------------------------------- Remplissage de la table Trajet

		Trajet trajet1 = new Trajet(gares.get("Paris - Gare de Lyon"), gares.get("Avignon-Centre"), itineraire1, 0);
		Trajet trajet2 = new Trajet(gares.get("Avignon-Centre"), gares.get("Aix en Provence"), itineraire1, 1);
		Trajet trajet3 = new Trajet(gares.get("Aix en Provence"), gares.get("Marseille - St Charles"), itineraire1, 2);
		Trajet trajet4 = new Trajet(gares.get("Marseille - St Charles"), gares.get("Lyon - Pardieu"), itineraire2, 3);

		Trajet[] trajets = { trajet1, trajet2, trajet3, trajet4 };

		for (Trajet t : trajets) {
			em.persist(t);
		}

		// --------------------------------- Remplissage de la table Voyage
		List<Trajet> voyageTrajet1 = new LinkedList<Trajet>();
		voyageTrajet1.add(trajet1);
		voyageTrajet1.add(trajet2);
		voyageTrajet1.add(trajet3);
		voyageTrajet1.add(trajet4);
		Voyage voyage1 = new Voyage(voyageTrajet1);
		em.persist(voyage1);

		List<Trajet> voyageTrajet2 = new LinkedList<Trajet>();
		voyageTrajet2.add(trajet1);
		voyageTrajet2.add(trajet2);
		voyageTrajet2.add(trajet3);
		Voyage voyage2 = new Voyage(voyageTrajet2);
		em.persist(voyage2);

		// --------------------------------- Remplissage de la table Voyageur

		String[] prenomsVoyageurs = { "Mariah", "Marc", "Sophia", "Alyssia", "Antoine", "Doudouh", "Lucie", "Lucas",
				"David", "Ben", "Maria", "Lucas", "Sophie", "Jean-Mi", "Jean", "Abdel", "Tatiana", "Charlotte",
				"Charlotte", "Abdel", "Ben", "Ben", "Mathieu", "Louis", "Jean-Luc", "Luc", "Jean", "Sophia", "Marc",
				"Manuel", "Abdel" };

		String[] nomsVoyageurs = { "Dupont", "Dupont", "Durand", "Martin", "Bernard", "Thomas", "Petit", "Grand",
				"Robert", "Richard", "Richard", "Dubois", "Petit", "Petit", "Moreau", "Laurent", "Simon", "Michel",
				"Lefevre", "Legrand", "Lefebvre", "Leroy", "Roux", "Leroi", "Morel", "Fournier", "Gerard", "Poirier",
				"Pommier", "Rossignol", "Benamara" };

		for (int i = 0; i < prenomsVoyageurs.length; i++) {
			Voyageur v = new Voyageur(prenomsVoyageurs[i], nomsVoyageurs[i]);
			if (i < 5) {
				if (i == 0) {
					v.setVoyageActuel(voyage1);
				}
				voyage1.addVoyageur(v);
			} else if (i == 15) {
				v.setVoyageActuel(voyage2);
				voyage2.addVoyageur(v);
			}
			em.persist(v);
		}
		em.getTransaction().commit();
	}

	@Test
	void testIsVoyageurCorrespondance() {
		Voyageur voyageurEnCorrespondance = voyageurRepository.getAllVoyageurs().get(0);
		Voyageur voyageurSansVoyage = voyageurRepository.getAllVoyageurs().get(15);
		Voyage voyage = voyageurEnCorrespondance.getVoyageActuel();
		assertNotNull(voyage);
		Itineraire it1 = voyage.getTrajets().get(0).getItineraire();
		Itineraire it2 = voyage.getTrajets().get(3).getItineraire();
		assertFalse(it1.equals(it2));
		assertTrue(voyageurRepository.isVoyageurCorrespondance(voyageurEnCorrespondance, it1, it2));
		assertFalse(voyageurRepository.isVoyageurCorrespondance(voyageurSansVoyage, it1, it2));
	}

	@AfterAll
	void nettoyageDonnees() {
		testDatabase.clear();
	}

}