package com.nttdata.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;

@RestController
@RequestMapping("/credit-cards")
public interface CreditCardsAPI {

    @PostMapping("/request")
    ApiResponse<CreditCardResponse> requestCreditCard(@RequestParam String customerId);

    @GetMapping("/{creditCardId}")
    ApiResponse<CreditCardDebtResponse> checkDebt(@PathVariable String creditCardId);

}
