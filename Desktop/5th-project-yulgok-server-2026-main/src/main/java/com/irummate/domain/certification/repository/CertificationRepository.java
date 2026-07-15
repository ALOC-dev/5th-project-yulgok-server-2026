package com.irummate.domain.certification.repository;

import com.irummate.domain.certification.entity.Certification;
import com.irummate.domain.certification.entity.CertificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findAllByOrderByCreatedAtDesc();

    List<Certification> findAllByCertificationStatusOrderByCreatedAtDesc(CertificationStatus certificationStatus);

    Optional<Certification> findByUser_IdAndSemester(Long userId, String semester);

    List<Certification> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Certification> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    boolean existsByUser_IdAndSemester(Long userId, String semester);
}
