//package com.bank.payment.client;
//
//import static org.mockito.Mockito.*;
//
//import java.time.LocalDateTime;
//
//import com.bank.payment.dto.MessageDTO;
//import com.bank.payment.mapper.MessageMapper;
//import com.bank.payment.model.Message;
//import com.bank.payment.repository.MessageRepository;
//
//import jakarta.jms.TextMessage;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//
//public class MqListenerTest {
//
//	@Mock
//	private MessageRepository messageRepository;
//
//	@Mock
//	private MessageMapper messageMapper;
//
//	@InjectMocks
//	private MqListener mqListener;
//
//	@BeforeEach
//	void setUp() {
//		MockitoAnnotations.openMocks(this);
//	}
//
//	@Test
//	void testReceiveMessage_shouldParseAndSave() throws Exception {
//	    // Given
//	    String sender = "Saddem";
//	    String content = "Hello";
//	    LocalDateTime receivedAt = LocalDateTime.now();
//	    String text = sender + "|" + content + "|" + receivedAt.toString();
//
//	    TextMessage mockTextMessage = mock(TextMessage.class);
//	    when(mockTextMessage.getText()).thenReturn(text);
//
//	    MessageDTO expectedDto = MessageDTO.builder()
//	            .sender(sender)
//	            .content(content)
//	            .receivedAt(receivedAt)
//	            .build();
//
//	    Message expectedEntity = Message.builder()
//	            .sender(sender)
//	            .content(content)
//	            .receivedAt(receivedAt)
//	            .build();
//
//	    // Assurez-vous que le mapper retourne l'entité
//	    when(messageMapper.toEntity(expectedDto)).thenReturn(expectedEntity);
//
//	    // When
//	    mqListener.receiveMessage(mockTextMessage);
//
//	    // Then
//	    verify(messageRepository, times(1)).save(expectedEntity);
//	}
//
//}
