
package ru.otus.webapp.component;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Component
public class ModelAndViewPopulator {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    public ModelAndView fillError404(HttpServletRequest request, ModelAndView modelAndView) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Locale locale = localeResolver.resolveLocale(request);

        modelAndView.setViewName("error/404");
        modelAndView.addObject("error0", status == null ? "404" : status);
        modelAndView.addObject("error1", messageSource.getMessage("error.404.1", null, locale));
        modelAndView.addObject("error2", messageSource.getMessage("error.404.2", null, locale));

        return modelAndView;
    }
}
