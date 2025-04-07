package com.bank.payment.service;

import com.bank.payment.dto.MessageDTO;
import com.bank.payment.mapper.MessageMapper;
import com.bank.payment.model.Message;
import com.bank.payment.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableCaching
class MessageServiceIT {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private MessageMapper messageMapper;

    private Message message;
    private MessageDTO messageDTO;

    @BeforeEach
    void setup() {
        message = Message.builder()
                .id(1L)
                .content("Hello")
                .sender("Saddem")
                .receivedAt(LocalDateTime.now())
                .build();

        messageDTO = MessageDTO.builder()
                .id(1L)
                .content("Hello")
                .sender("Saddem")
                .receivedAt(message.getReceivedAt())
                .build();
    }
    
    @BeforeEach
    void clearCache() {
        cacheManager.getCache("messages").clear();
    }

    @Test
    void getAllMessages_shouldReturnMappedPage() {
        Page<Message> page = new PageImpl<>(List.of(message));
        when(messageRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        Page<MessageDTO> result = messageService.getAllMessages(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals("Saddem", result.getContent().get(0).getSender());
    }

    @Test
    void getMessageById_found_shouldReturnDTO() {
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        Optional<MessageDTO> result = messageService.getMessageById(1L);

        assertTrue(result.isPresent());
        assertEquals("Hello", result.get().getContent());
    }

    @Test
    void getMessageById_notFound_shouldReturnEmptyOptional() {
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<MessageDTO> result = messageService.getMessageById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void addMessage_shouldSaveAndReturnDTO() {
        Message unsaved = Message.builder()
                .content("Hello")
                .sender("Saddem")
                .receivedAt(LocalDateTime.now())
                .build();

        MessageDTO inputDto = MessageDTO.builder()
                .content("Hello")
                .sender("Saddem")
                .receivedAt(unsaved.getReceivedAt())
                .build();

        when(messageMapper.toEntity(inputDto)).thenReturn(unsaved);
        when(messageRepository.save(unsaved)).thenReturn(message);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        MessageDTO result = messageService.addMessage(inputDto);

        assertNotNull(result);
        assertEquals("Saddem", result.getSender());
    }

    @Test
    void getAllMessages_shouldUseCache() {
        Page<Message> page = new PageImpl<>(List.of(message));
        when(messageRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        Pageable pageable = PageRequest.of(0, 10);

        // 1er appel : DB
        Page<MessageDTO> result1 = messageService.getAllMessages(pageable);

        // 2e appel : cache
        Page<MessageDTO> result2 = messageService.getAllMessages(pageable);

        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());

        verify(messageRepository, times(1)).findAll(any(Pageable.class)); // un seul accès à la base de données
    }
    
    @Test
    void getAllMessages_shouldNotUseCache() {
        Page<Message> page = new PageImpl<>(List.of(message));
        when(messageRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        Pageable pageable = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 10);

        // 1er appel : DB
        Page<MessageDTO> result1 = messageService.getAllMessages(pageable);

        // 2e appel : cache
        Page<MessageDTO> result2 = messageService.getAllMessages(pageable2);

        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());

        verify(messageRepository, times(2)).findAll(any(Pageable.class)); // 2 accès à la base de données
    }
}
