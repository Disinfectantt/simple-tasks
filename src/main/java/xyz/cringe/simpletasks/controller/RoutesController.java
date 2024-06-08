package xyz.cringe.simpletasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoutesController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

    @GetMapping("/all_tasks")
    public String allTasksPage() {
        return "pages/all_tasks";
    }
}
