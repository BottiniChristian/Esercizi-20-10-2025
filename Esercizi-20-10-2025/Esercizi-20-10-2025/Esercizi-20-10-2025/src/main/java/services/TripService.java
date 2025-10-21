package services;

import entities.Trip;
import exceptions.ResourceNotFoundException;
import repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TripService {
    private final TripRepository tripRepo;

    public TripService(TripRepository tripRepo) {
        this.tripRepo = tripRepo;
    }

    @Transactional(readOnly = true)
    public List<Trip> findAll() {
        return tripRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Trip findById(Long id) {
        return tripRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
    }

    @Transactional
    public Trip create(Trip trip) {
        return tripRepo.save(trip);
    }

    @Transactional
    public Trip update(Trip trip) {
        Long id = trip.getId();
        if (id == null || !tripRepo.existsById(id)) {
            throw new ResourceNotFoundException("Trip", id);
        }
        return tripRepo.save(trip);
    }

    @Transactional
    public void delete(Long id) {
        if (!tripRepo.existsById(id)) {
            throw new ResourceNotFoundException("Trip", id);
        }
        tripRepo.deleteById(id);
    }

    @Transactional
    public Trip changeStatus(Long id, String newStatus) {
        Trip t = tripRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        t.setStatus(newStatus);
        return tripRepo.save(t);
    }
}
