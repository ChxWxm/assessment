package com.kbtg.bootcamp.posttest.user_ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTicketRepository extends JpaRepository<UserTicket, Integer> {
    @Query("SELECT ut FROM UserTicket ut WHERE ut.lottery.id = :lotteryId AND ut.userAccount.id = :userAccountId")
    List<UserTicket> findByUserIdLotteryId(Integer userAccountId, Integer lotteryId);
}
