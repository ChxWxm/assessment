package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.NotFoundException;
import com.kbtg.bootcamp.posttest.lottery.response.LotteryTicketResponse;
import com.kbtg.bootcamp.posttest.user_account.UserAccount;
import com.kbtg.bootcamp.posttest.user_account.UserAccountRepository;
import com.kbtg.bootcamp.posttest.user_ticket.UserTicket;
import com.kbtg.bootcamp.posttest.user_ticket.UserTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {
    @Mock
    private LotteryRepository lotteryRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private UserTicketRepository userTicketRepository;
    private LotteryService lotteryService;

    @BeforeEach
    void setup() {
        lotteryService = new LotteryService(lotteryRepository, userAccountRepository, userTicketRepository);
    }

    @Test
    @DisplayName("get all lottery tickets should return all ticket numbers and status ok")
    void getAllLotteryTicketsReturnTicketNumbers() {
        Lottery lottery1 = new Lottery(1, "ticket1", 100, 1);
        Lottery lottery2 = new Lottery(2, "ticket2", 80, 1);
        List<Lottery> mockLotteryList = Arrays.asList(lottery1, lottery2);
        when(lotteryRepository.findAll()).thenReturn(mockLotteryList);

        ResponseEntity<Map<String, List<String>>> response = lotteryService.getLotteries();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, List<String>> expectedResponse = new HashMap<>();
        expectedResponse.put("tickets", mockLotteryList.stream().map(Lottery::getTicket).toList());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("create lottery should return ticket and status created")
    void createLotteryReturnTicket() {
        Lottery mockSavedLottery = new Lottery(1, "123456", 100, 1);
        when(lotteryRepository.save(any(Lottery.class))).thenReturn(mockSavedLottery);

        LotteryCreateRequest request = new LotteryCreateRequest("123456", 100, 1);
        ResponseEntity<LotteryTicketResponse> response = lotteryService.createLottery(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        LotteryTicketResponse expectedResponse = new LotteryTicketResponse("123456");
        assertEquals(expectedResponse.getTicket(), response.getBody().getTicket());
    }

    @Test
    @DisplayName("buy lottery should return ticket create response and status created")
    void buyLotterySuccessReturnTicketCreateResponseWithStatus201() {
        Integer mockUserId = 1;
        Integer mockTicketId = 1;
        UserAccount mockUserAccount = new UserAccount(1, 100);
        Lottery mockLottery = new Lottery(1, "123456", 100, 1);
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.of(mockUserAccount));
        when(lotteryRepository.findById(mockTicketId)).thenReturn(Optional.of(mockLottery));
        UserTicket mockUserTicket = new UserTicket(1, mockUserAccount, mockLottery);
        when(userTicketRepository.save(any(UserTicket.class))).thenReturn(mockUserTicket);

        ResponseEntity<Map<String, Integer>> response = lotteryService.buyLottery(mockUserId, mockTicketId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Integer> expectedTicketCreate = new HashMap<>();
        expectedTicketCreate.put("id", 1);
        assertEquals(expectedTicketCreate, response.getBody());
    }

    @Test
    @DisplayName("buy lottery when user does not exist should throw User not found")
    void buyLotteryUserDoesNotExist() {
        Integer mockUserId = 1;
        Integer mockTicketId = 1;
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> lotteryService.buyLottery(mockUserId, mockTicketId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("buy lottery when ticket does not exist should throw Ticket not found")
    void buyLotteryTicketDoesNotExist() {
        Integer mockUserId = 1;
        Integer mockTicketId = 1;
        UserAccount mockUserAccount = new UserAccount();
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.of(mockUserAccount));
        when(lotteryRepository.findById(mockTicketId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> lotteryService.buyLottery(mockUserId, mockTicketId));
        assertEquals("Ticket not found", exception.getMessage());
    }

    @Test
    @DisplayName("get user lotteries should return user ticket response with status ok")
    void getUserLotteriesShouldReturnUserTicketAndStatus200() {
        Integer mockUserId = 1;
        UserAccount mockUserAccount = new UserAccount(1, 100);
        Lottery lottery1 = new Lottery(1, "567890", 100, 1);
        UserTicket mockUserTicket = new UserTicket(1, mockUserAccount, lottery1);
        mockUserAccount.setTickets(List.of(mockUserTicket));
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.of(mockUserAccount));

        ResponseEntity<Map<String, Object>> response = lotteryService.getUserLotteries(mockUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("cost", 100);
        expectedResponse.put("tickets", mockUserAccount.getTickets().stream()
                .map(ticket -> ticket.getLottery().getTicket())
                .collect(Collectors.toList()));
        expectedResponse.put("count", mockUserAccount.getTickets().size());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("get user lotteries when user does not exist should throw User not found")
    void getUserLotteriesUserDoesNotExist() {
        Integer mockUserId = 1;
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> lotteryService.getUserLotteries(mockUserId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("sell lottery should return lottery id that user want to sell and status ok")
    void sellLotteryShouldReturnLotteryIdAndStatus200() {
        Integer mockUserId = 1;
        Integer mockTicketId = 2;
        UserAccount mockUserAccount = new UserAccount(mockUserId, 100);
        Lottery mockLottery = new Lottery(mockTicketId, "123456", 100, 1);
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.of(mockUserAccount));
        when(lotteryRepository.findById(mockTicketId)).thenReturn(Optional.of(mockLottery));
        UserTicket mockUserTicket = new UserTicket(1, mockUserAccount, mockLottery);
        when(userTicketRepository.findByUserIdLotteryId(mockUserId, mockTicketId)).thenReturn(List.of(mockUserTicket));

        ResponseEntity<LotteryTicketResponse> response = lotteryService.sellLottery(mockUserId, mockTicketId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(mockLottery.getTicket()).isEqualTo(response.getBody().getTicket());
    }

    @Test
    @DisplayName("sell lottery when user does not exist should throw User not found")
    void sellLotteryThrowUserNotFound() {
        Integer mockUserId = 1;
        Integer mockTicketId = 2;
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> lotteryService.sellLottery(mockUserId, mockTicketId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("sell lottery when ticket does not exist should throw Ticket not found")
    void sellLotteryThrowTicketNotFound() {
        Integer mockUserId = 1;
        UserAccount mockUserAccount = new UserAccount(mockUserId, 100);
        Integer mockTicketId = 2;
        when(userAccountRepository.findById(mockUserId)).thenReturn(Optional.of(mockUserAccount));
        when(lotteryRepository.findById(mockTicketId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> lotteryService.sellLottery(mockUserId, mockTicketId));
        assertEquals("Ticket not found", exception.getMessage());
    }
}