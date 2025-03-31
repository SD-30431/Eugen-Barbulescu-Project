package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.AuthActivityDTO;
import com.example.sd_backend2.dto.CategoryStatsDTO;
import com.example.sd_backend2.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuthActivityDTO>> getRecentActivity() {
        List<AuthActivityDTO> activities = adminService.getRecentActivity();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryStatsDTO>> getCategoryStats() {
        List<CategoryStatsDTO> result = adminService.getCategoryStats();
        return ResponseEntity.ok(result);
    }
}
