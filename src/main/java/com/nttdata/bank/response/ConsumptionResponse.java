package com.nttdata.bank.response;

import lombok.Data;
import java.time.LocalDate;

/**
 * ConsumptionResponse is a data transfer object representing the response
 * payload for a consumption record. This class includes attributes such as
 * billing date and payment date. It uses Lombok annotations for getters and
 * setters.
 */

@Data
public class ConsumptionResponse {

	private LocalDate billingDate;
	private LocalDate paymentDate;

}
