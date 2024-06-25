package xyz.cringe.simpletasks.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoutesController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("currentPage", "/tasks/");
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request, HttpServletResponse response) {
        String headerValue = request.getHeader("HX-Request");
        if (headerValue != null) {
            response.setHeader("HX-Redirect", "/login");
            return null;
        }
        return "login";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

}
