package com.bank.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.payment.model.Partner;

@Repository
public interface PartnerRepository  extends JpaRepository<Partner, Long>{

}
