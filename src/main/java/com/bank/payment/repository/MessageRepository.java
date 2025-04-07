package com.bank.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.payment.model.Message;

@Repository
public interface MessageRepository  extends JpaRepository<Message, Long>{

}
