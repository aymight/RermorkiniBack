package com.onegateafrica.Service;

import com.onegateafrica.Entities.Message;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message save(Message message);
    Optional<List<Message>> filterMessage(Long id);
    int setSeen(Long conversationId, Long senderId);
    Optional<List<Message>> getUnseen(Long conversationId, Long senderId);
    int setRecieved(Long recieverId);
    Optional<List<Message>> getNewMessages(Long recieverId, Long id);
    int setRecieved(Long recieverId, Long conversationId);
}
