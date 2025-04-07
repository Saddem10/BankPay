package com.bank.payment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bank.payment.dto.MessageDTO;

public interface MessageService {
	/**
     * Get all messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
	Page<MessageDTO> getAllMessages(Pageable pageable);
	
	/**
     * Get message by id.
     *
     * @param id the id of the entity.
     * @return the entity if exists.
     */
	Optional<MessageDTO> getMessageById(Long id);
	
	/**
     * Add a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
	MessageDTO addMessage(MessageDTO messageDTO);
}
