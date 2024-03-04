package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        PaymentMode paymentMode;
        try {
            paymentMode = PaymentMode.valueOf(mode.toUpperCase());
        }
        catch(RuntimeException e){
            throw new RuntimeException("Payment mode not detected");
        }

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        if(reservation.getPayment()!=null){
            Payment payment = reservation.getPayment();
            payment.setPaymentCompleted(true);
            return payment;
        }
        Spot spot = reservation.getSpot();
        if(reservation.getNumberOfHours()*spot.getPricePerHour() > amountSent){
            throw new RuntimeException("Insufficient Amount");
        }

        Payment payment = new Payment();
        payment.setPaymentMode(paymentMode);
        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);

        spot.setOccupied(false); //update

        reservation.setPayment(payment);

        reservationRepository2.save(reservation);
        return payment;
    }
}
