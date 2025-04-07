package com.bank.payment.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.dto.MessageDTO;
import com.bank.payment.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

@RestController
@RequestMapping("/bank")
public class MessageController {

	private final Logger log = LoggerFactory.getLogger(MessageController.class);

	private final JmsTemplate jmsTemplate;
	private final MessageService messageService;

	@Value("${ibm.mq.queue}")
	private String queueName;

	public MessageController(JmsTemplate jmsTemplate, MessageService messageService) {
		this.jmsTemplate = jmsTemplate;
		this.messageService = messageService;
	}

	/**
	 * {@code POST  /send} : send a new message.
	 *
	 * @param message the message to send.
	 * @return the {@link ResponseEntity} with status {@code 200 (message sent)} and
	 *         with body containing the message.
	 */
	@PostMapping("/mq/send")
	@Operation(summary = "Envoyer un message", description = "Envoie un message à la file IBM MQ avec le contenu, l'expéditeur et la date de réception.")
	public ResponseEntity<Map<String, ?>> sendMessage(@RequestBody MessageDTO messageDTO) {
		log.debug("REST request to send a message");
		messageDTO.setReceivedAt(LocalDateTime.now());

		jmsTemplate.send(queueName, (Session session) -> {
			TextMessage textMessage = session.createTextMessage(
					messageDTO.getSender() + "|" + messageDTO.getContent() + "|" + messageDTO.getReceivedAt());
			return textMessage;
		});

		return ResponseEntity.ok()
		        .contentType(MediaType.APPLICATION_JSON)
		        .body(Map.of(
		            "status", "success",
		            "message", "✅ Message sent to IBM MQ",
		            "data", messageDTO
		        ));
	}

	/**
	 * {@code GET  /messages} : get all the messages.
	 *
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of messages in body.
	 */
	@GetMapping("/messages")
	@Operation(summary = "Lister tous les messages", description = "Récupère une page de messages triés par date de réception, par ordre décroissant.")
	public ResponseEntity<Page<MessageDTO>> getAllMessages(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		log.debug("REST request to get a page of messages");
		Pageable pageable = PageRequest.of(page, size, Sort.by("receivedAt").descending());
		return ResponseEntity.ok(messageService.getAllMessages(pageable));
	}

	/**
	 * {@code GET  /messages/:id} : get the "id" message.
	 *
	 * @param id the id of the message to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the messageDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/message/{id}")
	@Operation(summary = "Obtenir un message par ID", description = "Retourne un message correspondant à l'identifiant fourni, s'il existe.")
	public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
		log.debug("REST request to get a Message : {}", id);
		Optional<MessageDTO> messageDTO = messageService.getMessageById(id);
		return messageDTO.map(dto -> ResponseEntity.ok().body(dto)).orElseGet(() -> ResponseEntity.notFound().build());
	}

}
