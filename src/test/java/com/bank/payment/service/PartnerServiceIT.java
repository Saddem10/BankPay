package com.bank.payment.service;

import com.bank.payment.dto.PartnerDTO;
import com.bank.payment.enumeration.Direction;
import com.bank.payment.enumeration.ProcessedFlowType;
import com.bank.payment.mapper.PartnerMapper;
import com.bank.payment.model.Partner;
import com.bank.payment.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableCaching
class PartnerServiceIT {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private PartnerRepository partnerRepository;

    @MockBean
    private PartnerMapper partnerMapper;

    private Partner partner;
    private PartnerDTO partnerDTO;

    @BeforeEach
    void setup() {
        partner = Partner.builder()
                .id(1L)
                .alias("BankA")
                .type("PAYMENT1")
                .direction(Direction.INBOUND)
                .application("App1")
                .processedFlowType(ProcessedFlowType.ALERTING)
                .description("bank partner 1")
                .build();

        partnerDTO = PartnerDTO.builder()
                .id(1L)
                .alias("BankA")
                .type("PAYMENT1")
                .direction(Direction.INBOUND)
                .application("App1")
                .processedFlowType(ProcessedFlowType.ALERTING)
                .description("bank partner 1")
                .build();
    }

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("partners").clear();
    }

    @Test
    void getAllPartners_shouldReturnMappedPage() {
        Page<Partner> page = new PageImpl<>(List.of(partner));
        when(partnerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(partnerMapper.toDto(partner)).thenReturn(partnerDTO);

        Page<PartnerDTO> result = partnerService.getAllPartners(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals("BankA", result.getContent().get(0).getAlias());
    }

    @Test
    void getPartnerById_found_shouldReturnDTO() {
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerMapper.toDto(partner)).thenReturn(partnerDTO);

        Optional<PartnerDTO> result = partnerService.getPartnerById(1L);

        assertTrue(result.isPresent());
        assertEquals("BankA", result.get().getAlias());
    }

    @Test
    void getPartnerById_notFound_shouldReturnEmptyOptional() {
        when(partnerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PartnerDTO> result = partnerService.getPartnerById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void addPartner_shouldSaveAndReturnDTO() {
        Partner toSave = Partner.builder()
                .alias("BankA")
                .type("PAYMENT1")
                .direction(Direction.INBOUND)
                .application("App1")
                .processedFlowType(ProcessedFlowType.ALERTING)
                .description("bank partner 1")
                .build();

        PartnerDTO inputDTO = PartnerDTO.builder()
                .alias("BankA")
                .type("PAYMENT1")
                .direction(Direction.INBOUND)
                .application("App1")
                .processedFlowType(ProcessedFlowType.ALERTING)
                .description("bank partner 1")
                .build();

        when(partnerMapper.toEntity(inputDTO)).thenReturn(toSave);
        when(partnerRepository.save(toSave)).thenReturn(partner);
        when(partnerMapper.toDto(partner)).thenReturn(partnerDTO);

        PartnerDTO result = partnerService.addPartner(inputDTO);

        assertNotNull(result);
        assertEquals("BankA", result.getAlias());
    }

    @Test
    void deletePartner_shouldCallRepository() {
        partnerService.deletePartner(1L);
        verify(partnerRepository).deleteById(1L);
    }

    @Test
    void getAllPartners_shouldUseCache() {
        Page<Partner> page = new PageImpl<>(List.of(partner));
        when(partnerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(partnerMapper.toDto(partner)).thenReturn(partnerDTO);

        Pageable pageable = PageRequest.of(0, 10);

        Page<PartnerDTO> result1 = partnerService.getAllPartners(pageable);
        Page<PartnerDTO> result2 = partnerService.getAllPartners(pageable);

        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());

        verify(partnerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllPartners_shouldNotUseCache() {
        Page<Partner> page = new PageImpl<>(List.of(partner));
        when(partnerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(partnerMapper.toDto(partner)).thenReturn(partnerDTO);

        Pageable pageable1 = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 10);

        Page<PartnerDTO> result1 = partnerService.getAllPartners(pageable1);
        Page<PartnerDTO> result2 = partnerService.getAllPartners(pageable2);

        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());

        verify(partnerRepository, times(2)).findAll(any(Pageable.class));
    }
}
