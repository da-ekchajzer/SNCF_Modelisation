package fr.pantheonsorbonne.ufr27.miage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.IncidentJAXB;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ArretDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.IncidentDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.ItineraireDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrainDAO;
import fr.pantheonsorbonne.ufr27.miage.n_dao.TrajetDAO;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident.CodeEtatIncident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Train;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.TrainAvecResa;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ArretRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.IncidentRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.ItineraireRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrainRepository;
import fr.pantheonsorbonne.ufr27.miage.n_repository.TrajetRepository;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceIncident;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur;
import fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajExecuteur;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceIncidentImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajDecideurImp;
import fr.pantheonsorbonne.ufr27.miage.n_service.impl.ServiceMajExecuteurImp;
import fr.pantheonsorbonne.ufr27.miage.tests.utils.TestPersistenceProducer;

@TestInstance(Lifecycle.PER_CLASS)
@EnableWeld
@TestMethodOrder(OrderAnnotation.class)
public class TestServiceIncident {

	@WeldSetup
	private WeldInitiator weld = WeldInitiator.from(ServiceIncident.class, ServiceIncidentImp.class, 
			TrainRepository.class, TrainDAO.class, IncidentRepository.class, IncidentDAO.class, 
			ItineraireRepository.class, ItineraireDAO.class, TrajetRepository.class, TrajetDAO.class, 
			ArretRepository.class, ArretDAO.class, ServiceMajDecideur.class, ServiceMajDecideurImp.class, 
			ServiceMajExecuteur.class, ServiceMajExecuteurImp.class, TestPersistenceProducer.class)
			.activate(RequestScoped.class).build();

	@Inject
	EntityManager em;
	@Inject
	ServiceIncident serviceIncident;
	@Inject
	TrainRepository trainRepository;
	@Inject
	IncidentRepository incidentRepository;


	@BeforeAll
	void initVarInDB() {
		Train train1 = new TrainAvecResa(1, "Marque");
		Itineraire it1 = new Itineraire(train1);
		it1.setEtat(CodeEtatItinieraire.EN_COURS.getCode());
		em.getTransaction().begin();
		em.persist(train1);
		em.persist(it1);
		em.getTransaction().commit();
	}
	
	@Test
	@Order(1)
	void testCreerIncident() throws DatatypeConfigurationException {
		Train t = this.trainRepository.getTrainById(1);
		IncidentJAXB incidentJAXB = new IncidentJAXB();
		
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		incidentJAXB.setHeureIncident(date2);
		incidentJAXB.setEtatIncident(CodeEtatIncident.EN_COURS.getCode());
		
		assertEquals(0, this.incidentRepository.getNbIncidents());
		this.serviceIncident.creerIncident(t.getId(), incidentJAXB);
		assertEquals(1, this.incidentRepository.getNbIncidents());		
		
		// TODO : tester méthode ServiceMajDecideur.decideMajRetardTrainLorsCreationIncident
	}
	
	@Test
	@Order(2)
	void testMajEtatIncident() {
		Train t = this.trainRepository.getTrainById(1);
		assertEquals(true,  this.serviceIncident.majEtatIncident(t.getId(), CodeEtatIncident.RESOLU.getCode()));
		
		// TODO : tester méthode ServiceMajDecideur.decideMajTrainEnCours
		// TODO : tester méthode ServiceMajDecideur.decideMajTrainFin
		
	}

}