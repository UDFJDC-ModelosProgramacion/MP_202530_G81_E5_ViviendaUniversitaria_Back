package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.UniversidadDTO;
import co.edu.udistrital.mdp.back.services.UniversidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universidades")
@CrossOrigin(origins = "*")
public class UniversidadController {

    @Autowired
    private UniversidadService universidadService;

    @GetMapping
    public ResponseEntity<List<UniversidadDTO>> getAll() {
        List<UniversidadDTO> universidades = universidadService.getAll();
        return ResponseEntity.ok(universidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UniversidadDTO> getById(@PathVariable Long id) {
        UniversidadDTO universidad = universidadService.getById(id);
        return ResponseEntity.ok(universidad);
    }

    @PostMapping
    public ResponseEntity<UniversidadDTO> create(@RequestBody UniversidadDTO universidadDTO) {
        UniversidadDTO created = universidadService.create(universidadDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UniversidadDTO> update(@PathVariable Long id, @RequestBody UniversidadDTO universidadDTO) {
        UniversidadDTO updated = universidadService.update(id, universidadDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        universidadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
