package com.nttdata.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.response.DebtResponse;

@RestController
@RequestMapping("/credit-cards")
public interface CreditCardsAPI {

    @PostMapping("/request")
    ApiResponse<CreditCardResponse> requestCreditCard(@RequestBody CreditCardRequest creditCardRequest);

    @GetMapping("/{creditCardId}")
    ApiResponse<DebtResponse> checkDebt(@PathVariable int creditCardId);

}
