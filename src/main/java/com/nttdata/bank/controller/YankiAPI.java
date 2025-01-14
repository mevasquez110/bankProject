package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.YankiResponse;

@RestController
@RequestMapping("/yanki")
public interface YankiAPI {

	@PostMapping("/create")
	ApiResponse<YankiResponse> createYanki(@RequestBody @Valid YankiRequest yankiRequest);

	@GetMapping("/all")
	ApiResponse<List<YankiResponse>> findAllYanki();

	@PutMapping("/update/{phoneNumber}")
	ApiResponse<YankiResponse> updateYanki(@PathVariable String phoneNumber,
			@RequestBody @Valid YankiUpdateRequest yankiUpdateRequest);

	@DeleteMapping("/delete/{phoneNumber}")
	ApiResponse<Void> deleteYanki(@PathVariable String phoneNumber);
}
