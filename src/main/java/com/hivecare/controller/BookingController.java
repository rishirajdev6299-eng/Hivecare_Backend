package com.hivecare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hivecare.model.Booking;
import com.hivecare.repository.BookingRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;


    public BookingController(
            BookingRepository bookingRepository) {

        this.bookingRepository = bookingRepository;
    }


    @PostMapping
    public Booking createBooking(
            @RequestBody Booking booking) {

        booking.setStatus("PENDING");
        booking.setWorkerId(null);

        return bookingRepository.save(booking);
    }


    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUser(
            @PathVariable Long userId) {

        return bookingRepository
                .findByUserId(userId);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking bookingDetails) {

        return bookingRepository.findById(id)
                .map(existingBooking -> {

                    existingBooking.setService(
                            bookingDetails.getService()
                    );

                    existingBooking.setName(
                            bookingDetails.getName()
                    );

                    existingBooking.setAddress(
                            bookingDetails.getAddress()
                    );

                    existingBooking.setDate(
                            bookingDetails.getDate()
                    );

                    existingBooking.setCourse(
                            bookingDetails.getCourse()
                    );

                    existingBooking.setPaymentMethod(
                            bookingDetails.getPaymentMethod()
                    );

                    existingBooking.setAmount(
                            bookingDetails.getAmount()
                    );

                    return ResponseEntity.ok(
                            bookingRepository.save(
                                    existingBooking
                            )
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(
            @PathVariable Long id) {

        return bookingRepository.findById(id)
                .map(booking -> {

                    bookingRepository.delete(booking);

                    return ResponseEntity.noContent()
                            .build();
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }
}