package com.bank.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bank.payment.model.Message;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

	@Autowired
	private MessageRepository messageRepository;

	@Test
	@DisplayName("Should save and retrieve a message")
	void testSaveAndFind() {
		// Arrange
		Message message = new Message();
		message.setContent("Hello MQ");
		message.setSender("System");
		message.setReceivedAt(LocalDateTime.now());

		// Act
		messageRepository.save(message);
		List<Message> messages = messageRepository.findAll();

		// Assert
		assertThat(messages).isNotEmpty();
		assertThat(messages.get(0).getContent()).isEqualTo("Hello MQ");
	}
}
