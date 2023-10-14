package com.coderiders.gamificationservice.config;

import com.coderiders.gamificationservice.utilities.ConsoleFormatter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.coderiders.gamificationservice.utilities.ConsoleFormatter.printColored;

@Aspect
@Component
@Profile({"dev", "docker"})
public class QueryTimingAspect {
    @Around("execution(* com.coderiders.gamificationservice.repository..*(..))")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;

        String fullClassName = pjp.getSignature().getDeclaringTypeName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = pjp.getSignature().getName();

        String sb = "Executed [" + simpleClassName + " " + methodName + "]" + " - Elapsed time: " + elapsedTime + "ms";
        printColored(sb, ConsoleFormatter.Color.BLUE, true);

        return output;
    }
}
