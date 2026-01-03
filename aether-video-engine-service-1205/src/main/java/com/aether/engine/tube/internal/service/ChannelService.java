package com.aether.engine.tube.internal.service;

import com.aether.engine.tube.internal.dto.ChannelResponse;
import com.aether.engine.tube.internal.dto.ChannelStatsResponse;
import com.aether.engine.tube.internal.dto.CreateChannelRequest;
import com.aether.engine.tube.internal.entity.Channel;
import com.aether.engine.tube.internal.entity.ChannelStatus;
import com.aether.engine.tube.internal.repository.ChannelRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    @Transactional
    public ChannelResponse createChannel(CreateChannelRequest request) {
        if (channelRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Channel already exists for this email");
        }
        if (channelRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Channel name is already taken");
        }
        if (channelRepository.existsByHandle(request.getHandle())) {
            throw new IllegalArgumentException("Channel handle is already taken");
        }

        Channel channel = new Channel();
        channel.setEmail(request.getEmail());
        channel.setName(request.getName());
        channel.setHandle(request.getHandle());
        channel.setDescription(request.getDescription());
        channel.setProfilePicUrl(request.getProfilePicUrl());
        channel.setBannerUrl(request.getBannerUrl());
        channel.setCountry(request.getCountry());
        channel.setDefaultLanguage(request.getDefaultLanguage());
        channel.setMadeForKids(request.isMadeForKids());
        channel.setAllowCommentsByDefault(request.isAllowCommentsByDefault());
        channel.setVisibility(request.getVisibility());
        channel.setOwnerId(request.getOwnerId());

        channel.setSubscribersCount(0L);
        channel.setVideosCount(0L);
        channel.setViewsCount(0L);
        channel.setStatus(ChannelStatus.ACTIVE);
        channel.setVerified(false);

        Channel savedChannel = channelRepository.save(channel);
        return mapToResponse(savedChannel);
    }

    @Transactional(readOnly = true)
    public ChannelResponse getChannelById(String id) {
        return channelRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));
    }

    @Transactional(readOnly = true)
    public ChannelResponse getChannelByHandle(String handle) {
        return channelRepository.findByHandle(handle)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found with handle: " + handle));
    }

    @Transactional(readOnly = true)
    public Page<ChannelResponse> searchChannelsPaginated(
            String query, String country, ChannelStatus status, Pageable pageable) {

        Specification<Channel> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !query.isBlank()) {
                String searchPattern = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), searchPattern),
                        cb.like(cb.lower(root.get("handle")), searchPattern),
                        cb.like(cb.lower(root.get("description")), searchPattern)));
            }

            if (country != null && !country.isBlank()) {
                predicates.add(cb.equal(root.get("country"), country));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return channelRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ChannelStatsResponse getChannelStats(String id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        return ChannelStatsResponse.builder()
                .channelId(channel.getId())
                .subscribersCount(channel.getSubscribersCount())
                .videosCount(channel.getVideosCount())
                .viewsCount(channel.getViewsCount())
                .build();
    }

    private ChannelResponse mapToResponse(Channel channel) {
        ChannelResponse response = new ChannelResponse();
        response.setId(channel.getId());
        response.setEmail(channel.getEmail());
        response.setName(channel.getName());
        response.setHandle(channel.getHandle());
        response.setDescription(channel.getDescription());
        response.setProfilePicUrl(channel.getProfilePicUrl());
        response.setBannerUrl(channel.getBannerUrl());
        response.setCountry(channel.getCountry());
        response.setDefaultLanguage(channel.getDefaultLanguage());
        response.setSubscribersCount(channel.getSubscribersCount());
        response.setVideosCount(channel.getVideosCount());
        response.setViewsCount(channel.getViewsCount());
        response.setStatus(channel.getStatus());
        response.setVerified(channel.isVerified());
        response.setMadeForKids(channel.isMadeForKids());
        response.setAllowCommentsByDefault(channel.isAllowCommentsByDefault());
        response.setVisibility(channel.getVisibility());
        response.setOwnerId(channel.getOwnerId());
        response.setUploadsPlaylistId(channel.getUploadsPlaylistId());
        response.setCreatedAt(channel.getCreatedAt());
        response.setUpdatedAt(channel.getUpdatedAt());
        return response;
    }
}
