package com.onegateafrica.ServiceImpl;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Repositories.ConsommateurRepository;
import com.onegateafrica.Repositories.ConversationRepository;
import com.onegateafrica.Service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {


	private final ConversationRepository conversationRepository;
	@Autowired
	public ConversationServiceImpl(ConversationRepository conversationRepository){
		this.conversationRepository=conversationRepository;

	}

	@Override
	public Optional<Conversation> findConversationByIds(Long consommateur1Id, Long consommateur2Id) {
		return this.conversationRepository.findConversation(consommateur1Id,consommateur2Id);
	}
	@Override
	public Conversation save(Conversation conversation) {
		return this.conversationRepository.save(conversation);
	}

	@Override
	public Optional<List<Conversation>> getAllConversation(Long id) {
		return conversationRepository.findAllConversations(id);
	}

	@Override
	public Optional<Conversation> findById(Long id) {
		return conversationRepository.findById(id);
	}
}
