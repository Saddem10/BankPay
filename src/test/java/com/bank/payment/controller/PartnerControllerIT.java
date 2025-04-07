package com.bank.payment.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.bank.payment.dto.PartnerDTO;
import com.bank.payment.enumeration.Direction;
import com.bank.payment.enumeration.ProcessedFlowType;
import com.bank.payment.service.PartnerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PartnerControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PartnerService partnerService;

	private PartnerDTO partner1;
	private PartnerDTO partner2;

	@BeforeEach
	void init() {
		partner1 = createPartnerDTO(1L, "BankA", "PAYMENT1", Direction.INBOUND, "App1", ProcessedFlowType.ALERTING, "bank partner 1");
		partner2 = createPartnerDTO(2L, "BankB", "PAYMENT2", Direction.OUTBOUND, "App2", ProcessedFlowType.NOTIFICATION, "bank partner 2");
	}

	private PartnerDTO createPartnerDTO(Long id, String alias, String type, Direction direction, String application, ProcessedFlowType processedFlowType, String description) {
	    PartnerDTO.PartnerDTOBuilder builder = PartnerDTO.builder()
	            .alias(alias)
	            .type(type)
	            .direction(direction)
	            .application(application)
	            .processedFlowType(processedFlowType)
	            .description(description);

	    if (id != null) {
	        builder.id(id);
	    }

	    return builder.build();
	}
	
	@Test
	void testCreatePartner_shouldReturnCreated() throws Exception {
		PartnerDTO partnerToSave = createPartnerDTO(null, "BankA", "PAYMENT1", Direction.INBOUND, "App1", ProcessedFlowType.ALERTING, "bank partner 1");
		when(partnerService.addPartner(any(PartnerDTO.class))).thenReturn(partner1);

		mockMvc.perform(post("/bank/partner")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(partnerToSave)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.alias", is(partner1.getAlias())))
				.andExpect(jsonPath("$.id", is(partner1.getId().intValue())));
	}


	@Test
	void testCreatePartner_shouldReturnBadRequest_ifIdIsPresent() throws Exception {
		// ID ne doit pas être présent pour une création
		mockMvc.perform(post("/bank/partner")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(partner1)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("A new partner cannot already have an ID")); 
	}
	
	@Test
	void testCreatePartner_shouldReturnBadRequest_whenAliasIsBlank() throws Exception {
		PartnerDTO invalidPartner = createPartnerDTO(null, "", "PAYMENT1", Direction.INBOUND, "App1", ProcessedFlowType.ALERTING, "bank partner 1");

	    mockMvc.perform(post("/bank/partner")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(invalidPartner)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.alias", is("Alias is required")));
	}
	
	@Test
	void testCreatePartner_shouldReturnBadRequest_whenTypeIsBlank_andDescriptionIsBlank() throws Exception {
		PartnerDTO invalidPartner = createPartnerDTO(null, "BankA", "", Direction.INBOUND, "App1", ProcessedFlowType.ALERTING, "");

	    mockMvc.perform(post("/bank/partner")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(invalidPartner)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.type", is("Type is required")))
	        .andExpect(jsonPath("$.description", is("Description is required")));
	}

	@Test
	void shouldReturnAllPartnersWithPagination() throws Exception {
		List<PartnerDTO> partners = Arrays.asList(partner1, partner2);
		Page<PartnerDTO> page = new PageImpl<>(partners);
		when(partnerService.getAllPartners(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/bank/partners").param("page", "0").param("size", "10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].alias", is(partner1.getAlias())))
				.andExpect(jsonPath("$.content[1].alias", is(partner2.getAlias())));
	}

	@Test
	void testGetPartnerById_shouldReturnPartner_whenFound() throws Exception {
		when(partnerService.getPartnerById(1L)).thenReturn(Optional.of(partner1));

		mockMvc.perform(get("/bank/partner/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(partner1.getId().intValue())))
				.andExpect(jsonPath("$.alias", is(partner1.getAlias())));
	}

	@Test
	void testGetPartnerById_shouldReturnNotFound_whenMissing() throws Exception {
		when(partnerService.getPartnerById(999L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/bank/partner/{id}", 999L))
				.andExpect(status().isNotFound());
	}

	@Test
	void testDeletePartner_shouldReturnNoContent() throws Exception {
		doNothing().when(partnerService).deletePartner(1L);

		mockMvc.perform(delete("/bank/partner/{id}", 1L))
				.andExpect(status().isNoContent());
	}
	
}
