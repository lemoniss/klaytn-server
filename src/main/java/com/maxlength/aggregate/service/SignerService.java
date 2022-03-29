package com.maxlength.aggregate.service;


import com.maxlength.spec.vo.Signer;
import com.maxlength.spec.vo.Token;

public interface SignerService {

    Signer.getCurrentAgenda getCurrentAgenda(String address) throws Exception;

    Token.transactionHash agendaSign(Signer.agreeSign request) throws Exception;
}
