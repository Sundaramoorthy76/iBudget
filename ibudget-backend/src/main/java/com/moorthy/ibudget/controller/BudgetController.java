package com.moorthy.ibudget.controller;

import com.moorthy.ibudget.model.BudgetEntry;
import com.moorthy.ibudget.model.BudgetSummary;
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
    public List<BudgetEntry> getBudgetEntries() {
        return budgetService.findAll();
    }

    @GetMapping("/budget/{id}")
    public ResponseEntity<BudgetEntry> getBudgetEntry(@PathVariable Long id) {
        return budgetService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/budget/summary")
    public BudgetSummary getSummary() {
        return budgetService.getSummary();
    }

    @PostMapping("/budget")
    public ResponseEntity<BudgetEntry> createBudgetEntry(@Valid @RequestBody BudgetEntry budgetEntry) {
        BudgetEntry savedEntry = budgetService.save(budgetEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
    }

    @PutMapping("/budget/{id}")
    public ResponseEntity<BudgetEntry> updateBudgetEntry(@PathVariable Long id, @Valid @RequestBody BudgetEntry budgetEntry) {
        BudgetEntry updatedEntry = budgetService.update(id, budgetEntry);
        return ResponseEntity.ok(updatedEntry);
    }

    @DeleteMapping("/budget/{id}")
    public ResponseEntity<Void> deleteBudgetEntry(@PathVariable Long id) {
        budgetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
