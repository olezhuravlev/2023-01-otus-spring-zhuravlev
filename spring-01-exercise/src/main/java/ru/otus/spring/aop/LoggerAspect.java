package ru.otus.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name="application.logger-enabled", havingValue = "true", matchIfMissing = false)
public class LoggerAspect {

    // Log methods annotated with @LoggedMethod.
    @Around("@annotation(ru.otus.spring.aop.LoggedMethod)")
    public Object logAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodDescription = joinPoint.getSignature().toLongString();
        System.out.printf("METHOD: %s is about to be invoked.\n", methodDescription);

        var result = joinPoint.proceed();
        String resultDescription;
        if (result == null) {
            resultDescription = "null";
        } else {
            resultDescription = "type \"" + result.getClass().getName() + "\"";
        }
        System.out.printf("METHOD: %s has returned %s\n", methodDescription, resultDescription);

        return result;
    }
}
