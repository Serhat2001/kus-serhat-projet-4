package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, new Executable() {
    // Changement de l'expression lambda par une expression avec une classe anonyme
            @Override
            public void execute() throws Throwable {
                fareCalculatorService.calculateFare(ticket);
            }
        });
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, new Executable() {
    // Changement de l'expression lambda par une expression avec une classe anonyme
            @Override
            public void execute() throws Throwable {
                fareCalculatorService.calculateFare(ticket);
            }
        });
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime(){
        Date inTime = new Date();
        ticket.setInTime(inTime);//Définit l'heure d'entrée à l'heure actuelle
        Date ouTime = new Date(inTime.getTime()+(29 * 60 * 1000));//Heure de sortie de la voiture
        ticket.setOutTime(ouTime);
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR, false);

        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0,ticket.getPrice());
    }

     @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime(){
        Date inTime = new Date();
        ticket.setInTime(inTime);//Définit l'heure d'entrée à l'heure actuelle
        Date ouTime = new Date(inTime.getTime()+(29 * 60 * 1000));//Heure de sortie de la voiture
        ticket.setOutTime(ouTime);
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.BIKE, false);

        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0,ticket.getPrice());
    }
    @Test
    public void calculateFareCarWithDiscount(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis()-(3 * 60 * 60 * 1000));
        ticket.setInTime(inTime);
        Date outTime = new Date();
        ticket.setOutTime(outTime);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,true);//La voiture a droit à une réduction

        double expectedPrice = 3 * Fare.CAR_RATE_PER_HOUR * 0.95;//Prix attendu pour 3h de stationnement en appliquant les 5%
        assertEquals(expectedPrice, ticket.getPrice(), 0);
    }
    @Test
    public void calculateFareBikeWithDiscount(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis()-(3 * 60 * 60 * 1000));
        ticket.setInTime(inTime);
        Date outTime = new Date();
        ticket.setOutTime(outTime);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,true);

        double expectedPrice = 3 * Fare.BIKE_RATE_PER_HOUR * 0.95;
        assertEquals(expectedPrice, ticket.getPrice(), 0);
}
}