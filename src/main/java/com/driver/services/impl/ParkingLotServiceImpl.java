package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLot.setSpotList(new ArrayList<Spot>());

        return parkingLotRepository1.save(parkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Spot spot = new Spot();

        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()){
            throw new RuntimeException("Invalid Parkinglot ID");
        }
        ParkingLot parkingLot = optionalParkingLot.get();

        if(numberOfWheels==2){
            spot.setSpotType(SpotType.TWO_WHEELER);
        } else if(numberOfWheels==4){
            spot.setSpotType(SpotType.FOUR_WHEELER);
        } else {
            spot.setSpotType(SpotType.OTHERS);
        }
        spot.setPricePerHour(pricePerHour);
        spot.setOccupied(false);
        spot.setReservationList(new ArrayList<>());
        spot.setParkingLot(parkingLot);

        parkingLot.getSpotList().add(spot);

        return spotRepository1.save(spot);
    }

    @Override
    public void deleteSpot(int spotId) {
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
//        Optional<Spot> optionalSpot = spotRepository1.findById(spotId);
//        if(!optionalSpot.isPresent()){
//            throw new RuntimeException("Invalid Spot ID");
//        }
//        Spot spot = optionalSpot.get();

        Optional<ParkingLot> optionalParkingLot = parkingLotRepository1.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()){
            throw new RuntimeException("Invalid Parkinglot ID");
        }
        ParkingLot parkingLot = optionalParkingLot.get();

        Spot spot = null;
        for(Spot spt: parkingLot.getSpotList()){
            if(spt.getId() == spotId){
                spot = spt;
                break;
            }
        }
        if(spot==null){
            throw new RuntimeException("Invalid Spot ID");
        }
        spot.setPricePerHour(pricePerHour);

        return spotRepository1.save(spot);
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
