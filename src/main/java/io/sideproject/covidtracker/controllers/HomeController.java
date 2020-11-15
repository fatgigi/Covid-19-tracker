package io.sideproject.covidtracker.controllers;

import io.sideproject.covidtracker.models.LocationStats;
import io.sideproject.covidtracker.services.CovidDataService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @Autowired
  CovidDataService covidDataService;

  @GetMapping("/")
  public String home(Model model) {
    List<LocationStats> allStats = covidDataService.getAllStats();
    int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
    int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
    model.addAttribute("totalReportedCases", totalReportedCases);
    model.addAttribute("totalNewCases", totalNewCases);


    List<LocationStats> usStats = covidDataService.getUsStats();
    int usTotalReportedCases = usStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
    int usTotalNewCases = usStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
    model.addAttribute("usTotalReportedCases", usTotalReportedCases);
    model.addAttribute("usTotalNewCases", usTotalNewCases);

    return "home";
  }




}
