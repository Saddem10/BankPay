package com.bank.payment.mapper;

import org.mapstruct.Mapper;

import com.bank.payment.dto.MessageDTO;
import com.bank.payment.model.Message;

@Mapper(componentModel = "spring")
public abstract class MessageMapper implements EntityMapper<MessageDTO, Message> {}
