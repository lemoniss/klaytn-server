package com.maxlength.aggregate.service;

import com.klaytn.caver.abi.TypeReference;
import com.klaytn.caver.abi.datatypes.Address;
import com.klaytn.caver.abi.datatypes.Bool;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.abi.datatypes.Utf8String;
import com.maxlength.component.TransUtils;
import com.maxlength.spec.vo.Event;
import com.maxlength.spec.vo.Token;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private TransUtils transUtils;

    public EventServiceImpl(TransUtils transUtils) {
        this.transUtils = transUtils;
    }

    @Override
    public Mono<Token.transactionHash> write(Event.writeRequest request, String contractAddress) throws Exception {

        // 기록 트랜잭션 수수료를 대납자가 지불하도록 하려면 properties에 있는 대납자 주소로 nonce를 구하고 대납자 주소로 send하면 된다.
        // 지금은 API 호출한 자가 트랜잭셕 수수료를 지불한다

        Token.txCountResponse txCount = transUtils.txCount(transUtils.corpCredentials().getAddress());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(transUtils.corpCredentials().getAddress()));
        inputParameters.add(new Utf8String(request.getContents()));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        Token.txRequest txData = Token.txRequest.builder()
            .address(transUtils.corpCredentials().getAddress())
            .nonce(txCount.getNonce())
            .functionName(request.getFunctionName())
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return Mono.just(transUtils.klaySendSignedTx(txData));
    }

    @Override
    public Token.transactionReceipt receipt(String txHash) throws Exception {
        return transUtils.txReceipt(txHash);
    }

    @Override
    public List<Event.getEventList> list(String eventName, String contractAddress, BigInteger blockNumber) {
//        return transUtils.ethEventList(eventName, contractAddress, blockNumber);
        return null;
    }

    @Override
    public Event.getEventDetail detail(Event.detailRequest request) {

        String eventName = "EnegyUseEvent";

//        return transUtils.klayEventDetail(eventName, request);
        return null;
    }

}

