package info.pekny.lunchapp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lunch")
@Getter
@Setter
public class Lunch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal mealPrice;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amountPaid;
}
