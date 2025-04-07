package com.bank.payment.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.payment.dto.MessageDTO;
import com.bank.payment.model.Message;
import com.bank.payment.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = { "ibm.mq.queue=mockedQueue" })
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MessageControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MessageService messageService;

	@MockBean
	private JmsTemplate jmsTemplate;

	@Test
	void shouldSendMessageToMqSuccessfully() throws Exception {
		MessageDTO message = createMessageDTO(null, "Alice", "Hello MQ!");

		mockMvc.perform(post("/bank/mq/send").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(message))).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("✅ Message sent")));
	}

	@Test
	void shouldReturnAllMessagesWithPagination() throws Exception {
		// Given
		MessageDTO msg1 = createMessageDTO(1L, "Hello", "Saddem");
		MessageDTO msg2 = createMessageDTO(2L, "Hey", "Ali");
		List<MessageDTO> messageList = Arrays.asList(msg1, msg2);
		Page<MessageDTO> page = new PageImpl<>(messageList);

		// When
		when(messageService.getAllMessages(any(Pageable.class))).thenReturn(page);

		// Then
		mockMvc.perform(
				get("/bank/messages").param("page", "0").param("size", "10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].sender", is("Saddem")))
				.andExpect(jsonPath("$.content[1].content", is("Hey")));
	}

	@Test
	void shouldReturnMessageById_whenExists() throws Exception {
		// Given
		Long id = 1L;
		MessageDTO messageDTO = createMessageDTO(id, "Hello", "Saddem");
		when(messageService.getMessageById(id)).thenReturn(Optional.of(messageDTO));

		// When & Then
		mockMvc.perform(get("/bank/message/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.sender").value("Saddem")).andExpect(jsonPath("$.content").value("Hello"));
	}

	@Test
	void shouldReturn404_whenMessageNotFound() throws Exception {
		// Given
		Long id = 999L;
		when(messageService.getMessageById(id)).thenReturn(Optional.empty());

		// When & Then
		mockMvc.perform(get("/bank/message/{id}", id)).andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
	    mockMvc.perform(get("/bank/message/invalid-id"))
	           .andExpect(status().isBadRequest());
	}
	

	private MessageDTO createMessageDTO(Long id, String content, String sender) {
		return MessageDTO.builder()
				.id(id)
				.content(content)
				.sender(sender)
				.receivedAt(LocalDateTime.now())
				.build();
	}

}
