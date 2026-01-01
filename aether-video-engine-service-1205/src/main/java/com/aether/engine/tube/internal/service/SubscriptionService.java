package com.aether.engine.tube.internal.service;

import com.aether.engine.tube.internal.entity.Channel;
import com.aether.engine.tube.internal.entity.Subscription;
import com.aether.engine.tube.internal.repository.ChannelRepository;
import com.aether.engine.tube.internal.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public void subscribe(String subscriberEmail, String channelEmail) {
        if (subscriberEmail.equals(channelEmail)) {
            throw new IllegalArgumentException("Cannot subscribe to own channel");
        }

        Channel channel = channelRepository.findById(channelEmail)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        if (subscriptionRepository.existsBySubscriberEmailAndChannelEmail(subscriberEmail, channelEmail)) {
            throw new IllegalArgumentException("Already subscribed");
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriberEmail(subscriberEmail);
        subscription.setChannelEmail(channelEmail);
        subscriptionRepository.save(subscription);

        channel.setSubscribersCount(channel.getSubscribersCount() + 1);
        channelRepository.save(channel);
    }

    @Transactional
    public void unsubscribe(String subscriberEmail, String channelEmail) {
        Subscription subscription = subscriptionRepository
                .findBySubscriberEmailAndChannelEmail(subscriberEmail, channelEmail)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscriptionRepository.delete(subscription);

        Channel channel = channelRepository.findById(channelEmail)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        if (channel.getSubscribersCount() > 0) {
            channel.setSubscribersCount(channel.getSubscribersCount() - 1);
            channelRepository.save(channel);
        }
    }
}
