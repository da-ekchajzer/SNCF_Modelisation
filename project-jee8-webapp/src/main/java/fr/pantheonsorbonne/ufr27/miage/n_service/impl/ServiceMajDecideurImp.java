package fr.pantheonsorbonne.ufr27.miage.n_service.impl;

import java.time.LocalTime;

public class ServiceMajDecideurImp implements fr.pantheonsorbonne.ufr27.miage.n_service.ServiceMajDecideur {

	@Override
	public void decideMajTrainCreation(int idTrain, LocalTime estimationTempsRetard) {
		// TODO Auto-generated method stub
		//regarde le code etat, en deduis la duree de l'incident --> recup methode estimationTempsRetard
		//Verifie avec les voyageurs si on ne doit pas supprimer, ajouter ou retarder les arrets
		
	}


	@Override
	public void decideMajTrainEnCours(int idTrain, LocalTime estimationTempsRetardEnCours) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decideMajTrainFin(int idTrain) {
		// TODO Auto-generated method stub
		
	}
	
}
