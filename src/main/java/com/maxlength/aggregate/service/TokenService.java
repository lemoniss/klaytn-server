package com.maxlength.aggregate.service;


import com.maxlength.spec.vo.Token;

public interface TokenService {

    Token.transactionHash transfer(Token.transferRequest request) throws Exception;

    Token.transactionHash delegateTransfer(Token.transferRequest request) throws Exception;

    Token.balanceOf getBalanceOf(Long accountId, int scale) throws Exception;
}
