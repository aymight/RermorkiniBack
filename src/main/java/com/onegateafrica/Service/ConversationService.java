package com.onegateafrica.Service;

import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Entities.Message;

import java.util.List;
import java.util.Optional;

public interface ConversationService {

    Optional<Conversation> findConversationByIds(Long consommateur1Id, Long consommateur2Id);

    Conversation save(Conversation conversation);

    Optional<List<Conversation>> getAllConversation(Long id);

    Optional<Conversation> findById(Long id);
}
