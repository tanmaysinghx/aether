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
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(indexes = {
                @Index(name = "idx_subscription_subscriber", columnList = "subscriberEmail"),
                @Index(name = "idx_subscription_channel", columnList = "channelId"),
                @Index(name = "idx_subscription_channel_email", columnList = "channelEmail"),
                @Index(name = "idx_subscription_created", columnList = "createdAt")
}, uniqueConstraints = @UniqueConstraint(columnNames = { "subscriberEmail", "channelId" }))
public class Subscription {

        @Id
        @GeneratedValue
        private UUID id;

        @Column(nullable = false)
        private String subscriberEmail;

        @Column(nullable = false)
        private String channelId;

        @Column(nullable = false)
        private String channelEmail;

        @UpdateTimestamp
        private LocalDateTime updatedAt;

        @CreationTimestamp
        private LocalDateTime createdAt;
}
