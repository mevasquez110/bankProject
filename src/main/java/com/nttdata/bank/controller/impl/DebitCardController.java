package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.DebitCardAPI;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.DebitCardResponse;
import com.nttdata.bank.service.DebitCardService;

/**
 * DebitCardController is a REST controller that implements the DebitCardAPI 
 * interface. This class handles HTTP requests related to debit card operations 
 * such as creating, finding, associating accounts, and deleting debit cards. 
 * It delegates the actual business logic to DebitCardService.
 */

@RestController
public class DebitCardController implements DebitCardAPI {

    private static final Logger logger = LoggerFactory.getLogger(DebitCardController.class);

    @Autowired
    DebitCardService debitCardService;

    /**
     * Creates a new debit card based on the provided DebitCardRequest object.
     *
     * @param debitCardRequest - The debit card details provided in the request body.
     * @return ApiResponse containing the created DebitCardResponse.
     */
    @Override
    public ApiResponse<DebitCardResponse> createDebitCard(@Valid DebitCardRequest debitCardRequest) {
        logger.debug("Received request to create debit card: {}", debitCardRequest);
        ApiResponse<DebitCardResponse> response = new ApiResponse<>();
        DebitCardResponse debitCardResponse = debitCardService.createDebitCard(debitCardRequest);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Debit card successfully created.");
        response.setData(debitCardResponse);
        logger.info("Debit card created successfully: {}", debitCardResponse);
        return response;
    }

    /**
     * Retrieves a list of all debit cards.
     *
     * @return ApiResponse containing a list of DebitCardResponse objects.
     */
    @Override
    public ApiResponse<List<DebitCardResponse>> findAllDebitCard() {
        logger.debug("Received request to find all debit cards.");
        ApiResponse<List<DebitCardResponse>> response = new ApiResponse<>();
        List<DebitCardResponse> debitCards = debitCardService.findAllDebitCard();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Debit cards retrieved successfully.");
        response.setData(debitCards);
        logger.info("All debit cards retrieved successfully.");
        return response;
    }

    /**
     * Associates an account with the specified debit card based on the provided
     * AssociateAccountRequest object.
     *
     * @param debitCardNumber - The debit card number to be associated with an account.
     * @param associateAccountRequest - The association details provided in the request body.
     * @return ApiResponse containing the updated DebitCardResponse.
     */
    @Override
    public ApiResponse<DebitCardResponse> associateAccount(String debitCardNumber,
            @Valid AssociateAccountRequest associateAccountRequest) {
        logger.debug("Received request to associate account with debit card: {}", debitCardNumber);
        ApiResponse<DebitCardResponse> response = new ApiResponse<>();
        DebitCardResponse debitCardResponse = debitCardService.associateAccount(debitCardNumber, associateAccountRequest);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Account successfully associated.");
        response.setData(debitCardResponse);
        logger.info("Account associated successfully with debit card: {}", debitCardNumber);
        return response;
    }

    /**
     * Deletes the specified debit card based on the debit card number.
     *
     * @param debitCardNumber - The debit card number to be deleted.
     * @return ApiResponse with a status message upon successful deletion.
     */
    @Override
    public ApiResponse<Void> deleteDebitCard(String debitCardNumber) {
        logger.debug("Received request to delete debit card with number: {}", debitCardNumber);
        ApiResponse<Void> response = new ApiResponse<>();
        debitCardService.deleteDebitCard(debitCardNumber);
        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setMessage("Debit card successfully deleted.");
        logger.info("Debit card deleted successfully with number: {}", debitCardNumber);
        return response;
    }
}
