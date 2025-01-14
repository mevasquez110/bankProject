package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.YankiResponse;

public interface YankiService {

	YankiResponse createYanki(YankiRequest yankiRequest);

	List<YankiResponse> findAllYanki();

	YankiResponse updateYanki(String phoneNumber, YankiUpdateRequest yankiUpdateRequest);

	void deleteYanki(String phoneNumber);
}
