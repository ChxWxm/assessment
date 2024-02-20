package com.kbtg.bootcamp.posttest.lottery.response;

import lombok.Getter;

@Getter
public class LotteryTicketResponse {
    private final String ticket;

    public LotteryTicketResponse(String ticketNumber) {
        this.ticket = ticketNumber;
    }
}
