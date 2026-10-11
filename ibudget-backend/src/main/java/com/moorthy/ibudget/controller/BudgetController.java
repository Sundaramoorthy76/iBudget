package com.moorthy.ibudget.controller;

import com.moorthy.ibudget.dto.BudgetEntryRequest;
import com.moorthy.ibudget.dto.BudgetEntryResponse;
import com.moorthy.ibudget.dto.BudgetSummaryResponse;
import com.moorthy.ibudget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"}, originPatterns = {"https://*.onrender.com", "http://*.onrender.com"})
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/budget")
    public List<BudgetEntryResponse> getBudgetEntries() {
        return budgetService.findAll();
    }

    @GetMapping("/budget/{id}")
    public ResponseEntity<BudgetEntryResponse> getBudgetEntry(@PathVariable Long id) {
        return budgetService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/budget/summary")
    public BudgetSummaryResponse getSummary() {
        return budgetService.getSummary();
    }

    @PostMapping("/budget")
    public ResponseEntity<BudgetEntryResponse> createBudgetEntry(@Valid @RequestBody BudgetEntryRequest request) {
        BudgetEntryResponse savedEntry = budgetService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
    }

    @PutMapping("/budget/{id}")
    public ResponseEntity<BudgetEntryResponse> updateBudgetEntry(
            @PathVariable Long id,
            @Valid @RequestBody BudgetEntryRequest request
    ) {
        BudgetEntryResponse updatedEntry = budgetService.update(id, request);
        return ResponseEntity.ok(updatedEntry);
    }

    @DeleteMapping("/budget/{id}")
    public ResponseEntity<Void> deleteBudgetEntry(@PathVariable Long id) {
        budgetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
