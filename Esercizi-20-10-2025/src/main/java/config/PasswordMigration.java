package config;

import entities.Employee;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import repositories.EmployeeRepository;

@Component
public class PasswordMigration implements CommandLineRunner {
    private final EmployeeRepository repo;
    private final PasswordEncoder encoder;

    public PasswordMigration(EmployeeRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        repo.findAll().forEach(e -> {
            String pw = e.getPassword();
            if (pw != null && !pw.startsWith("$2a$") && !pw.startsWith("$2b$")) {
                e.setPassword(encoder.encode(pw));
                repo.save(e);
                System.out.println("Password aggiornata per: " + e.getEmail());
            }
        });
    }
}
