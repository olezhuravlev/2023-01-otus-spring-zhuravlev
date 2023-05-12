package ru.otus.webapp.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.webapp.component.ModelAndViewPopulator;

@Controller
public class AppErrorController implements ErrorController {

    @Autowired
    ModelAndViewPopulator modelAndViewPopulator;

    @GetMapping(value = "/error")
    public ModelAndView handleError(HttpServletRequest request, ModelAndView modelAndView) {
        return modelAndViewPopulator.fillError404(request, modelAndView);
    }
}
