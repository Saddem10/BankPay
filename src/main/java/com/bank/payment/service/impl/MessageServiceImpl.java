package com.bank.payment.service.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bank.payment.dto.MessageDTO;
import com.bank.payment.mapper.MessageMapper;
import com.bank.payment.repository.MessageRepository;
import com.bank.payment.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final MessageMapper messageMapper;

	public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
		this.messageRepository = messageRepository;
		this.messageMapper = messageMapper;
	}

	@Override
	@Cacheable(value = "messages", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
	public Page<MessageDTO> getAllMessages(Pageable pageable) {
		return messageRepository.findAll(pageable).map(messageMapper::toDto);
	}

	@Override
	public Optional<MessageDTO> getMessageById(Long id) {
		return Optional
				.ofNullable(messageRepository.findById(id).map(message -> messageMapper.toDto(message)).orElse(null));
	}

	@Override
	@CacheEvict(value = "messages", allEntries = true)
	public MessageDTO addMessage(MessageDTO messageDTO) {
		return messageMapper.toDto(messageRepository.save(messageMapper.toEntity(messageDTO)));
	}

}
