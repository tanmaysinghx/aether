package com.aether.engine.tube.internal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String handle;

    private String description;
    private String profilePicUrl;
    private String bannerUrl;

    private String country;
    private String defaultLanguage;

    private Long subscribersCount = 0L;
    private Long videosCount = 0L;
    private Long viewsCount = 0L;

    @Enumerated(EnumType.STRING)
    private ChannelStatus status = ChannelStatus.ACTIVE;

    private boolean isVerified = false;
    private boolean isMadeForKids = false;

    private boolean allowCommentsByDefault = true;
    @Enumerated(EnumType.STRING)
    private DefaultVisibility visibility = DefaultVisibility.PUBLIC;

    private String ownerId;
    private String uploadsPlaylistId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
