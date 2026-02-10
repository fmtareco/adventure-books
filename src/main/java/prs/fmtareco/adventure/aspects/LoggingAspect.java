package prs.fmtareco.adventure.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* prs.fmtareco.adventure.service.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* prs.fmtareco.adventure.repository.*.*(..))")
    public void repoMethods() {}

    @Before("serviceMethods()")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        log.info("Service Method Started: {}", joinPoint.getSignature().getName());
        log.info("- Arguments: {}", joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void afterServiceMethod(JoinPoint joinPoint, Object result) {
        log.info("Service Method {} returned {}",
            joinPoint.getSignature().getName(),
            result);
    }

    @Before("repoMethods()")
    public void beforeRepoMethod(JoinPoint joinPoint) {
        log.info("Repo Method Started: {}", joinPoint.getSignature().getName());
        log.info("- Arguments: {}", joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "repoMethods()", returning = "result")
    public void afterRepoMethod(JoinPoint joinPoint, Object result) {
        log.info("Repo Method {} returned {}",
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(pointcut = "serviceMethods() || repoMethods()", throwing = "exception")
    public void onException(JoinPoint joinPoint, Throwable exception) {
        log.info("Exception in Method {} : {}",
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

}

