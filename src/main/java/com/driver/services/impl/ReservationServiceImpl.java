package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        ParkingLot parkingLot;
        try{
            user = userRepository3.findById(userId).get();
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        } catch (Exception e) {
            throw new Exception("Cannot make reservation");
        }

        Spot bestSpot = null;
        int minPrice = Integer.MAX_VALUE;

        for(Spot spot: parkingLot.getSpotList()){
            if(!spot.getOccupied() && isSpotBigEnough(numberOfWheels, spot)){
                if(spot.getPricePerHour()*timeInHours < minPrice){
                    bestSpot = spot;
                    minPrice = spot.getPricePerHour()*timeInHours;
                }
            }
        }
        if(bestSpot==null){
            throw new Exception("Cannot make reservation");
        }

        Reservation reservation = new Reservation();
        reservation.setSpot(bestSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);

        bestSpot.setOccupied(true);
        bestSpot.getReservationList().add(reservation);

        user.getReservationList().add(reservation);

        //saving user and spot instead of reservation for bidirectional save
        userRepository3.save(user);
        spotRepository3.save(bestSpot);

        return reservation;
    }

    private boolean isSpotBigEnough(Integer numberOfWheels, Spot spot){
        if(numberOfWheels == 4 && spot.getSpotType()==SpotType.TWO_WHEELER){
            return false;
        }
        else return numberOfWheels <= 4 || spot.getSpotType() == SpotType.OTHERS;
    }
}
