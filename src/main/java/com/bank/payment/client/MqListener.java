package com.bank.payment.client;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.bank.payment.dto.MessageDTO;
import com.bank.payment.mapper.MessageMapper;
import com.bank.payment.repository.MessageRepository;
import com.bank.payment.service.MessageService;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Component
public class MqListener {

	private final MessageService messageService;

	public MqListener(MessageService messageService) {
		this.messageService = messageService;
	}

	@JmsListener(destination = "${ibm.mq.queue}")
	@Profile("!test") 
	public void receiveMessage(Message message) throws JMSException {
		if (message instanceof TextMessage textMessage) {
			String content = textMessage.getText();
			System.out.println("📩 Received Message: " + content);

			// Save message to database
			String[] elements = content.trim().split("\\|");
			MessageDTO messageDTO = new MessageDTO(null, elements[1], elements[0], LocalDateTime.parse(elements[2]));
			messageService.addMessage(messageDTO);

			System.out.println("💾 Message read!");
		}
	}
}
