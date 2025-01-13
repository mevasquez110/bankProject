package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.DebitCardAPI;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.DebitCardResponse;

@RestController
public class DebitCardController implements DebitCardAPI {

	@Override
	public ApiResponse<DebitCardResponse> createDebitCard(@Valid DebitCardRequest debitCardRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<List<DebitCardResponse>> findAllDebitCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<DebitCardResponse> update(String documentNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<Void> deleteAccount(String phoneNumber) {
		// TODO Auto-generated method stub
		return null;
	}

}
