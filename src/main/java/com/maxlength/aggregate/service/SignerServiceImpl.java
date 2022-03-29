package com.maxlength.aggregate.service;

import com.klaytn.caver.abi.TypeReference;
import com.klaytn.caver.abi.datatypes.Address;
import com.klaytn.caver.abi.datatypes.Bool;
import com.klaytn.caver.abi.datatypes.DynamicArray;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.abi.datatypes.generated.Uint8;
import com.maxlength.component.TransUtils;
import com.maxlength.spec.vo.Signer;
import com.maxlength.spec.vo.Token;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignerServiceImpl implements SignerService {

    private TransUtils transUtils;

    public SignerServiceImpl(TransUtils transUtils) {
        this.transUtils = transUtils;
    }

    @Override
    public Signer.getCurrentAgenda getCurrentAgenda(String address) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(address);

        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<DynamicArray<Uint8>> agenda = new TypeReference<>() {};
        TypeReference<Address> removeSignerAddr = new TypeReference<>() {};
        outputParameters.add(agenda);
        outputParameters.add(removeSignerAddr);

        String functionName = "getCurrentAgenda";

        Token.txRequest txData = Token.txRequest.builder()
            .address(address)
            .nonce(txCount.getNonce())
            .functionName(functionName)
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        List<Type> res = transUtils.klayCallTx(txData);
        // TODO : 클레이튼에서 리턴을 어떻게 주는지 디버그로 확인한 후에 마저 작업하도록 한다.
        return null;
//        List<Confirm> requestConfirms = new ArrayList<>();
//
//        List<Uint8> confirmTypes = (List<Uint8>) res.get(0).getValue();
//
//        for(int i= 0; i< confirmTypes.size(); i++) {
//            BigInteger confirm = confirmTypes.get(i).getValue();
//
//            switch (confirm.intValue()) {
//                case 0: requestConfirms.add(Confirm.TRANSFER);
//                    break;
//                case 1: requestConfirms.add(Confirm.MINT);
//                    break;
//                case 2: requestConfirms.add(Confirm.BURN);
//                    break;
//                case 3: requestConfirms.add(Confirm.ADD);
//                    break;
//                case 4: requestConfirms.add(Confirm.REMOVE);
//                    break;
//            }
//        }
//        return Signer.getRequestConfirm.builder()
//            .confirmTypes(requestConfirms)
//            .removeAddress(res.get(1).getValue().toString().equals("0x0000000000000000000000000000000000000000") ? "" : res.get(1).getValue().toString())
//            .build();

    }

    @Override
    public Token.transactionHash agendaSign(Signer.agreeSign request) throws Exception {

        Token.txCountResponse txCount = transUtils.txCount(request.getAddress());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Uint8(request.getAgenda()));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        String functionName = "agendaSign";

        Token.txRequest txData = Token.txRequest.builder()
            .address(request.getAddress())
            .nonce(txCount.getNonce())
            .functionName(functionName)
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return transUtils.klaySendSignedTx(txData);
    }

}

