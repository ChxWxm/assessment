package com.kbtg.bootcamp.posttest.user_ticket;

import com.kbtg.bootcamp.posttest.lottery.Lottery;
import com.kbtg.bootcamp.posttest.user_account.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_ticket")
public class UserTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Setter
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "lottery_id")
    @Getter
    @Setter
    private Lottery lottery;
}
