package com.hivecare.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hivecare.dto.SubjectResponse;
import com.hivecare.model.Subject;
import com.hivecare.repository.SubjectRepository;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(
            SubjectRepository subjectRepository) {

        this.subjectRepository = subjectRepository;
    }

    // =====================================================
    // GET ALL SUBJECTS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {

        List<SubjectResponse> subjects =
                subjectRepository.findAll()
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(subjects);
    }


    // =====================================================
    // GET SUBJECTS / OPTIONS BY SERVICE
    // =====================================================

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<SubjectResponse>>
    getSubjectsByService(
            @PathVariable String serviceName) {

        List<SubjectResponse> subjects =
                subjectRepository
                        .findByService_NameIgnoreCase(serviceName)
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(subjects);
    }


    // =====================================================
    // OLD TUTOR ENDPOINT
    //
    // KEEPING THIS SO YOUR CURRENT MyBookings.js
    // DOES NOT BREAK.
    // =====================================================

    @GetMapping("/tutor")
    public ResponseEntity<List<SubjectResponse>>
    getTutorSubjects() {

        List<SubjectResponse> subjects =
                subjectRepository
                        .findByService_NameIgnoreCase("Tutor")
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(subjects);
    }


    // =====================================================
    // CONVERT SUBJECT TO RESPONSE
    // =====================================================

    private SubjectResponse convertToResponse(
            Subject subject) {

        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getPrice()
        );
    }
}