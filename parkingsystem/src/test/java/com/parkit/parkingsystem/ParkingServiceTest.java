package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    private Ticket ticket;

    @BeforeEach
    private void setUpPerTest() {
        try {
            

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            
            

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() throws Exception {
        /*Mock de l'appel à la méthode "getNbTicket" de l'objet "ticketDAO" et on retourne 1 peu importe la chaîne
        passée en paramètre.*/
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        //Arrange
        when(ticketDAO.getNbTicket(anyString())).thenReturn(1);
        //Simule le processus de sortie du véhicule
        //Act
        parkingService.processExitingVehicle();

        /*Vérifie que la méthode "updateParking" du parkingSpotDAO a été appeléé exactement 1 fois
        un objet "parkingSpot" quelconque en paramètre.*/
        //Assert
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(ticketDAO, times(1)).getTicket(anyString());
        verify(ticketDAO, times(1)).getNbTicket(eq("ABCDEF"));
    }

    @Test
    public void testProcessIncomingVehicle() throws Exception {
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //Test du cas où le ticket est déhà existant
        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        
        verify(ticketDAO, times(0)).saveTicket(any(Ticket.class));
        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();

        //Test du cas d'un nouveau ticket
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }
    
    @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        parkingService.processExitingVehicle();
    }

    @Test
    public void testGetNextParkingNumberIfAvailable(){
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(parkingSpot.getId(), 1);
        assertTrue(parkingSpot.isAvailable());
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        assertNull(parkingService.getNextParkingNumberIfAvailable());
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {

        when(inputReaderUtil.readSelection()).thenReturn(3);

        assertNull(parkingService.getNextParkingNumberIfAvailable()); 
    }
}
