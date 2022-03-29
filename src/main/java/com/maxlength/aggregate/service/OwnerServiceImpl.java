package com.maxlength.aggregate.service;

import com.klaytn.caver.abi.TypeReference;
import com.klaytn.caver.abi.datatypes.Address;
import com.klaytn.caver.abi.datatypes.Bool;
import com.klaytn.caver.abi.datatypes.DynamicArray;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.abi.datatypes.generated.Uint256;
import com.klaytn.caver.abi.datatypes.generated.Uint8;
import com.maxlength.component.TransUtils;
import com.maxlength.spec.vo.Owner;
import com.maxlength.spec.vo.Token;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnerServiceImpl implements OwnerService {

    private TransUtils transUtils;

    public OwnerServiceImpl(TransUtils transUtils) {
        this.transUtils = transUtils;
    }

    @Override
    public Token.transactionHash mintBurn(Owner.mintBurn request, String functionName) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(request.getOwnerAddress());
        BigInteger sendBalance = transUtils.multiplyTokenDecimals(request.getAmount());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(request.getTargetAddress()));
        inputParameters.add(new Uint256(sendBalance));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        Token.txRequest txData = Token.txRequest.builder()
            .address(request.getOwnerAddress())
            .nonce(txCount.getNonce())
            .functionName(functionName)
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return transUtils.klaySendSignedTx(txData);
    }

    @Override
    public Token.transactionHash proposeAgenda(Owner.proposeAgenda request) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(request.getOwnerAddress());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Uint8(request.getAgenda()));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        Token.txRequest txData = Token.txRequest.builder()
            .address(request.getOwnerAddress())
            .nonce(txCount.getNonce())
            .functionName("proposeAgenda")
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return transUtils.klaySendSignedTx(txData);
    }

    @Override
    public Token.transactionHash proposeAgendaForRemoveSigner(Owner.proposeAgendaForRemoveSigner request) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(request.getOwnerAddress());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Uint8(request.getAgenda()));
        inputParameters.add(new Address(request.getRemoveSignerAddress()));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        Token.txRequest txData = Token.txRequest.builder()
            .address(request.getOwnerAddress())
            .nonce(txCount.getNonce())
            .functionName("proposeAgendaForRemoveSigner")
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return transUtils.klaySendSignedTx(txData);
    }

    @Override
    public Token.transactionHash abandonAgenda(Owner.address request) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(request.getOwnerAddress());

        List<Type> inputParameters = new ArrayList<>();

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        Token.txRequest txData = Token.txRequest.builder()
            .address(request.getOwnerAddress())
            .nonce(txCount.getNonce())
            .functionName("abandonAgenda")
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return transUtils.klaySendSignedTx(txData);
    }

    @Override
    public Token.transactionHash target(Owner.target request, String functionName) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(request.getOwnerAddress());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(request.getTargetAddress()));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        Token.txRequest txData = Token.txRequest.builder()
            .address(request.getOwnerAddress())
            .nonce(txCount.getNonce())
            .functionName(functionName)
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return transUtils.klaySendSignedTx(txData);
    }

    @Override
    public Owner.getCurrentAgendaAndSigners getCurrentAgendaAndSigners(String ownerAddress) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(ownerAddress);

        List<Type> inputParameters = new ArrayList<>();

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Uint8> agenda = new TypeReference<>() {};
        TypeReference<DynamicArray<Address>> agreeSignerAddressList = new TypeReference<>() {};
        TypeReference<Address> removeSignerAddr = new TypeReference<>() {};
        outputParameters.add(agenda);
        outputParameters.add(agreeSignerAddressList);
        outputParameters.add(removeSignerAddr);

        Token.txRequest txData = Token.txRequest.builder()
            .address(ownerAddress)
            .nonce(txCount.getNonce())
            .functionName("getCurrentAgendaAndSigners")
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        List<Type> res = transUtils.klayCallTx(txData);

        Owner.getCurrentAgendaAndSigners response = new Owner.getCurrentAgendaAndSigners();

        // TODO : 클레이튼에서 리턴을 어떻게 주는지 디버그로 확인한 후에 마저 작업하도록 한다.
        return null;

//        for(int i= 0; i< res.size(); i++) {
//
//            for(Address ad: (List<Address>) res.get(i).getValue()) {
//                Owner.getSigners obj = Owner.getSigners.builder()
//                    .signerAddress(ad.getValue())
//                    .build();
//                responseList.add(obj);
//            }
//        }
//
//        return responseList;
    }

    @Override
    public List<Owner.getSigners> getAllSigners(String ownerAddress) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(ownerAddress);

        List<Type> inputParameters = new ArrayList<>();

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<DynamicArray<Address>> typeReference = new TypeReference<>() {};
        outputParameters.add(typeReference);

        Token.txRequest txData = Token.txRequest.builder()
            .address(ownerAddress)
            .nonce(txCount.getNonce())
            .functionName("getAllSigners")
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        List<Type> res = transUtils.klayCallTx(txData);

        List<Owner.getSigners> responseList = new ArrayList<>();

        for(int i= 0; i< res.size(); i++) {

            for(Address ad: (List<Address>) res.get(i).getValue()) {
                Owner.getSigners obj = Owner.getSigners.builder()
                    .signerAddress(ad.getValue())
                    .build();
                responseList.add(obj);
            }
        }

        return responseList;
    }

}

