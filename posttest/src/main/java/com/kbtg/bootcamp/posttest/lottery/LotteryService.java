package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.NotFoundException;
import com.kbtg.bootcamp.posttest.lottery.response.LotteryTicketResponse;
import com.kbtg.bootcamp.posttest.user_account.UserAccount;
import com.kbtg.bootcamp.posttest.user_account.UserAccountRepository;
import com.kbtg.bootcamp.posttest.user_ticket.UserTicket;
import com.kbtg.bootcamp.posttest.user_ticket.UserTicketRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LotteryService {
    private final LotteryRepository lotteryRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserTicketRepository userTicketRepository;

    public LotteryService(LotteryRepository lotteryRepository, UserAccountRepository userAccountRepository, UserTicketRepository userTicketRepository) {
        this.lotteryRepository = lotteryRepository;
        this.userAccountRepository = userAccountRepository;
        this.userTicketRepository = userTicketRepository;
    }

    public ResponseEntity<Map<String, List<String>>> getLotteries() {
        List<String> tickets = lotteryRepository.findAll()
                .stream()
                .map(Lottery::getTicket)
                .collect(Collectors.toList());

        Map<String, List<String>> ticketListResponse = new HashMap<>();
        ticketListResponse.put("tickets", tickets);

        return ResponseEntity.ok().body(ticketListResponse);
    }

    public ResponseEntity<LotteryTicketResponse> createLottery(LotteryCreateRequest request) {
        Lottery lottery = new Lottery();
        lottery.setTicket(request.ticket());
        lottery.setPrice(request.price());
        lottery.setAmount(request.amount());

        Lottery savedLottery = lotteryRepository.save(lottery);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LotteryTicketResponse(savedLottery.getTicket()));
    }

    @Transactional
    public ResponseEntity<Map<String, Integer>> buyLottery(Integer userId, Integer ticketId) {
        UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Lottery lottery = lotteryRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));

        UserTicket userTicket = new UserTicket();
        userTicket.setUserAccount(userAccount);
        userTicket.setLottery(lottery);

        UserTicket saveUserTicket = userTicketRepository.save(userTicket);

        Integer totalPrice = userAccount.getCost() + saveUserTicket.getLottery().getPrice();
        userAccount.setCost(totalPrice);
        userAccountRepository.save(userAccount);

        Integer amount = lottery.getAmount() - 1;
        lottery.setAmount(amount);
        lotteryRepository.save(lottery);

        Map<String, Integer> ticketCreateResponse = new HashMap<>();
        ticketCreateResponse.put("id", saveUserTicket.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCreateResponse);
    }

    public ResponseEntity<Map<String, Object>> getUserLotteries(Integer userId) {
        UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Integer totalPrice = userAccount.getCost();
        List<String> userTickets = userAccount.getTickets()
                .stream()
                .map(ticket -> ticket.getLottery().getTicket())
                .toList();

        Map<String, Object> userTicketResponse = new HashMap<>();
        userTicketResponse.put("cost", totalPrice);
        userTicketResponse.put("tickets", userTickets);
        userTicketResponse.put("count", userTickets.size());

        return ResponseEntity.ok().body(userTicketResponse);
    }

    @Transactional
    public ResponseEntity<LotteryTicketResponse> sellLottery(Integer userId, Integer ticketId) {
        UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Lottery lottery = lotteryRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));
        Optional<UserTicket> selectedUserTicket = userTicketRepository.findByUserIdLotteryId(userAccount.getId(), lottery.getId()).stream().findFirst();

        if (selectedUserTicket.isEmpty()) {
            throw new NotFoundException("This user does not have ticket id: " + ticketId);
        } else {
            userTicketRepository.deleteById(selectedUserTicket.get().getId());

            Integer totalPrice = userAccount.getCost() - selectedUserTicket.get().getLottery().getPrice();
            userAccount.setCost(totalPrice);
            userAccountRepository.save(userAccount);

            return ResponseEntity.ok().body(new LotteryTicketResponse(selectedUserTicket.get().getLottery().getTicket()));
        }
    }
}

record LotteryCreateRequest(
        @NotNull(message = "Ticket number cannot be null")
        @Pattern(regexp = "^\\d{6}$", message = "Ticket must be a 6-digit number")
        String ticket,
        @Min(value = 0, message = "Price must be 0 or greater than 0")
        Integer price,
        @Min(value = 0, message = "Amount must be 0 or greater than 0")
        @Max(value = 100, message = "Amount must be under or equal to 100")
        Integer amount) {
}
