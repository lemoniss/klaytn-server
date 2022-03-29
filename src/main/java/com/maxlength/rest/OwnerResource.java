package com.maxlength.rest;

import com.maxlength.aggregate.service.OwnerService;
import com.maxlength.spec.common.BaseResponse;
import com.maxlength.spec.vo.Owner;
import com.maxlength.spec.vo.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/klaytn/contracts/owners")
@Api(tags = "[블록체인] - 관리자 API", protocols = "http", produces = "application/json", consumes = "appliction/json")
public class OwnerResource {

    private final OwnerService ownerService;

    public OwnerResource(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    /**
     * 토큰 발행
     * @param request
     * @return
     */
    @PostMapping("/mint")
    @ApiOperation(value = "토큰 발행", notes = "토큰 발행")
    public ResponseEntity<Token.transactionHash> mint(@RequestBody Owner.mintBurn request) throws Exception {

        return BaseResponse.ok(ownerService.mintBurn(request, "mint"));
    }

    /**
     * 토큰 소각
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/burn")
    @ApiOperation(value = "토큰 소각", notes = "토큰 소각")
    public ResponseEntity<Token.transactionHash> burn(@RequestBody Owner.mintBurn request) throws Exception {

        return BaseResponse.ok(ownerService.mintBurn(request, "burn"));
    }

    /**
     * 서명 안건 발의 (서명자삭제 제외)
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/proposeAgenda")
    @ApiOperation(value = "서명 안건 발의 (서명자삭제 제외)", notes = "서명 안건 발의 (서명자삭제 제외)")
    public ResponseEntity<Token.transactionHash> proposeAgenda(@RequestBody Owner.proposeAgenda request) throws Exception {

        return BaseResponse.ok(ownerService.proposeAgenda(request));
    }

    /**
     * 서명 안건 발의 (서명자삭제 제외 전용)
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/proposeAgendaForRemoveSigner")
    @ApiOperation(value = "서명 안건 발의 (서명자삭제 제외 전용)", notes = "서명 안건 발의 (서명자삭제 제외 전용)")
    public ResponseEntity<Token.transactionHash> proposeAgendaForRemoveSigner(@RequestBody Owner.proposeAgendaForRemoveSigner request) throws Exception {

        return BaseResponse.ok(ownerService.proposeAgendaForRemoveSigner(request));
    }

    /**
     * owner가 서명 안건 폐기
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/abandonAgenda")
    @ApiOperation(value = "owner가 서명 안건 폐기", notes = "owner가 서명 안건 폐기")
    public ResponseEntity<Token.transactionHash> proposeAgendaForRemoveSigner(@RequestBody Owner.address request) throws Exception {

        return BaseResponse.ok(ownerService.abandonAgenda(request));
    }


    /**
     * 서명자 출가
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/addSigner")
    @ApiOperation(value = "서명자 추가", notes = "서명자 추가")
    public ResponseEntity<Token.transactionHash> addSigner(@RequestBody Owner.target request) throws Exception {

        return BaseResponse.ok(ownerService.target(request, "addSigner"));
    }

    /**
     * 서명자 제거
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/removeSigner")
    @ApiOperation(value = "서명자 제거", notes = "서명자 제거")
    public ResponseEntity<Token.transactionHash> removeSigner(@RequestBody Owner.target request) throws Exception {

        return BaseResponse.ok(ownerService.target(request, "removeSigner"));
    }

    /**
     * owner 위임
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/transferOwnership")
    @ApiOperation(value = "owner 위임", notes = "owner 위임")
    public ResponseEntity<Token.transactionHash> transferOwnership(@RequestBody Owner.target request) throws Exception {

        return BaseResponse.ok(ownerService.target(request, "transferOwnership"));
    }

    /**
     * 현재안건과 안건에 서명한 서명자목록 가져오기 (안건이 삭제인 경우 삭제예정인 서명자 주소도 포함)
     * @param ownerAddress
     * @return
     */
    @GetMapping("/currentAgendaAndSigners")
    @ApiOperation(value = "전체 서명자목록 가져오기", notes = "전체 서명자목록 가져오기")
    public ResponseEntity<Owner.getCurrentAgendaAndSigners> getCurrentAgendaAndSigners(
        @ApiParam(value = "owner 지갑주소", required = true) @RequestParam(name= "address") String ownerAddress) throws Exception {

        return BaseResponse.ok(ownerService.getCurrentAgendaAndSigners(ownerAddress));
    }

    /**
     * 전체 서명자목록 가져오기
     * @param ownerAddress
     * @return
     */
    @GetMapping("/allSigners")
    @ApiOperation(value = "전체 서명자목록 가져오기", notes = "전체 서명자목록 가져오기")
    public ResponseEntity<List<Owner.getSigners>> getSigners(
        @ApiParam(value = "owner 지갑주소", required = true) @RequestParam(name= "address") String ownerAddress) throws Exception {

        return BaseResponse.ok(ownerService.getAllSigners(ownerAddress));
    }


}
