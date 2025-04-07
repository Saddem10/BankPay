package com.bank.payment.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class MessageDTO {

	@Schema(description = "Identifiant unique du message", example = "1")
	private Long id;

	@Schema(description = "Contenu du message", example = "Bonjour, voici votre relevé bancaire.")
	private String content;

	@Schema(description = "Nom ou identifiant de l'expéditeur du message", example = "BanqueX")
	private String sender;

	@Schema(description = "Date et heure de réception du message", example = "2025-04-07T10:15:30")
	private LocalDateTime receivedAt;

}
