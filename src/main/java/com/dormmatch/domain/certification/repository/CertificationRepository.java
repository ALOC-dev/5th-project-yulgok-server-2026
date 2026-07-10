package com.dormmatch.domain.certification.repository;

import com.dormmatch.domain.certification.entity.Certification;
import com.dormmatch.domain.certification.entity.CertificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndStatusIn(Long userId, Collection<CertificationStatus> statuses);

    List<Certification> findAllByStatusOrderByCreatedAtDesc(CertificationStatus status);

    List<Certification> findAllByOrderByCreatedAtDesc();
}
