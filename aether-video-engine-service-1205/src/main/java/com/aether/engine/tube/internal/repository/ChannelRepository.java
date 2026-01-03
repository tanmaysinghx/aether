package com.aether.engine.tube.internal.repository;

import com.aether.engine.tube.internal.entity.Channel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String>, JpaSpecificationExecutor<Channel> {
    Optional<Channel> findByName(String name);

    Optional<Channel> findByHandle(String handle);

    boolean existsByName(String name);

    boolean existsByHandle(String handle);

    java.util.List<Channel> findByNameContainingIgnoreCaseOrHandleContainingIgnoreCase(String name, String handle);

    Optional<Channel> findByEmail(String email);

    boolean existsByEmail(String email);
}
