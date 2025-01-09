package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import java.util.List;

@RestController
@RequestMapping("/credit-cards")
public interface CreditCardsAPI {

    @PostMapping("/request")
    ApiResponse<CreditCardResponse> requestCreditCard(@RequestBody @Valid CreditCardRequest creditCardRequest);

    @GetMapping("/{creditCardId}")
    ApiResponse<CreditCardDebtResponse> checkDebt(@PathVariable String creditCardId);

    @GetMapping("/all")
    ApiResponse<List<CreditCardResponse>> findAllCreditCards();

    @PutMapping("/update/{creditCardId}")
    ApiResponse<CreditCardResponse> updateCreditCard(@PathVariable String creditCardId);

    @DeleteMapping("/delete/{creditCardId}")
    ApiResponse<Void> deleteCreditCard(@PathVariable String creditCardId);
}
