package com.kbtg.bootcamp.posttest.user_ticket;

import com.kbtg.bootcamp.posttest.lottery.LotteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserTicketController {
    private final LotteryService lotteryService;

    public UserTicketController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/{userId}/lotteries/{ticketId}/")
    public ResponseEntity<Map<String, Integer>> buyLottery(@PathVariable Integer userId, @PathVariable Integer ticketId) throws Exception {
        return lotteryService.buyLottery(userId, ticketId);
    }

    @GetMapping("/{userId}/lotteries/")
    public ResponseEntity<Map<String, Object>> getUserLotteries(@PathVariable Integer userId) {
        return lotteryService.getUserLotteries(userId);
    }

    @DeleteMapping("/{userId}/lotteries/{ticketId}/")
    public ResponseEntity<Map<String, String>> sellLottery(@PathVariable Integer userId, @PathVariable Integer ticketId) {
        return lotteryService.sellLottery(userId, ticketId);
    }
}
