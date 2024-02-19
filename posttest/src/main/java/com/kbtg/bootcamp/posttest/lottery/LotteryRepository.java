package com.kbtg.bootcamp.posttest.lottery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotteryRepository extends JpaRepository<Lottery, Integer> {
    @Query("SELECT lottery FROM Lottery lottery WHERE lottery.ticket LIKE %:ticket%")
    List<Lottery> findByTicketContain(String ticket);

    Boolean existsByTicket(String ticket);
}
