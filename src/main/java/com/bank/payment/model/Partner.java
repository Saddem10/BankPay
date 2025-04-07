package com.bank.payment.model;

import com.bank.payment.enumeration.Direction;
import com.bank.payment.enumeration.ProcessedFlowType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String alias;

	@Column(nullable = false)
	private String type;

	private Direction direction;

	private String application;

	private ProcessedFlowType processedFlowType;

	@Column(nullable = false)
	private String description;
}
