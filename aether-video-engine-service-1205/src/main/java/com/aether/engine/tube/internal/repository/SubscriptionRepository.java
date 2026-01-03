package com.aether.engine.tube.internal.repository;

import com.aether.engine.tube.internal.entity.Subscription;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, java.util.UUID> {
    Optional<Subscription> findBySubscriberEmailAndChannelId(String subscriberEmail, String channelId);

    long countByChannelId(String channelId);

    boolean existsBySubscriberEmailAndChannelId(String subscriberEmail, String channelId);

    Page<Subscription> findBySubscriberEmail(String subscriberEmail, Pageable pageable);

    Page<Subscription> findByChannelId(String channelId, Pageable pageable);
}
