package com.maxlength.component;

import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.EventEncoder;
import com.klaytn.caver.abi.FunctionEncoder;
import com.klaytn.caver.abi.FunctionReturnDecoder;
import com.klaytn.caver.abi.TypeReference;
import com.klaytn.caver.abi.datatypes.Address;
import com.klaytn.caver.abi.datatypes.Bool;
import com.klaytn.caver.abi.datatypes.Utf8String;
import com.klaytn.caver.abi.datatypes.generated.Uint256;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.crypto.KlayCredentials;
import com.klaytn.caver.kct.kip7.KIP7;
import com.klaytn.caver.methods.request.CallObject;
import com.klaytn.caver.methods.request.KlayLogFilter;
import com.klaytn.caver.methods.response.KlayLogs;
import com.klaytn.caver.methods.response.Quantity;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.tx.type.TxType;
import com.klaytn.caver.tx.type.TxTypeLegacyTransaction;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import com.maxlength.spec.common.BaseException;
import com.maxlength.spec.vo.Event;
import com.maxlength.spec.vo.Token;
import com.maxlength.spec.vo.Wallet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.klaytn.caver.abi.Function;
import com.klaytn.caver.abi.datatypes.Type;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import okhttp3.Credentials;

@Component
public class TransUtils {

    @Value("${blockchain.klaytn.tokenContractAddress}")
    private String contractAddress;

    @Value("${blockchain.klaytn.endPointNode}")
    private String endPointNode;

    @Value("${blockchain.klaytn.accessKeyId}")
    private String accessKeyId;

    @Value("${blockchain.klaytn.sercetAccessKeyId}")
    private String sercetAccessKeyId;

    @Value("${blockchain.klaytn.corpPrivateKey}")
    private String corpPrivateKey;

    @Value("${blockchain.klaytn.chainId}")
    private String chainId;

    @Value("${blockchain.klaytn.decimals}")
    private int decimals;

    private String gasPrice = "750000000000";
    private String gasLimit = "10000000";

    /**
     * 공통 트랜잭션 필수인수
     * @param address
     * @return
     * @throws Exception
     */
    public Token.txCountResponse txCount(String address) throws Exception {

        Quantity transactionCount = caver().rpc.klay.getTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();

        return Token.txCountResponse.builder()
            .nonce(transactionCount.getValue())
            .gasPrice(new BigInteger(gasPrice))
            .gasLimit(new BigInteger(gasLimit))
            .build();
    }

    /**
     * 공통 트랜잭션 회사지갑의 nonce 구해오기
     * @return
     * @throws Exception
     */
    public Token.txCountResponse txCorpAddressCount() throws Exception {

        Quantity transactionCount = caver().rpc.klay.getTransactionCount(corpCredentials().getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

        return Token.txCountResponse.builder()
            .nonce(transactionCount.getValue())
            .gasPrice(new BigInteger(gasPrice))
            .gasLimit(new BigInteger(gasLimit))
            .build();
    }

    /**
     * 공통 트랜잭션 call
     * @param txData
     * @return
     * @throws Exception
     */
    public List<Type> klayCallTx(Token.txRequest txData) throws Exception {

        Function function = new Function(
            txData.getFunctionName(),
            txData.getInputParameters().size() > 0 ? txData.getInputParameters() : Collections.emptyList(),
            txData.getOutputParameters().size() > 0 ? txData.getOutputParameters() : Collections.emptyList()
        );

        String functionEncoder = FunctionEncoder.encode(function);

        CallObject callObject = CallObject.createCallObject(
            txData.getAddress(),
            contractAddress,
            new BigInteger(gasLimit),
            new BigInteger(gasPrice),
            BigInteger.ZERO,
            functionEncoder
        );

        Response<String> response = caver().rpc.klay.call(callObject, DefaultBlockParameterName.LATEST).sendAsync().get();

        if(response.hasError())
            throw new BaseException(response.getError().getMessage());

        return FunctionReturnDecoder.decode(response.getResult(), function.getOutputParameters());
    }

    /**
     * 공통 서명한 트랜잭션 send
     * @param txData
     * @return
     * @throws Exception
     */
    public Token.transactionHash klaySendSignedTx(Token.txRequest txData) throws Exception {

        Function function = new Function(
            txData.getFunctionName(),
            txData.getInputParameters().size() > 0 ? txData.getInputParameters() : Collections.emptyList(),
            txData.getOutputParameters().size() > 0 ? txData.getOutputParameters() : Collections.emptyList()
        );

        String functionEncoder = FunctionEncoder.encode(function);

        String rawTransaction = signTransaction(
            txData.getNonce(),
            new BigInteger(gasLimit),
            new BigInteger(gasPrice),
            contractAddress,
            BigInteger.ZERO,
            functionEncoder,
            corpCredentials()
            );

        com.klaytn.caver.methods.response.Bytes32 txHash = caver().rpc.klay.sendRawTransaction(rawTransaction).sendAsync().get();

        return Token.transactionHash.builder().txHash(txHash.getResult()).build();
    }

    /**
     * 공통 ERC20 전송 트랜잭션 send
     * @param request
     * @return
     * @throws Exception
     */
    public Token.transactionHash erc20Transfer(Token.transferRequest request, String fromAddress) throws Exception {

        Token.txCountResponse txCount = txCount(fromAddress);

        BigInteger sendBalance = multiplyTokenDecimals(request.getAmount());

        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new Address(request.getTo()));
        inputParameters.add(new Uint256(sendBalance));

        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Bool> typeReferenceBool = new TypeReference<>() {};
        outputParameters.add(typeReferenceBool);

        String functionName = "transfer";

        Token.txRequest txData = Token.txRequest.builder()
            .address(fromAddress)
            .nonce(txCount.getNonce())
            .functionName(functionName)
            .inputParameters(inputParameters)
            .outputParameters(outputParameters)
            .build();

        return klaySendSignedTx(txData);

    }

