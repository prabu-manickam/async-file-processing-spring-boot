package com.babin.csvreconciler.dao;

import com.babin.csvreconciler.model.QwoRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QwoRecordDao  extends JpaRepository<QwoRecord, Long> {
    Optional<QwoRecord> findOneByCustomerNumberIsAndProductReference(String customerNumber, String productReference);

}
