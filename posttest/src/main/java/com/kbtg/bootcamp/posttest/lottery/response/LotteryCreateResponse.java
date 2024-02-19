package com.kbtg.bootcamp.posttest.lottery.response;

import lombok.Getter;

@Getter
public class LotteryCreateResponse {
    private final String ticket;

    public LotteryCreateResponse(String ticketNumber) {
        this.ticket = ticketNumber;
    }
}