    /**
     * 공통 대납전송 트랜잭션 send
     * @param request
     * @return
     * @throws Exception
     */
    public Token.transactionHash erc20DelegateTransfer(Token.transferRequest request, String fromPrivateKey) throws Exception {

        BigInteger sendBalance = multiplyTokenDecimals(request.getAmount());

        Caver caver = caver();

        SingleKeyring sender = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        caver.wallet.add(sender);

        SingleKeyring feePayer = KeyringFactory.createFromPrivateKey(corpPrivateKey);
        caver.wallet.add(feePayer);

        SendOptions sendOptions = new SendOptions();
        sendOptions.setFrom(sender.getAddress());
        sendOptions.setGas("300000");
        sendOptions.setFeeDelegation(true);
        sendOptions.setFeePayer(feePayer.getAddress());

        KIP7 kip7 = new KIP7(caver, contractAddress);

        TransactionReceipt.TransactionReceiptData receiptData = kip7.transfer(request.getTo(), sendBalance, sendOptions);

        return Token.transactionHash.builder().txHash(receiptData.getTransactionHash()).build();
    }

    /**
     * 공통 트랜잭션 영수증 리턴
     * @param txHash
     * @return
     * @throws Exception
     */
    public Token.transactionReceipt txReceipt(String txHash) throws Exception {

        TransactionReceipt receipt = caver().rpc.klay.getTransactionReceipt(txHash).sendAsync().get();

        return Token.transactionReceipt.builder()
            .from(receipt.getResult().getFrom())
            .to(receipt.getResult().getTo())
            .gasUsed(new BigDecimal(String.valueOf(receipt.getResult().getGasUsed())).divide(new BigDecimal(10).pow(decimals)).toPlainString())
            .logs(receipt.getResult().getLogs())
            .build();
    }

    /**
     * 공통 10^18 곱하기
     * @param token
     * @return
     */
    public BigInteger multiplyTokenDecimals(BigDecimal token) {
        BigDecimal tokenDecimal = new BigDecimal(10).pow(decimals);
        BigDecimal computeToken = token.multiply(tokenDecimal);
        return computeToken.toBigInteger();
    }

    /**
     * 공통 10^18 나누기 (소수점2자리 반올림)
     * @param token
     * @param scale (소수점 n 자리)
     * @return
     */
    public BigDecimal devideTokenDecimals(BigInteger token, int scale) {
        BigDecimal tokenDecimal = new BigDecimal(10).pow(decimals);
        BigDecimal computeToken = new BigDecimal(token).divide(tokenDecimal, scale, RoundingMode.HALF_UP);
        return computeToken;
    }

