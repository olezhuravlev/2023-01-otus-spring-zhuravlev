package ru.otus.webapp.component;

import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.otus.webapp.dto.SysInfoDto;
import ru.otus.webapp.service.SysInfoService;

@AllArgsConstructor
@Component
public class SysInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final SysInfoService sysInfoService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(SysInfoDto.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        return sysInfoService.getSysInfo();
    }
}
