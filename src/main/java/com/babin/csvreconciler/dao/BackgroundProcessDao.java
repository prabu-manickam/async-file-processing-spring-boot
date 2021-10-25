package com.babin.csvreconciler.dao;

import com.babin.csvreconciler.model.BackgroundProcess;
import com.babin.csvreconciler.model.QwoRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackgroundProcessDao extends JpaRepository<BackgroundProcess, Long> {
}
