package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditCardsAPI;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

@RestController
public class CreditCardsController implements CreditCardsAPI {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardsController.class);

    @Autowired
    CreditCardService creditCardsService;

    /**
     * Requests a new credit card.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditCardRequest The credit card request payload
     * @return ApiResponse containing the credit card response
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditCardsService", fallbackMethod = "fallbackRequestCreditCard")
    public ApiResponse<CreditCardResponse> requestCreditCard(CreditCardRequest creditCardRequest) {
        logger.debug("Received request to create credit card: {}", creditCardRequest);
        ApiResponse<CreditCardResponse> response = new ApiResponse<>();
        CreditCardResponse creditCardResponse = creditCardsService.requestCreditCard(creditCardRequest);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Credit card created successfully");
        response.setData(creditCardResponse);
        logger.info("Credit card created successfully: {}", creditCardResponse);
        return response;
    }

    /**
     * Fallback method for requestCreditCard in case of failure.
     *
     * @param creditCardRequest The credit card request payload
     * @param t The throwable cause of the failure
     * @return ApiResponse containing the fallback response
     */
    public ApiResponse<CreditCardResponse> fallbackRequestCreditCard(CreditCardRequest creditCardRequest, Throwable t) {
        logger.error("Fallback method triggered for requestCreditCard due to: {}", t.getMessage());
        ApiResponse<CreditCardResponse> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setMessage("Service is currently unavailable. Please try again later.");
        return response;
    }

    /**
     * Checks the debt of a credit card.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditCardId The credit card ID
     * @return ApiResponse containing the credit card debt response
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditCardsService")
    public ApiResponse<CreditCardDebtResponse> checkDebt(String creditCardId) {
        logger.debug("Received request to check debt for credit card: {}", creditCardId);
        ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
        CreditCardDebtResponse creditCardDebtResponse = creditCardsService.checkDebt(creditCardId);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Check debt successfully");
        response.setData(creditCardDebtResponse);
        logger.info("Debt checked successfully for credit card: {}", creditCardId);
        return response;
    }

    /**
     * Finds all credit cards.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @return ApiResponse containing the list of credit card responses
     */
    @Transactional
    @CircuitBreaker(name = "creditCardsService")
    public ApiResponse<List<CreditCardResponse>> findAllCreditCards() {
        logger.debug("Received request to find all credit cards");
        ApiResponse<List<CreditCardResponse>> response = new ApiResponse<>();
        List<CreditCardResponse> creditCards = creditCardsService.findAllCreditCards();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Credit cards retrieved successfully");
        response.setData(creditCards);
        logger.info("All credit cards retrieved successfully");
        return response;
    }

    /**
     * Updates a credit card.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditCardId The credit card ID
     * @return ApiResponse containing the updated credit card response
     */
    @Transactional
    @CircuitBreaker(name = "creditCardsService")
    public ApiResponse<CreditCardResponse> updateCreditCard(String creditCardId) {
        logger.debug("Received request to update credit card with ID: {}", creditCardId);
        ApiResponse<CreditCardResponse> response = new ApiResponse<>();
        CreditCardResponse creditCardResponse = creditCardsService.updateCreditCard(creditCardId);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Credit card updated successfully");
        response.setData(creditCardResponse);
        logger.info("Credit card updated successfully with ID: {}", creditCardId);
        return response;
    }

    /**
     * Deletes a credit card.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditCardId The credit card ID
     * @return ApiResponse indicating the result of the delete operation
     */
    @Transactional
    @CircuitBreaker(name = "creditCardsService")
    public ApiResponse<Void> deleteCreditCard(String creditCardId) {
        logger.debug("Received request to delete credit card with ID: {}", creditCardId);
        ApiResponse<Void> response = new ApiResponse<>();
        creditCardsService.deleteCreditCard(creditCardId);
        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setMessage("Credit card deleted successfully");
        logger.info("Credit card deleted successfully with ID: {}", creditCardId);
        return response;
    }
}
