package com.hivecare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hivecare.model.Booking;
import com.hivecare.repository.BookingRepository;
import com.hivecare.repository.UserRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/bookings")
public class BookingController {

	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;

	public BookingController(
	        BookingRepository bookingRepository,
	        UserRepository userRepository) {

	    this.bookingRepository = bookingRepository;
	    this.userRepository = userRepository;
	}


    // =====================================================
    // CREATE BOOKING
    // =====================================================

    @PostMapping
    public Booking createBooking(
            @RequestBody Booking booking) {

        /*
         * Every new booking starts as PENDING.
         */
        booking.setStatus("PENDING");

        /*
         * Worker is not assigned when customer creates
         * the booking.
         */
        booking.setWorkerId(null);

        /*
         * Every new booking starts with unpaid status.
         */
        booking.setPaymentStatus("PENDING");

        /*
         * Razorpay IDs are empty until payment happens.
         */
        booking.setRazorpayOrderId(null);
        booking.setRazorpayPaymentId(null);
        booking.setRazorpaySignature(null);
        booking.setPaidAt(null);

        /*
         * Service completion happens later.
         */
        booking.setCompletedAt(null);

        return bookingRepository.save(booking);
    }


    // =====================================================
    // GET ALL BOOKINGS
    // =====================================================

    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    // =====================================================
    // GET BOOKINGS OF USER
    // =====================================================

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(
            @PathVariable Long userId) {

        List<Booking> bookings =
                bookingRepository.findByUserId(userId);

        for (Booking booking : bookings) {

            if (booking.getWorkerId() != null) {

                userRepository
                        .findById(booking.getWorkerId())
                        .ifPresent(worker -> {

                            if ("WORKER".equalsIgnoreCase(
                                    worker.getRole())) {

                                booking.setWorkerName(
                                        worker.getName()
                                );

                            }

                        });
            }
        }

        return bookings;
    }


    // =====================================================
    // UPDATE BOOKING
    // =====================================================

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

                    /*
                     * Allow changing payment timing when
                     * editing a booking.
                     */
                    if (bookingDetails.getPaymentTiming() != null) {

                        existingBooking.setPaymentTiming(
                                bookingDetails.getPaymentTiming()
                        );
                    }

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


    // =====================================================
    // DELETE BOOKING
    // =====================================================

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