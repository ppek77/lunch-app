package info.pekny.lunchapp.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import info.pekny.lunchapp.entity.Lunch;
import info.pekny.lunchapp.repository.LunchRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/lunch")
@RequiredArgsConstructor
public class LunchController {

	private final LunchRepository lunchRepository;

	@GetMapping("/new")
	public String newLunch(Model model) {
		var form = new LunchForm();
		form.setDate(LocalDate.now());
		model.addAttribute("lunchForm", form);
		return "lunch-form";
	}

	@GetMapping("/history")
	public String history(Model model) {
		model.addAttribute("lunches", lunchRepository.findTop50ByOrderByDateDesc());
		return "lunch-history";
	}

	@PostMapping
	public String createLunch(@ModelAttribute LunchForm lunchForm) {
		var lunch = new Lunch();
		lunch.setDate(lunchForm.getDate());
		lunch.setMealPrice(lunchForm.getMealPrice());
		lunch.setAmountPaid(lunchForm.getAmountPaid());
		lunchRepository.save(lunch);
		return "redirect:/";
	}
}
