package info.pekny.lunchapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import info.pekny.lunchapp.entity.Lunch;
import info.pekny.lunchapp.repository.LunchRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LunchService {

	private final LunchRepository lunchRepository;

	public LunchStatistics getStatistics() {
		List<Lunch> lunches = lunchRepository.findAll();

		BigDecimal totalMealPrice = BigDecimal.ZERO;
		BigDecimal totalAmountPaid = BigDecimal.ZERO;

		for (Lunch lunch : lunches) {
			totalMealPrice = totalMealPrice.add(lunch.getMealPrice());
			totalAmountPaid = totalAmountPaid.add(lunch.getAmountPaid());
		}

		BigDecimal difference = totalMealPrice.subtract(totalAmountPaid);

		BigDecimal differencePercent = BigDecimal.ZERO;
		if (totalMealPrice.compareTo(BigDecimal.ZERO) != 0) {
			differencePercent = difference
					.multiply(BigDecimal.valueOf(100))
					.divide(totalMealPrice, 1, RoundingMode.HALF_UP);
		}

		return new LunchStatistics(
				lunches.size(),
				totalMealPrice,
				totalAmountPaid,
				difference,
				differencePercent
		);
	}

	public record LunchStatistics(
			int lunchCount,
			BigDecimal totalMealPrice,
			BigDecimal totalAmountPaid,
			BigDecimal difference,
			BigDecimal differencePercent
	) {
		public boolean isSaving() {
			return difference.compareTo(BigDecimal.ZERO) > 0;
		}

		public boolean isOverpaying() {
			return difference.compareTo(BigDecimal.ZERO) < 0;
		}
	}
}
