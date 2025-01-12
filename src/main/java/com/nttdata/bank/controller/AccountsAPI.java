package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;
import java.util.List;

/**
 * AccountsAPI defines the RESTful endpoints for account-related operations.
 * This interface includes methods for creating, retrieving, updating, 
 * and deleting accounts, as well as checking account balance. 
 * Each method maps to an HTTP request and returns a structured API response.
 */

@RestController
@RequestMapping("/account")
public interface AccountsAPI {

    /**
     * Creates a new account based on the provided AccountRequest object.
     * @param accountRequest - The account details provided in the request body.
     * @return ApiResponse containing the created AccountResponse.
     */
    @PostMapping("/create")
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest);

    /**
     * Checks the balance of the specified account.
     * @param accountNumber - The account number for which the balance is to be checked.
     * @return ApiResponse containing the BalanceResponse.
     */
    @GetMapping("/balance")
    ApiResponse<BalanceResponse> checkBalance(@RequestParam String accountNumber);

    /**
     * Retrieves a list of all accounts.
     * @return ApiResponse containing a list of AccountResponse objects.
     */
    @GetMapping("/all")
    ApiResponse<List<AccountResponse>> findAllAccounts();

    /**
     * Updates the specified account based on the account ID.
     * @param accountNumber - The account number to be updated.
     * @return ApiResponse containing the updated AccountResponse.
     */
    @PutMapping("/update/{accountNumber}")
    ApiResponse<AccountResponse> updateAccountAllowWithdrawals(@PathVariable String accountNumber);

    /**
     * Deletes the specified account based on the account ID.
     * @param accountNumber - The account number to be deleted.
     * @return ApiResponse with no content upon successful deletion.
     */
    @DeleteMapping("/delete/{accountNumber}")
    ApiResponse<Void> deleteAccount(@PathVariable String accountNumber);
}
