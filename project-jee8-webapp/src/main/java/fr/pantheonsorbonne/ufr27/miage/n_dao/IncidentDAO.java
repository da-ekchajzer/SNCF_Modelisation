package fr.pantheonsorbonne.ufr27.miage.n_dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.pantheonsorbonne.ufr27.miage.n_jpa.Incident;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire;
import fr.pantheonsorbonne.ufr27.miage.n_jpa.Itineraire.CodeEtatItinieraire;

@ManagedBean
public class IncidentDAO {

	@Inject
	EntityManager em;

	@Inject
	ItineraireDAO itineraireDAO;

	public List<Incident> getAllIncidents() {
		TypedQuery<Incident> query = em.createNamedQuery("Incident.findAllIncidents", Incident.class);
		return query.getResultList();
	}

	public int getNbIncidents() {
		TypedQuery<Long> query = em.createNamedQuery("Incident.getNbIncidents", Long.class);
		return query.getSingleResult().intValue();
	}

	public boolean creerIncident(int idTrain, Incident incident) {
		// On récupère le nb d'Incidents en BD avant l'insertion
		int nbIncidentsAvantAjout = this.getNbIncidents();

		// Persistence de l'incident
		em.getTransaction().begin();
		em.persist(incident);
		em.getTransaction().commit();

		// On récupère le nb d'Incidents en BD après l'insertion
		int nbIncidentsApresAjout = this.getNbIncidents();

		// On vérifie que l'insertion a été effectuée
		if (nbIncidentsApresAjout != nbIncidentsAvantAjout + 1) {
			// LOG.error
			return false;
		}

		// Récupération de l'itinéraire EN COURS (=1) de TRAIN_ID idTrain
		Itineraire itineraire = em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", CodeEtatItinieraire.EN_COURS).getSingleResult();

		// Ajout de l'INCIDENT_ID dans l'itinéraire associé au train
		itineraireDAO.ajouterIncidentItineraire(itineraire.getId(), incident.getId());

		return true;

	}

	public void updateEtatIncident(int idTrain, int etat) {
		// Récupération de l'itinéraire EN COURS (=1) de TRAIN_ID idTrain
		Itineraire itineraire = em.createNamedQuery("Itineraire.getItineraireByTrainEtEtat", Itineraire.class)
				.setParameter("idTrain", idTrain).setParameter("etat", CodeEtatItinieraire.EN_COURS).getSingleResult();

		// Récupération de l'incident associé à l'itinéraire itinéraire
		Incident incident = em.createNamedQuery("IncidentDAO.getIncidentById", Incident.class)
				.setParameter("id", itineraire.getIncident().getId()).getSingleResult();

		// MàJ de l'état de l'incident associé au train
		em.getTransaction().begin();
		incident.setEtat(etat);
		em.getTransaction().commit();
	}
}
