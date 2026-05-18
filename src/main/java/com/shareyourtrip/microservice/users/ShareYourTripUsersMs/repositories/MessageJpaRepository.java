package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageJpaRepository extends JpaRepository<Message, Integer> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);

    List<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId);
}