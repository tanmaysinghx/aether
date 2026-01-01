package com.aether.engine.tube.internal.repository;

import com.aether.engine.tube.internal.entity.Channel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {
    Optional<Channel> findByName(String name);

    Optional<Channel> findByHandle(String handle);

    boolean existsByName(String name);

    boolean existsByHandle(String handle);
}
