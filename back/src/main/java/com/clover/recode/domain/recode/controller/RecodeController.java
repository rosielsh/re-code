package com.clover.recode.domain.recode.controller;

import com.clover.recode.domain.recode.service.RecodeService;
import com.clover.recode.domain.statistics.service.StatisticsService;
import com.clover.recode.global.result.ResultCode;
import com.clover.recode.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "레코드 API")
@RequestMapping("/problems/recode")
@RequiredArgsConstructor
public class RecodeController {

    private final RecodeService recodeService;

    @Operation(summary = "요청한 코드의 레코드를 가져옵니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{codeId}")
    public ResponseEntity<ResultResponse> getRecode(@PathVariable("codeId") int codeId) {
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_Recode_SUCCESS, recodeService.getRecode(codeId)));
    }

    @Operation(summary = "요청한 코드의 레코드 해결 카운트를 증가시킵니다.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{codeId}")
    public ResponseEntity<ResultResponse> addRecodeCount(@PathVariable("codeId") int codeId) {
        recodeService.addRecodeCount(codeId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PUT_Recode_SUCCESS));
    }

}
