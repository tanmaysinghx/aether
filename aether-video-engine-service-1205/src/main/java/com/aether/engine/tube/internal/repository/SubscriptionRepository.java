package com.aether.engine.tube.internal.repository;

import com.aether.engine.tube.internal.entity.Subscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, java.util.UUID> {
    Optional<Subscription> findBySubscriberEmailAndChannelEmail(String subscriberEmail, String channelEmail);

    long countByChannelEmail(String channelEmail);

    boolean existsBySubscriberEmailAndChannelEmail(String subscriberEmail, String channelEmail);
}
