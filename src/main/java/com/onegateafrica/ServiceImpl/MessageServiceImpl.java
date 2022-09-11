package com.onegateafrica.ServiceImpl;

import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Entities.Message;
import com.onegateafrica.Repositories.MessageRepository;
import com.onegateafrica.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {


	private final MessageRepository messageRepository;
	@Autowired
	public MessageServiceImpl(MessageRepository messageRepository){
		this.messageRepository=messageRepository;

	}
	@Override
	public Message save(Message message) {
		return this.messageRepository.save(message);
	}

	@Override
	public Optional<List<Message>> filterMessage(Long id) {
		return messageRepository.filterMessage(id);
	}

	@Override
	public int setSeen(Long conversationId, Long senderId) {
		return messageRepository.setSeen(conversationId, senderId);
	}

	@Override
	public Optional<List<Message>> getUnseen(Long conversationId, Long senderId) {
		return messageRepository.getUnseen(conversationId, senderId);
	}

	@Override
	public int setRecieved(Long recieverId) {
		return messageRepository.setRecieved(recieverId);
	}

	@Override
	public Optional<List<Message>> getNewMessages(Long recieverId,Long id) {
		return messageRepository.getNewMessages(recieverId,id);
	}

	@Override
	public int setRecieved(Long recieverId, Long conversationId) {
		return messageRepository.setRecieved(recieverId,conversationId);
	}
}
