package com.aether.engine.tube.internal.service;

import com.aether.engine.tube.internal.dto.ChannelResponse;
import com.aether.engine.tube.internal.dto.CreateChannelRequest;
import com.aether.engine.tube.internal.entity.Channel;
import com.aether.engine.tube.internal.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    @Transactional
    public ChannelResponse createChannel(CreateChannelRequest request) {
        if (channelRepository.existsById(request.getEmail())) {
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
        channel.setSubscribersCount(0L);

        Channel savedChannel = channelRepository.save(channel);
        return mapToResponse(savedChannel);
    }

    private ChannelResponse mapToResponse(Channel channel) {
        ChannelResponse response = new ChannelResponse();
        response.setEmail(channel.getEmail());
        response.setName(channel.getName());
        response.setHandle(channel.getHandle());
        response.setSubscribersCount(channel.getSubscribersCount());
        response.setCreatedAt(channel.getCreatedAt());
        return response;
    }
}
