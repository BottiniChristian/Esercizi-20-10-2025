package controller;

import entities.Trip;
import services.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) { this.tripService = tripService; }

    @GetMapping
    public List<Trip> getAll() { return tripService.findAll(); }

    @GetMapping("/{id}")
    public Trip getOne(@PathVariable Long id) { return tripService.findById(id); }

    @PostMapping
    public ResponseEntity<Trip> create(@Valid @RequestBody Trip trip){
        Trip created = tripService.create(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Trip update(@PathVariable Long id, @Valid @RequestBody Trip trip){
        trip.setId(id);
        return tripService.update(trip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public Trip changeStatus(@PathVariable Long id, @RequestParam String status){
        return tripService.changeStatus(id, status);
    }
}