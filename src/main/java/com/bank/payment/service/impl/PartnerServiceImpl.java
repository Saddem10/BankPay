package com.bank.payment.service.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bank.payment.dto.PartnerDTO;
import com.bank.payment.mapper.PartnerMapper;
import com.bank.payment.repository.PartnerRepository;
import com.bank.payment.service.PartnerService;

@Service
public class PartnerServiceImpl implements PartnerService {

	private final PartnerRepository partnerRepository;
	private final PartnerMapper partnerMapper;
	
	public PartnerServiceImpl(PartnerRepository partnerRepository, PartnerMapper partnerMapper) {
        this.partnerRepository = partnerRepository;
		this.partnerMapper = partnerMapper;
    }
	
	@Override
	@Cacheable(value = "partners", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
	public Page<PartnerDTO> getAllPartners(Pageable pageable) {
		return partnerRepository.findAll(pageable).map(partnerMapper::toDto);
	}

	@Override
	public Optional<PartnerDTO> getPartnerById(Long id) {
		return Optional.ofNullable(
		        partnerRepository.findById(id)
		            .map(partner -> partnerMapper.toDto(partner))
		            .orElse(null)
		    );
	}

	@Override
	@CacheEvict(value = "partners", allEntries = true)
	public PartnerDTO addPartner(PartnerDTO partnerDTO) {
		return partnerMapper.toDto(partnerRepository.save(partnerMapper.toEntity(partnerDTO)));
	}

	@Override
	@CacheEvict(value = "partners", allEntries = true)
	public void deletePartner(Long id) {
		partnerRepository.deleteById(id);
	}

}
