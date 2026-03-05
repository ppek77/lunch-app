package info.pekny.lunchapp.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LunchForm {

	private LocalDate date;
	private BigDecimal mealPrice;
	private BigDecimal amountPaid;
}
