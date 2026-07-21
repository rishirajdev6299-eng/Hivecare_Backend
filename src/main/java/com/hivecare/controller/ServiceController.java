package com.hivecare.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.hivecare.model.Service;
import com.hivecare.repository.ServiceRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @PostMapping
    public Service createService(@RequestBody Service service) {
        return serviceRepository.save(service);
    }
}
