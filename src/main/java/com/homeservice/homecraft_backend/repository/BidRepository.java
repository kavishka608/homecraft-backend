package com.homeservice.homecraft_backend.repository;

import com.homeservice.homecraft_backend.model.entity.Bid;
import com.homeservice.homecraft_backend.model.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByProjectId(Long projectId);
    List<Bid> findByProfessionalId(Long professionalId);

    // This returns List<Bid> - multiple bids can be pending for a project
    List<Bid> findByProjectIdAndStatus(Long projectId, BidStatus status);

    // This returns Optional<Bid> - for finding a specific bid
    Optional<Bid> findByIdAndStatus(Long id, BidStatus status);

    boolean existsByProjectIdAndProfessionalId(Long projectId, Long professionalId);
}