package com.bank.payment.dto;

import com.bank.payment.enumeration.Direction;
import com.bank.payment.enumeration.ProcessedFlowType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerDTO {

	@Schema(description = "Identifiant unique du partenaire", example = "1")
	private Long id;

	@Schema(description = "Alias du partenaire", example = "BanqueY")
	@NotBlank(message = "Alias is required")
	private String alias;

	@Schema(description = "Type de partenaire", example = "Banque")
	@NotBlank(message = "Type is required")
	private String type;

	@Schema(description = "Direction du flux associé au partenaire", example = "INCOMING")
	private Direction direction;

	@Schema(description = "Nom de l'application liée au partenaire", example = "AppPaiement")
	private String application;

	@Schema(description = "Type de flux traité", example = "MESSAGE")
	private ProcessedFlowType processedFlowType;

	@Schema(description = "Description du partenaire", example = "Partenaire pour les virements entrants")
	@NotBlank(message = "Description is required")
	private String description;
}
