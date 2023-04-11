package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
        }
        // Initialisation du temps en millisecondes
        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();

        // Convertir les millisecondes en heures :
        double durationInHours = (outTimeMillis - inTimeMillis) / (1000.0 * 60.0 * 60.0);
        if (durationInHours <0.5){
          ticket.setPrice(0);
        } else {
          //Calcul du taux horaire en fonction du type de véhicule
          double fareRate = 0;
          switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                fareRate = Fare.CAR_RATE_PER_HOUR;
                break;
            case BIKE:
                fareRate = Fare.BIKE_RATE_PER_HOUR;
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
        //Calcul du prix total du ticket en fonction du temps et du taux horaire
        double price = durationInHours * fareRate;
        //Application de la réduction des 5% si le paramètre discount est vrai
        if(discount){
          price *= 0.95;
        }
        //Mise à jour du prix avec le prix calculé juste avant
        ticket.setPrice(price);
        }
}
//Méthode calculateFare avec le paramètre discount à false par défaut
public void calculateFare(Ticket ticket){
  calculateFare(ticket, false);
}
}