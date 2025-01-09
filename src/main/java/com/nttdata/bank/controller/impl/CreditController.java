package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    CreditService creditService;

    /**
     * Grants a new credit.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditRequest The credit request payload
     * @return ApiResponse containing the credit response
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditService", fallbackMethod = "fallbackGrantCredit")
    public ApiResponse<CreditResponse> grantCredit(CreditRequest creditRequest) {
        logger.debug("Received request to grant credit: {}", creditRequest);
        ApiResponse<CreditResponse> response = new ApiResponse<>();
        CreditResponse creditResponse = creditService.grantCredit(creditRequest);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Credit granted successfully");
        response.setData(creditResponse);
        logger.info("Credit granted successfully: {}", creditResponse);
        return response;
    }

    /**
     * Fallback method for grantCredit in case of failure.
     *
     * @param creditRequest The credit request payload
     * @param t The throwable cause of the failure
     * @return ApiResponse containing the fallback response
     */
    public ApiResponse<CreditResponse> fallbackGrantCredit(CreditRequest creditRequest, Throwable t) {
        logger.error("Fallback method triggered for grantCredit due to: {}", t.getMessage());
        ApiResponse<CreditResponse> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setMessage("Service is currently unavailable. Please try again later.");
        return response;
    }

    /**
     * Checks the debt of a credit.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditId The credit ID
     * @return ApiResponse containing the credit debt response
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<CreditDebtResponse> checkDebt(String creditId) {
        logger.debug("Received request to check debt for credit: {}", creditId);
        ApiResponse<CreditDebtResponse> response = new ApiResponse<>();
        CreditDebtResponse creditDebtResponse = creditService.checkDebt(creditId);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Debt retrieved successfully");
        response.setData(creditDebtResponse);
        logger.info("Debt retrieved successfully for credit: {}", creditId);
        return response;
    }

    /**
     * Reprograms the debt of a credit.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param reprogramDebtRequest The reprogram debt request payload
     * @return ApiResponse containing the reprogrammed credit response
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<CreditResponse> updateReprogramDebt(ReprogramDebtRequest reprogramDebtRequest) {
        logger.debug("Received request to reprogram debt: {}", reprogramDebtRequest);
        ApiResponse<CreditResponse> response = new ApiResponse<>();
        CreditResponse creditResponse = creditService.updateReprogramDebt(reprogramDebtRequest);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Credit reprogrammed successfully");
        response.setData(creditResponse);
        logger.info("Debt reprogrammed successfully: {}", creditResponse);
        return response;
    }

    /**
     * Finds all credits.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @return ApiResponse containing the list of credit responses
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<List<CreditResponse>> findAllCredits() {
        logger.debug("Received request to find all credits");
        ApiResponse<List<CreditResponse>> response = new ApiResponse<>();
        List<CreditResponse> creditResponses = creditService.findAllCredits();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Credits retrieved successfully");
        response.setData(creditResponses);
        logger.info("All credits retrieved successfully");
        return response;
    }

    /**
     * Deletes a credit.
     * This method is transactional and uses a circuit breaker for resilience.
     *
     * @param creditId The credit ID
     * @return ApiResponse indicating the result of the delete operation
     */
    @Override
    @Transactional
    @CircuitBreaker(name = "creditService")
    public ApiResponse<Void> deleteCredit(String creditId) {
        logger.debug("Received request to delete credit with ID: {}", creditId);
        ApiResponse<Void> response = new ApiResponse<>();
        creditService.deleteCredit(creditId);
        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setMessage("Credit deleted successfully");
        logger.info("Credit deleted successfully with ID: {}", creditId);
        return response;
    }
}
