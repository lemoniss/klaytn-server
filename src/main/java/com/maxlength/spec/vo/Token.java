package com.maxlength.spec.vo;

import com.klaytn.caver.abi.TypeReference;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.methods.response.KlayLogs.Log;
import io.swagger.annotations.ApiModel;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@ApiModel(description = "Klaytn 토큰 VO")
public class Token {

    @Getter
    @Builder
    @ApiModel(description = "토큰전송 요청")
    public static class transferRequest {
        private Long accountId;
        private String to;
        private BigDecimal amount;
    }

    @Getter
    @Builder
    @ApiModel(description = "토큰전송 응답")
    public static class transactionReceipt {
        private String from;
        private String to;
        private String gasUsed;
        private List<Log> logs;
    }

    @Getter
    @Builder
    @ApiModel(description = "트랜잭션 응답")
    public static class transactionHash {
        private String txHash;
    }

    @Getter
    @Builder
    @ApiModel(description = "토큰전송 응답")
    public static class balanceOf {
        private BigDecimal amount;
    }

    @Getter
    @Builder
    @ApiModel(description = "트랜잭션 필수인수 응답")
    public static class txCountResponse {
        private BigInteger nonce;
        private BigInteger gasPrice;
        private BigInteger gasLimit;
    }

    @Getter
    @Builder
    @ApiModel(description = "트랜잭션 요청")
    public static class txRequest {
        private String address;
        private BigInteger nonce;
        private String functionName;
        private List<Type> inputParameters;
        private List<TypeReference<?>> outputParameters;
    }

}
