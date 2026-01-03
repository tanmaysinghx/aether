package com.aether.engine.tube.internal.service;

import com.aether.engine.tube.internal.entity.Channel;
import com.aether.engine.tube.internal.entity.Subscription;
import com.aether.engine.tube.internal.repository.ChannelRepository;
import com.aether.engine.tube.internal.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public Subscription subscribe(String channelId, String subscriberEmail) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        if (subscriberEmail.equals(channel.getEmail())) {
            throw new IllegalArgumentException("Cannot subscribe to own channel");
        }

        if (subscriptionRepository.existsBySubscriberEmailAndChannelId(subscriberEmail, channelId)) {
            throw new IllegalStateException("Already subscribed");
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriberEmail(subscriberEmail);
        subscription.setChannelId(channelId);
        subscription.setChannelEmail(channel.getEmail());
        Subscription saved = subscriptionRepository.save(subscription);

        channel.setSubscribersCount(channel.getSubscribersCount() + 1);
        channelRepository.save(channel);

        return saved;
    }

    @Transactional
    public void unsubscribe(String channelId, String subscriberEmail) {
        Subscription subscription = subscriptionRepository
                .findBySubscriberEmailAndChannelId(subscriberEmail, channelId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscriptionRepository.delete(subscription);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        if (channel.getSubscribersCount() > 0) {
            channel.setSubscribersCount(channel.getSubscribersCount() - 1);
            channelRepository.save(channel);
        }
    }

    @Transactional(readOnly = true)
    public Page<Subscription> getMySubscriptions(String subscriberEmail, Pageable pageable) {
        return subscriptionRepository.findBySubscriberEmail(subscriberEmail, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Subscription> getChannelSubscribers(String channelId, Pageable pageable) {
        return subscriptionRepository.findByChannelId(channelId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(String subscriberEmail, String channelId) {
        return subscriptionRepository.existsBySubscriberEmailAndChannelId(subscriberEmail, channelId);
    }

    @Transactional(readOnly = true)
    public long getSubscriberCount(String channelId) {
        return subscriptionRepository.countByChannelId(channelId);
    }
}
