package com.hivecare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hivecare.model.Booking;
import com.hivecare.repository.BookingRepository;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/bookings")
public class BookingController {
	
	private final BookingRepository bookingRepository;

    public BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingRepository.save(booking);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return bookingRepository.findById(id)
            .map(existingBooking -> {
                existingBooking.setService(bookingDetails.getService());
                existingBooking.setName(bookingDetails.getName());
                existingBooking.setAddress(bookingDetails.getAddress());
                existingBooking.setDate(bookingDetails.getDate());
                Booking updatedBooking = bookingRepository.save(existingBooking);
                return ResponseEntity.ok(updatedBooking);
            }).orElse(ResponseEntity.notFound().build());
    }


    // Delete Booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        return bookingRepository.findById(id)
            .map(booking -> {
                bookingRepository.delete(booking);
                return ResponseEntity.noContent().build();
            }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUser(
            @PathVariable Long userId) {

        return bookingRepository
                .findByUserId(userId);
    }


}
