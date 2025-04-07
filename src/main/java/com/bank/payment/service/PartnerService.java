package com.bank.payment.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bank.payment.dto.PartnerDTO;

public interface PartnerService {
	/**
     * Get all partners.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
	Page<PartnerDTO> getAllPartners(Pageable pageable);
	
	/**
     * Get partner by id.
     *
     * @param id the id of the entity.
     * @return the list of entities.
     */
	Optional<PartnerDTO> getPartnerById(Long id);
	
	/**
     * Add a partner.
     *
     * @param partnerDTO the entity to save.
     * @return the persisted entity.
     */
	PartnerDTO addPartner(PartnerDTO partnerDTO);
	
	/**
     * Delete the "id" partner.
     *
     * @param id the id of the entity to delete.
     */
	void deletePartner(Long id);
}
