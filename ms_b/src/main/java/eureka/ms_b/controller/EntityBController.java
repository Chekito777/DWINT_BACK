package eureka.ms_b.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eureka.ms_b.dto.EntityBDto;
import eureka.ms_b.dto.EntityBDtoList;
import eureka.ms_b.dto.EntityBEntityADto;
import eureka.ms_b.service.EntityBService;

@RestController
@RequestMapping("/api/entity-b")
public class EntityBController {

    @Autowired
    private EntityBService service;

    @PostMapping
    public ResponseEntity<EntityBDto> create(@RequestBody EntityBDto dto) {
        EntityBDto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/entity-b/" + created.getId())).body(created);

    }

    @GetMapping
    public ResponseEntity<List<EntityBDtoList>> getAll() {
        List<EntityBDtoList> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<EntityBDto>> getAllByIds(@RequestBody List<Integer> ids) {
        List<EntityBDto> list = service.findAllByIDs(ids);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityBDto> getById(@PathVariable int id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityBDto> update(@PathVariable int id, @RequestBody EntityBDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean deleted = service.delete(id);
        if (deleted)
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{entityBId}/add-entity-a")
    public ResponseEntity<EntityBDto> addEntityAToEntityB(@PathVariable int entityBId,
            @RequestBody EntityBEntityADto dto) {
        return service.addEntityAToEntityB(entityBId, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}