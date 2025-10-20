package services;

import entities.Employee;
import exceptions.ResourceNotFoundException;
import exceptions.BadRequestException;
import repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository empRepo;

    public EmployeeService(EmployeeRepository empRepo) {
        this.empRepo = empRepo;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return empRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return empRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    @Transactional(readOnly = true)
    public Employee findEntityById(Long id) {
        return findById(id);
    }

    @Transactional
    public Employee create(Employee emp) {
        if (emp.getUsername() != null && empRepo.existsByUsername(emp.getUsername())) {
            throw new BadRequestException("Username già in uso");
        }
        if (emp.getEmail() != null && empRepo.existsByEmail(emp.getEmail())) {
            throw new BadRequestException("Email già in uso");
        }
        return empRepo.save(emp);
    }

    @Transactional
    public Employee update(Employee emp) {
        Long id = emp.getId();
        if (id == null || !empRepo.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
        return empRepo.save(emp);
    }

    @Transactional
    public void delete(Long id) {
        if (!empRepo.existsById(id)) throw new ResourceNotFoundException("Employee", id);
        empRepo.deleteById(id);
    }

    @Transactional
    public Employee save(Employee emp) {
        return empRepo.save(emp);
    }
}