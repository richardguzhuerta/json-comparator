package com.demo.jsoncomparator.dao;

import com.demo.jsoncomparator.entity.DiffRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Methods for diff record data manipulation
 *
 * @author Richard Guzman
 * @version v1, March 2021
 */
@Repository
public interface DiffRecordRepository extends JpaRepository<DiffRecord, String> {
}
