package com.kbtg.bootcamp.posttest.user_account;

import com.kbtg.bootcamp.posttest.user_ticket.UserTicket;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user_account")
@Getter
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private List<String> roles;

    @Min(0)
    @Setter
    private Integer cost;

    @OneToMany(mappedBy = "userAccount")
    private List<UserTicket> tickets;
}

