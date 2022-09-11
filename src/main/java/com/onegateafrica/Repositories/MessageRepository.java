package com.onegateafrica.Repositories;


import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT * FROM message WHERE id_conversation = :conversationId  ORDER BY timestamp ASC", nativeQuery = true)
    Optional<List<Message>> filterMessage(@Param(value="conversationId") Long conversationId);
    @Modifying
    @Query(value = "UPDATE message SET seen=1 WHERE id_conversation=:conversationId " +
            "AND reciever_id = :recieverId AND seen = 0", nativeQuery = true)
    int setSeen(@Param(value="conversationId") Long conversationId
            , @Param(value="recieverId") Long recieverId);
    @Query(value = "SELECT * FROM message WHERE id_conversation=:conversationId " +
            "AND reciever_id = :recieverId AND seen = 0", nativeQuery = true)
    Optional<List<Message>> getUnseen(@Param(value="conversationId") Long conversationId
            , @Param(value="recieverId") Long recieverId);
    @Modifying
    @Query(value = "UPDATE message SET recieved=1 WHERE reciever_id = :recieverId AND recieved = 0", nativeQuery = true)
    int setRecieved(@Param(value="recieverId") Long recieverId);
    @Modifying
    @Query(value = "UPDATE message SET received=1 WHERE reciever_id = :recieverId AND id_conversation = :conversationId AND received = 0", nativeQuery = true)
    int setRecieved(@Param(value="recieverId") Long recieverId, @Param(value="conversationId") Long conversationId);
    @Query(value = "SELECT * FROM message WHERE id_conversation = :conversationId AND reciever_id = :recieverId AND received = 0  ORDER BY timestamp DESC", nativeQuery = true)
    Optional<List<Message>> getNewMessages(@Param(value="recieverId") Long recieverId, @Param(value="conversationId") Long conversationId);
}

