package controller;

import entities.Booking;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    @PostMapping("/assign")
    public ResponseEntity<Booking> assign(@RequestParam Long employeeId, @RequestParam Long tripId, @RequestParam(required=false) String notes) {
        Booking b = bookingService.assignEmployeeToTrip(employeeId, tripId, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(b);
    }

    @GetMapping
    public List<Booking> getAll() { return bookingService.findAll(); }

    @GetMapping("/{id}")
    public Booking getOne(@PathVariable Long id) { return bookingService.findById(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { bookingService.delete(id); return ResponseEntity.noContent().build(); }
}