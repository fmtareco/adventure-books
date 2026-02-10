package prs.fmtareco.adventure.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTrackerAspect {

    @Around("@annotation(prs.fmtareco.adventure.annotations.TrackExecution)")
    public Object trackExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        final String methodName = joinPoint.getSignature().getName();
        log.info("Execution time for {} : {} ms ",methodName, executionTime);
        return result;
    }

}
