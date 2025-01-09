package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditAPI;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.request.ReprogramDebtRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.service.CreditService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

@RestController
public class CreditController implements CreditAPI {

    @Autowired
    CreditService creditService;

    @Override
    @Transactional
    @CircuitBreaker(name = "creditService", fallbackMethod = "fallbackGrantCredit")
    public ApiResponse<CreditResponse> grantCredit(CreditRequest creditRequest) {
        ApiResponse<CreditResponse> response = new ApiResponse<>();
        CreditResponse creditResponse = creditService.grantCredit(creditRequest);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Credit granted successfully");
        response.setData(creditResponse);
        return response;
    }

    public ApiResponse<CreditResponse> fallbackGrantCredit(CreditRequest creditRequest, Throwable t) {
        ApiResponse<CreditResponse> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setMessage("Service is currently unavailable. Please try again later.");
        return response;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<CreditDebtResponse> checkDebt(String creditId) {
        ApiResponse<CreditDebtResponse> response = new ApiResponse<>();
        CreditDebtResponse creditDebtResponse = creditService.checkDebt(creditId);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Debt retrieved successfully");
        response.setData(creditDebtResponse);
        return response;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<CreditResponse> updateReprogramDebt(ReprogramDebtRequest reprogramDebtRequest) {
        ApiResponse<CreditResponse> response = new ApiResponse<>();
        CreditResponse creditResponse = creditService.updateReprogramDebt(reprogramDebtRequest);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Credit reprogrammed successfully");
        response.setData(creditResponse);
        return response;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<List<CreditResponse>> findAllCredits() {
        ApiResponse<List<CreditResponse>> response = new ApiResponse<>();
        List<CreditResponse> creditResponses = creditService.findAllCredits();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Credits retrieved successfully");
        response.setData(creditResponses);
        return response;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<Void> deleteCredit(String creditId) {
        ApiResponse<Void> response = new ApiResponse<>();
        creditService.deleteCredit(creditId);
        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setMessage("Credit deleted successfully");
        return response;
    }
}

