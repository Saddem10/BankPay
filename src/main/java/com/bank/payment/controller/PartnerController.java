package com.bank.payment.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.dto.PartnerDTO;
import com.bank.payment.service.PartnerService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bank")
public class PartnerController {

	private final Logger log = LoggerFactory.getLogger(PartnerController.class);

	private final PartnerService partnerService;

	public PartnerController(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	/**
	 * {@code POST  /partner} : Create a new partner.
	 *
	 * @param partnerDTO the partnerDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new partnerDTO, or with status {@code 400 (Bad Request)} if
	 *         the partner has already an ID.
	 * @throws BadRequestException
	 * @throws URISyntaxException  if the Location URI syntax is incorrect.
	 */
	@PostMapping("/partner")
	@Operation(summary = "Ajouter un partenaire", description = "Ajouter un nouveau partenaire das la base.")
	public ResponseEntity<?> createPartner(@Valid @RequestBody PartnerDTO partnerDTO)
			throws URISyntaxException, BadRequestException {
		log.debug("REST request to save Partner : {}", partnerDTO);
		if (partnerDTO.getId() != null) {
			//throw new BadRequestException("A new partner cannot already have an ID");
			return ResponseEntity.badRequest().body("A new partner cannot already have an ID");
		}
		PartnerDTO result = partnerService.addPartner(partnerDTO);
		return ResponseEntity.created(new URI("/api/partner/" + result.getId())).body(result);
	}

	/**
	 * {@code GET  /partners} : get all the partners.
	 *
	 *
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of partners in body.
	 */
	@GetMapping("/partners")
	@Operation(summary = "Lister tous les partenaires", description = "Récupère une page de partenaires triés par ID, par ordre décroissant.")
	public ResponseEntity<Page<PartnerDTO>> getAllPartners(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		log.debug("REST request to get a page of partners");
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return ResponseEntity.ok(partnerService.getAllPartners(pageable));
	}

	/**
	 * {@code GET  /partners/:id} : get the "id" partner.
	 *
	 * @param id the id of the partner to retrieve.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
	 *         the partnerDTO, or with status {@code 404 (Not Found)}.
	 */
	@GetMapping("/partner/{id}")
	@Operation(summary = "Obtenir un partenaire par ID", description = "Retourne un partenaire correspondant à l'identifiant fourni, s'il existe.")
	public ResponseEntity<PartnerDTO> getPartnerById(@PathVariable Long id) {
		log.debug("REST request to get a Partner : {}", id);
		Optional<PartnerDTO> partnerDTO = partnerService.getPartnerById(id);
		return partnerDTO.map(dto -> ResponseEntity.ok().body(dto)).orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * {@code DELETE  /partner/:id} : delete the "id" partner.
	 *
	 * @param id the id of the partner to delete.
	 * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
	 */
	@DeleteMapping("/partner/{id}")
	@Operation(summary = "Supprimer un partenaire", description = "Supprime le partenaire correspondant à l'identifiant fourni.")
	public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
		log.debug("REST request to delete Partner : {}", id);
		partnerService.deletePartner(id);
		return ResponseEntity.noContent().build();
	}

}
