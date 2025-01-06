package com.nttdata.bank.request;

import lombok.Data;

@Data
public class CreditRequest {

    private int creditId;
    private int customerId;
    private double amount;
    private String accountNumber;

}
