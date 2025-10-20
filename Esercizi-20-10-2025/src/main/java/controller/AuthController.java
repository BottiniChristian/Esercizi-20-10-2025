package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import repositories.EmployeeRepository;
import security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final repositories.EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil,
                          EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //login riceve email e password e ritorna token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            String token = jwtUtil.generateToken(req.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenziali non valide"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        if (employeeRepository.existsByEmail(r.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email già in uso"));
        }
        entities.Employee e = new entities.Employee();
        e.setEmail(r.getEmail());
        e.setUsername(r.getUsername());
        e.setFirstName(r.getFirstName());
        e.setLastName(r.getLastName());
        e.setPassword(passwordEncoder.encode(r.getPassword()));
        e.setRole("ROLE_USER");
        employeeRepository.save(e);
        return ResponseEntity.status(201).body(Map.of("msg", "utente creato"));
    }
}

//DTOs interni
class AuthRequest { private String email; private String password; public String getEmail(){return email;} public String getPassword(){return password;} }
class RegisterRequest { private String email; private String password; private String username; private String firstName; private String lastName;
    public String getEmail(){return email;} public String getPassword(){return password;} public String getUsername(){return username;}
    public String getFirstName(){return firstName;} public String getLastName(){return lastName;}
}
