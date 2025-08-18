package com.example.repository;

import com.example.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
           "(:studentId IS NULL OR s.studentId = :studentId) AND " +
           "(:className IS NULL OR s.className = :className)")
    Page<Student> findByFilters(
            @Param("studentId") Long studentId,
            @Param("className") String className,
            Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Student s WHERE " +
           "(:studentId IS NULL OR s.studentId = :studentId) AND " +
           "(:className IS NULL OR s.className = :className)")
    Long countByFilters(
            @Param("studentId") Long studentId,
            @Param("className") String className
    );
}