    public List<Event.getEventList> klayEventList(String eventName, String contractAddress, BigInteger blockNumber) throws Exception {

        com.klaytn.caver.abi.datatypes.Event event = new com.klaytn.caver.abi.datatypes.Event(eventName,
            Arrays.asList(
                new TypeReference<Address>(true) {},
                new TypeReference<Utf8String>(false) {},
                new TypeReference<Uint256>(false) {}
            )
        );

        String eventHash = EventEncoder.encode(event);

        DefaultBlockParameter fromBlock;
        if(blockNumber == null) {
            fromBlock = DefaultBlockParameterName.EARLIEST;
        } else {
            fromBlock = DefaultBlockParameter.valueOf(blockNumber.add(BigInteger.valueOf(1)));
        }

        // Filter
//        KlayFilter filter = new KlayFilter(
//            fromBlock,
//            DefaultBlockParameterName.LATEST,
//            contractAddress)
//            .addSingleTopic(eventHash)
//            ;

        List<Event.getEventList> responseList = new ArrayList<>();

        KlayLogFilter logFilter = (KlayLogFilter) new KlayLogFilter(
            fromBlock,
            DefaultBlockParameterName.LATEST,
            contractAddress,
            null
        ).addSingleTopic(eventHash);

        // Pull all the events for this contract
        KlayLogs logs = caver().rpc.klay.getLogs(logFilter).sendAsync().get();

        // TODO : logs 값을 디버그로 확인해보자

//        for(LogResult log: logs.getLogs()) {
//            log.get()
//        }


//        public List<LogResult> getLogs() {
//            return getResult();
//        }

//         web3j().ethLogFlowable(filter).subscribe(log -> {
//            //            String eventHash = log.getTopics().get(0); // Index 0 is the event definition hash
//
//            Address fromAddress = (Address) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(1), new TypeReference<Address>() {});
//
//            List<Type> nonIndexParams = FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());
//
//            Event.getEventList res = Event.getEventList.builder()
//                .blockNumber(log.getBlockNumber())
//                .transactionHash(log.getTransactionHash())
//                .address(fromAddress.toString())
//                .contents((String) nonIndexParams.get(0).getValue())
//                .regDt(LocalDateTime.ofInstant(Instant.ofEpochSecond(new BigInteger(String.valueOf(nonIndexParams.get(1).getValue())).longValue()), TimeZone
//                    .getTimeZone("Asia/Seoul").toZoneId()))
//                .build();
//
//            responseList.add(res);
//
//        });

        return responseList;

    }

//    public Event.getEventDetail ethEventDetail(String eventName, Event.detailRequest request) {
//
//        com.klaytn.caver.abi.datatypes.Event event = new com.klaytn.caver.abi.datatypes.Event(eventName,
//            Arrays.asList(
//                new TypeReference<Address>(true) {},
//                new TypeReference<Utf8String>(false) {},
//                new TypeReference<Uint256>(false) {}
//            )
//        );
//
//        String eventHash = EventEncoder.encode(event);
//
//        // Filter
//        EthFilter filter = new EthFilter(
//            DefaultBlockParameter.valueOf(request.getBlockNumber()),
//            DefaultBlockParameter.valueOf(request.getBlockNumber()),
//            eventContractAddress)
//            .addSingleTopic(eventHash)
//            ;
//
//        AtomicReference<Event.getEventDetail> details = new AtomicReference<>();
//
//        // Pull all the events for this contract
//        web3j().ethLogFlowable(filter).subscribe(log -> {
//            //            String eventHash = log.getTopics().get(0); // Index 0 is the event definition hash
//            if(request.getTransactionHash().equals(log.getTransactionHash())) {
//                Address fromAddress = (Address) FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(1), new TypeReference<Address>() {});
//
//                List<Type> nonIndexParams = FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());
//
//                Event.getEventDetail res = Event.getEventDetail.builder()
//                    .blockNumber(log.getBlockNumber())
//                    .transactionHash(log.getTransactionHash())
//                    .address(fromAddress.toString())
//                    .contents((String) nonIndexParams.get(0).getValue())
//                    .regDt(LocalDateTime.ofInstant(Instant.ofEpochSecond(new BigInteger(String.valueOf(nonIndexParams.get(1).getValue())).longValue()), TimeZone
//                        .getTimeZone("Asia/Seoul").toZoneId()))
//                    .build();
//
//                details.set(res);
//            }
//        });
//
//        return details.get();
//    }

    /**
     *     public static class makeWallet {
     *         private String address;
     *         private String privateKey;
     * @return
     */

    public Wallet.makeWallet makeWallet() throws Exception {

        KlayCredentials credentials = KlayCredentials.create(Keys.createEcKeyPair());
        String pk = Numeric.toHexStringWithPrefix(credentials.getEcKeyPair().getPrivateKey());

        return Wallet.makeWallet.builder()
            .address(credentials.getAddress())
            .privateKey(pk)
            .build();
    }

    private Caver caver() {
        HttpService httpService = new HttpService(endPointNode);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, sercetAccessKeyId));
        httpService.addHeader("x-chain-id", chainId);
        return new Caver(httpService);
    }

    public KlayCredentials corpCredentials() {
        return KlayCredentials.create(corpPrivateKey);
    }

    private String signTransaction(
                    BigInteger nonce,
                    BigInteger gasLimit,
                    BigInteger gasPrice,
                    String to,
                    BigInteger value,
                    String data,
                    KlayCredentials credentials) {

        TxType tx = TxTypeLegacyTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        return tx.sign(credentials, Integer.parseInt(chainId)).getValueAsString();
    }
}


