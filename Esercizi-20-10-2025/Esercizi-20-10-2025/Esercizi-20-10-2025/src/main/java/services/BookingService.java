package services;

import entities.Booking;
import entities.Employee;
import entities.Trip;
import exceptions.BadRequestException;
import exceptions.ResourceNotFoundException;
import repositories.BookingRepository;
import repositories.EmployeeRepository;
import repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;
    private final EmployeeRepository empRepo;
    private final TripRepository tripRepo;

    public BookingService(BookingRepository bookingRepo, EmployeeRepository empRepo, TripRepository tripRepo) {
        this.bookingRepo = bookingRepo;
        this.empRepo = empRepo;
        this.tripRepo = tripRepo;
    }

    @Transactional
    public Booking assignEmployeeToTrip(Long employeeId, Long tripId, String notes) {
        Employee e = empRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        Trip t = tripRepo.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", tripId));

        LocalDate tripDate = t.getDate();

        if (bookingRepo.existsByEmployeeIdAndTripDate(employeeId, tripDate)) {
            throw new BadRequestException("Dipendente già prenotato per la data: " + tripDate);
        }

        if (bookingRepo.existsByEmployeeIdAndTripId(employeeId, tripId)) {
            throw new BadRequestException("Prenotazione per questo viaggio già esistente.");
        }

        Booking b = new Booking();
        b.setEmployee(e);
        b.setTrip(t);
        b.setRequestDate(LocalDate.now());
        b.setNotes(notes);
        return bookingRepo.save(b);
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Booking findById(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepo.existsById(id)) {
            throw new ResourceNotFoundException("Booking", id);
        }
        bookingRepo.deleteById(id);
    }
}
