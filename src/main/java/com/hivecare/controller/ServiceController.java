package com.hivecare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hivecare.model.Service;
import com.hivecare.repository.ServiceRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;


    public ServiceController(
            ServiceRepository serviceRepository) {

        this.serviceRepository = serviceRepository;
    }


    @GetMapping
    public List<Service> getAllServices() {

        return serviceRepository.findAll();
    }


    @PostMapping
    public Service createService(
            @RequestBody Service service) {

        return serviceRepository.save(service);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(
            @PathVariable Long id,
            @RequestBody Service details) {

        return serviceRepository.findById(id)
                .map(service -> {

                    service.setName(details.getName());
                    service.setDescription(
                            details.getDescription()
                    );

                    return ResponseEntity.ok(
                            serviceRepository.save(service)
                    );
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(
            @PathVariable Long id) {

        if (!serviceRepository.existsById(id)) {

            return ResponseEntity.notFound().build();
        }

        serviceRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}