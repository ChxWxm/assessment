package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.lottery.response.LotteryCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping("/lotteries")
    public ResponseEntity<Map<String, List<String>>> getLotteries() {
        return this.lotteryService.getLotteries();
    }

    @PostMapping("admin/lotteries")
    public ResponseEntity<LotteryCreateResponse> createLottery(@RequestBody @Validated LotteryCreateRequest request) throws Exception {
        return this.lotteryService.createLottery(request);
    }
}

