package com.moorthy.ibudget.repository;

import com.moorthy.ibudget.model.BudgetEntry;
import com.moorthy.ibudget.model.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BudgetEntryRepository extends JpaRepository<BudgetEntry, Long> {

    List<BudgetEntry> findAllByOrderByEntryDateDesc();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM BudgetEntry e WHERE e.type = :type")
    BigDecimal sumByType(@Param("type") EntryType type);
}
