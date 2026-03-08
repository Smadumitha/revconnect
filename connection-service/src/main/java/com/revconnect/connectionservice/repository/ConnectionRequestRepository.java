package com.revconnect.connectionservice.repository;

import com.revconnect.connectionservice.entity.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConnectionRequestRepository
        extends JpaRepository<ConnectionRequest, Long> {

    List<ConnectionRequest> findByReceiverId(Long receiverId);
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
    boolean existsBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
            Long senderId, Long receiverId,
            Long receiverId2, Long senderId2
    );

    // NEW METHODS (for pending requests)
    List<ConnectionRequest> findByReceiverIdAndStatus(Long receiverId, String status);

    List<ConnectionRequest> findBySenderIdAndStatus(Long senderId, String status);

}