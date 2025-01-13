package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.YankiAPI;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.YankiResponse;

@RestController
public class YankiController implements YankiAPI {

	@Override
	public ApiResponse<YankiResponse> createYanki(@Valid YankiRequest yankiRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<List<YankiResponse>> findAllDebitCardResponses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<YankiResponse> update(String phoneNumber, @Valid YankiUpdateRequest yankiUpdateRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<Void> deleteDebitCardResponse(String phoneNumber) {
		// TODO Auto-generated method stub
		return null;
	}


}
