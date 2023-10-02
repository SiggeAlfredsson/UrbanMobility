package com.siggebig.demo.repository;

import com.siggebig.demo.models.Trip;
import com.siggebig.demo.models.User;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TripRepositoryTests {

    @Autowired
    private TripRepository tripRepository;


    @Test
    void savesTripAndReturnsSavedDataAndGeneratedId() {
        // arrange
        Trip trip = Trip.builder()
                .departure("Uddevalla")
                .arrival("Vänersborg")
                .transportType("Bus")
                .price(100)
                .discount(10)
                .departureDate(LocalDate.of(2023, 10, 22))
                .departureTime(LocalTime.of(22, 0))
                .arrivalDate(LocalDate.of(2023, 10, 22))
                .arrivalTime(LocalTime.of(22, 30))
                //.user()
                .build();

        //act
        Trip savedTrip = tripRepository.save(trip);

        //assert
        assertNotNull(savedTrip);
        assertEquals("Uddevalla", savedTrip.getDeparture());
        assertEquals("Bus", savedTrip.getTransportType());
        Assertions.assertThat(savedTrip.getId()).isGreaterThan(0);
    }


    @Test
    void findByIdReturnsTripWithThatId() {
        //arrange
        Trip trip = Trip.builder()
                .departure("Uddevalla")
                .arrival("Vänersborg")
                .transportType("Bus")
                .price(100)
                .discount(10)
                .departureDate(LocalDate.of(2023, 10, 22))
                .departureTime(LocalTime.of(22, 0))
                .arrivalDate(LocalDate.of(2023, 10, 22))
                .arrivalTime(LocalTime.of(22, 30))
                //.user()
                .build();
        //act
        Trip savedTrip = tripRepository.save(trip);
        Optional<Trip> dbTrip = tripRepository.findById(savedTrip.getId());


        //assert
        assertNotNull(dbTrip);
        assertEquals(savedTrip.getId(),dbTrip.get().getId());
        assertEquals(savedTrip.getDeparture(),dbTrip.get().getDeparture());
        assertEquals(savedTrip.getTransportType(),dbTrip.get().getTransportType());

    }




    @Test
    void deleteTripByIdDeletesTrip() {
        //arrange
        Trip trip = Trip.builder()
                .id(1L)
                .departure("Uddevalla")
                .arrival("Vänersborg")
                .transportType("Bus")
                .price(100)
                .discount(10)
                .departureDate(LocalDate.of(2023, 10, 22))
                .departureTime(LocalTime.of(22, 0))
                .arrivalDate(LocalDate.of(2023, 10, 22))
                .arrivalTime(LocalTime.of(22, 30))
                //.user()
                .build();
        //act
        tripRepository.save(trip);


        //delete
        tripRepository.deleteById(trip.getId());

        //assert after delete
        Optional<Trip> tripReturn = tripRepository.findById(trip.getId());

        Assertions.assertThat(tripReturn).isEmpty();

    }





}
