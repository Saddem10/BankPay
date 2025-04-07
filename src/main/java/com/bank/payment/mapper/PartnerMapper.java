package com.bank.payment.mapper;

import org.mapstruct.Mapper;

import com.bank.payment.dto.PartnerDTO;
import com.bank.payment.model.Partner;

@Mapper(componentModel = "spring")
public abstract class PartnerMapper implements EntityMapper<PartnerDTO, Partner> {
}
