package com.aether.engine.tube.internal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_subscription_subscriber", columnList = "subscriberEmail"),
        @Index(name = "idx_subscription_channel", columnList = "channelEmail")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = { "subscriberEmail", "channelEmail" })
})
public class Subscription {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String subscriberEmail;

    @Column(nullable = false)
    private String channelEmail;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
