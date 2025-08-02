package com.example.miniLMS.controller;

import com.example.miniLMS.entity.Module;
import com.example.miniLMS.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/modules")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Module Management")
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping("/{moduleId}")
    @Operation(summary = "Get module by ID")
    public ResponseEntity<Module> getModuleById(@PathVariable("moduleId") Long moduleId) {
        try {
            return moduleService.getModuleById(moduleId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{moduleId}/progress")
    @Operation(summary = "Get module progress")
    public ResponseEntity<Double> getModuleProgress(
            @PathVariable Long moduleId,
            @RequestParam String userId) {
        try {
            double progress = moduleService.calculateModuleProgress(moduleId, userId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
