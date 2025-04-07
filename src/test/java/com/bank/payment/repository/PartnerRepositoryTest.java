package com.bank.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bank.payment.enumeration.Direction;
import com.bank.payment.enumeration.ProcessedFlowType;
import com.bank.payment.model.Partner;

@DataJpaTest
@ActiveProfiles("test")
class PartnerRepositoryTest {

	@Autowired
	private PartnerRepository partnerRepository;

	@Test
	@DisplayName("Should save and retrieve a partner")
	void testSaveAndFind() {
		// Arrange
		Partner partner = new Partner();
		partner.setAlias("bankA");
		partner.setType("type1");
		partner.setDirection(Direction.INBOUND);
		partner.setApplication("application 1");
		partner.setProcessedFlowType(ProcessedFlowType.ALERTING);
		partner.setDescription("description 1");

		// Act
		partnerRepository.save(partner);
		List<Partner> partners = partnerRepository.findAll();

		// Assert
		assertThat(partners).isNotEmpty();
		assertThat(partners.get(0).getAlias()).isEqualTo("bankA");
	}
}
