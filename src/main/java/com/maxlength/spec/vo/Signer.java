package com.maxlength.spec.vo;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@ApiModel(description = "Signer")
public class Signer {


    @Getter
    @ApiModel(description = "서명하기")
    public static class agreeSign {
        private String address;

        private int agenda;
    }

    @Getter
    @Builder
    @ApiModel(description = "서명요청 가져오기 ")
    public static class getCurrentAgenda {

        private int agenda;

        private String agendaDesc;
    }

}
