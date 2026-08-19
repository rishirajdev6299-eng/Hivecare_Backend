package com.hivecare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hivecare.model.Booking;
import com.hivecare.model.User;
import com.hivecare.repository.BookingRepository;
import com.hivecare.repository.UserRepository;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkerController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    public WorkerController(
            UserRepository userRepository,
            BookingRepository bookingRepository) {

        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }


    // WORKER PROFILE
    @GetMapping("/{workerId}")
    public ResponseEntity<User> getWorker(
            @PathVariable Long workerId) {

        return userRepository.findById(workerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // BOOKING REQUESTS MATCHING WORKER SERVICE
    @GetMapping("/{workerId}/requests")
    public ResponseEntity<List<Booking>> getRequests(
            @PathVariable Long workerId) {

        User worker =
                userRepository.findById(workerId)
                        .orElse(null);

        if (worker == null ||
            !"WORKER".equalsIgnoreCase(worker.getRole())) {

            return ResponseEntity.notFound().build();
        }

        List<Booking> requests =
                bookingRepository.findByServiceAndStatus(
                        worker.getWorkerService(),
                        "PENDING"
                );

        return ResponseEntity.ok(requests);
    }


    // ACCEPT BOOKING
    @PutMapping("/{workerId}/bookings/{bookingId}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable Long workerId,
            @PathVariable Long bookingId) {

        User worker =
                userRepository.findById(workerId)
                        .orElse(null);

        if (worker == null ||
            !"WORKER".equalsIgnoreCase(worker.getRole())) {

            return ResponseEntity.badRequest()
                    .body("Invalid worker");
        }


        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElse(null);

        if (booking == null) {

            return ResponseEntity.notFound().build();
        }


        // Already accepted
        if (!"PENDING".equalsIgnoreCase(
                booking.getStatus())) {

            return ResponseEntity.badRequest()
                    .body("Booking already assigned");
        }


        // Make sure worker service matches
        if (!worker.getWorkerService()
                .equalsIgnoreCase(booking.getService())) {

            return ResponseEntity.badRequest()
                    .body("This booking is not for your service");
        }


        booking.setWorkerId(workerId);
        booking.setStatus("ACCEPTED");

        return ResponseEntity.ok(
                bookingRepository.save(booking)
        );
    }


    // REJECT BOOKING
    @PutMapping("/{workerId}/bookings/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(
            @PathVariable Long workerId,
            @PathVariable Long bookingId) {

        User worker =
                userRepository.findById(workerId)
                        .orElse(null);

        if (worker == null) {

            return ResponseEntity.notFound().build();
        }


        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElse(null);

        if (booking == null) {

            return ResponseEntity.notFound().build();
        }


        // We don't assign the worker on rejection.
        // Another matching worker can still accept.
        return ResponseEntity.ok(
                "Booking rejected"
        );
    }


    // MY ASSIGNED BOOKINGS
    @GetMapping("/{workerId}/bookings")
    public List<Booking> getMyBookings(
            @PathVariable Long workerId) {

        return bookingRepository
                .findByWorkerId(workerId);
    }
 // =====================================================
 // COMPLETE SERVICE
 // =====================================================

 @PutMapping("/{workerId}/bookings/{bookingId}/complete")
 public ResponseEntity<?> completeService(
         @PathVariable Long workerId,
         @PathVariable Long bookingId) {

     User worker =
             userRepository.findById(workerId)
                     .orElse(null);

     if (worker == null ||
         !"WORKER".equalsIgnoreCase(worker.getRole())) {

         return ResponseEntity.badRequest()
                 .body("Invalid worker");
     }


     Booking booking =
             bookingRepository.findById(bookingId)
                     .orElse(null);

     if (booking == null) {

         return ResponseEntity.notFound().build();
     }


     /*
      * Make sure this worker is actually assigned
      * to this booking.
      */
     if (booking.getWorkerId() == null ||
         !booking.getWorkerId().equals(workerId)) {

         return ResponseEntity.badRequest()
                 .body(
                     "This booking is not assigned to this worker"
                 );
     }


     /*
      * Only ACCEPTED bookings can be completed.
      */
     if (!"ACCEPTED".equalsIgnoreCase(
             booking.getStatus())) {

         return ResponseEntity.badRequest()
                 .body(
                     "Only accepted bookings can be completed"
                 );
     }


     /*
      * Mark service as completed.
      */
     booking.setStatus("COMPLETED");


     /*
      * Save completion time.
      */
     booking.setCompletedAt(
             java.time.LocalDateTime
                     .now()
                     .toString()
     );


     return ResponseEntity.ok(
             bookingRepository.save(booking)
     );
 }
}