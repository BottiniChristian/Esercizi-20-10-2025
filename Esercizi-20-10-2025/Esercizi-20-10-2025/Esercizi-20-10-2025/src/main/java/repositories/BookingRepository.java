package repositories;

import entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //verifica se esiste una prenotazione per employee in una certa data
    @Query("select case when count(b)>0 then true else false end from Booking b where b.employee.id = :employeeId and b.trip.date = :date")
    boolean existsByEmployeeIdAndTripDate(Long employeeId, LocalDate date);

    boolean existsByEmployeeIdAndTripId(Long employeeId, Long tripId);
}