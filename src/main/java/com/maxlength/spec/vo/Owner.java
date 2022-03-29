package com.maxlength.spec.vo;

import io.swagger.annotations.ApiModel;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@ApiModel(description = "Owner")
public class Owner {


    @Getter
    @Builder
    @ApiModel(description = "서명자 지갑주소 목록")
    public static class getSigners {
        private String signerAddress;
    }

    @Getter
    @Setter
    @ApiModel(description = "서명안건, 안건에 서명한 서명자목록 (삭제예정자가 있으면 그것도 가져옴)")
    public static class getCurrentAgendaAndSigners {

        private int agenda;

        private List<String> agreeSignerAddressList;

        private String removeSignerAddress;
    }

    @Getter
    @ApiModel(description = "서명 안건 발의 (서명자삭제 제외)")
    public static class proposeAgenda {
        private String ownerAddress;

        private int agenda;
    }

    @Getter
    @ApiModel(description = "서명 안건 발의 (서명자삭제 전용)")
    public static class proposeAgendaForRemoveSigner {
        private String ownerAddress;

        private int agenda;

        private String removeSignerAddress;
    }

    @Getter
    @ApiModel(description = "owner 주소")
    public static class address {
        private String ownerAddress;
    }


    @Getter
    @ApiModel(description = "owner주소와 대상주소")
    public static class target {
        private String ownerAddress;

        private String targetAddress;
    }

    @Getter
    @ApiModel(description = "토큰 발행/소각")
    public static class mintBurn {
        private String ownerAddress;

        private String targetAddress;

        private BigDecimal amount;
    }

}
