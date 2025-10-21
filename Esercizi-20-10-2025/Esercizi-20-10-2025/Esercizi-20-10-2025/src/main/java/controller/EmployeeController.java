package controller;

import entities.Employee;
import exceptions.BadRequestException;
import exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.EmployeeService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final Path uploadDir;

    public EmployeeController(EmployeeService employeeService, @Value("${app.upload.dir}") String uploadDirStr) {
        this.employeeService = employeeService;
        this.uploadDir = Paths.get(uploadDirStr).toAbsolutePath();
        try { Files.createDirectories(this.uploadDir); } catch (IOException e) { throw new RuntimeException(e); }
    }

    @GetMapping
    public List<Employee> getAll() { return employeeService.findAll(); }

    @GetMapping("/{id}")
    public Employee getOne(@PathVariable Long id) { return employeeService.findById(id); }

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee emp){
        Employee created = employeeService.create(emp);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @Valid @RequestBody Employee emp){
        emp.setId(id);
        return employeeService.update(emp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Employee e = employeeService.findEntityById(id);
        if (file.isEmpty()) throw new BadRequestException("File vuoto.");
        String filename = "emp-" + id + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        e.setAvatarPath(target.toString());
        employeeService.save(e);
        return ResponseEntity.ok(Map.of("avatarPath", e.getAvatarPath()));
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<Resource> getAvatar(@PathVariable Long id) throws MalformedURLException {
        Employee e = employeeService.findEntityById(id);
        if (e.getAvatarPath() == null) throw new ResourceNotFoundException("Avatar non trovato per employee", id);
        Path p = Paths.get(e.getAvatarPath());
        Resource res = new UrlResource(p.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(res);
    }
}