package com.maxlength.aggregate.service;

import com.klaytn.caver.abi.TypeReference;
import com.klaytn.caver.abi.datatypes.Address;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.abi.datatypes.generated.Uint256;
import com.maxlength.aggregate.entity.AccountEntity;
import com.maxlength.aggregate.repository.AccountRepository;
import com.maxlength.component.TransUtils;
import com.maxlength.spec.common.BaseException;
import com.maxlength.spec.vo.Token;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private TransUtils transUtils;

    private AccountRepository accountRepository;

    public TokenServiceImpl(TransUtils transUtils, AccountRepository accountRepository) {
        this.transUtils = transUtils;
        this.accountRepository = accountRepository;
    }

    @Override
    public Token.transactionHash transfer(Token.transferRequest request) throws Exception {

        Optional<AccountEntity> accountEntity = accountRepository.findById(request.getAccountId());

        if(accountEntity.isEmpty())
            throw new BaseException("accountId not found");

        if(accountEntity.get().getUserEntity().getWalletEntity() == null)
            throw new BaseException("wallet not found");


        return transUtils.erc20Transfer(request, accountEntity.get().getUserEntity().getWalletEntity().getAddress());
    }

    @Override
    public Token.transactionHash delegateTransfer(Token.transferRequest request) throws Exception {

        Optional<AccountEntity> accountEntity = accountRepository.findById(request.getAccountId());

        if(accountEntity.isEmpty())
            throw new BaseException("accountId not found");

        if(accountEntity.get().getUserEntity().getWalletEntity() == null)
            throw new BaseException("wallet not found");


        return transUtils.erc20DelegateTransfer(request, accountEntity.get().getUserEntity().getWalletEntity().getPrivateKey());
    }

    @Override
    public Token.balanceOf getBalanceOf(Long accountId, int scale) throws Exception {

        Optional<AccountEntity> accountEntity = accountRepository.findById(accountId);

        if(accountEntity.isEmpty())
            throw new BaseException("accountId not found");

        if(accountEntity.get().getUserEntity().getWalletEntity() == null)
            throw new BaseException("wallet not found");

        String address = accountEntity.get().getUserEntity().getWalletEntity().getAddress();

        Token.txCountResponse txCount = transUtils.txCount(address);

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(address));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Uint256> typeReferenceUint256 = new TypeReference<>() {};
        outputParameters.add(typeReferenceUint256);

        String functionName = "balanceOf";

        Token.txRequest txData = Token.txRequest.builder()
            .address(address)
            .nonce(txCount.getNonce())
            .functionName(functionName)
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        List<Type> res = transUtils.klayCallTx(txData);

        return Token.balanceOf.builder()
            .amount(transUtils.devideTokenDecimals((BigInteger) res.get(0).getValue(), scale))
            .build();
    }

}

