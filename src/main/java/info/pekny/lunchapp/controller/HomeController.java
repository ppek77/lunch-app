package info.pekny.lunchapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import info.pekny.lunchapp.service.LunchService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final LunchService lunchService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("stats", lunchService.getStatistics());
		return "home";
	}
}
