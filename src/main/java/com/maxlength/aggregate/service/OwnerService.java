package com.maxlength.aggregate.service;

import com.maxlength.spec.vo.Owner;
import com.maxlength.spec.vo.Token;
import java.util.List;

public interface OwnerService {

    Token.transactionHash mintBurn(Owner.mintBurn request, String functionName) throws Exception;

    Token.transactionHash  proposeAgenda(Owner.proposeAgenda request) throws Exception;

    Token.transactionHash proposeAgendaForRemoveSigner(Owner.proposeAgendaForRemoveSigner request) throws Exception;

    Token.transactionHash abandonAgenda(Owner.address request) throws Exception;

    Token.transactionHash target(Owner.target request, String functionName) throws Exception;

    Owner.getCurrentAgendaAndSigners getCurrentAgendaAndSigners(String ownerAddress) throws Exception;

    List<Owner.getSigners> getAllSigners(String ownerAddress) throws Exception;

}
