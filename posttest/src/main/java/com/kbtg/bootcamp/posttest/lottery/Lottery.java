package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.user_ticket.UserTicket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
public class Lottery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private String ticket;

    @Setter
    private Integer price;

    @Setter
    private Integer amount;

    @OneToMany(mappedBy = "lottery")
    private List<UserTicket> tickets;


    public Lottery() {
    }
}
