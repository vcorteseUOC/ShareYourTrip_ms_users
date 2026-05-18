package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.repositories;

import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.entitites.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationJpaRepository extends JpaRepository<Conversation, Integer> {
    
    @Query("SELECT DISTINCT c FROM Conversation c JOIN c.messages m WHERE m.sender.id = :userId OR m.recipient.id = :userId")
    List<Conversation> findConversationsByUserId(Long userId);
}