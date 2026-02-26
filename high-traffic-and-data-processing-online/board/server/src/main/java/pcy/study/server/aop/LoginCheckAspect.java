package pcy.study.server.aop;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pcy.study.server.utils.SessionUtil;

@Aspect
@Component
@Slf4j
public class LoginCheckAspect {

    @Around("@annotation(pcy.study.server.aop.LoginCheck) && @annotation(loginCheck)")
    public Object loginCheck(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        UserType userType = loginCheck.type();
        Long id = switch (userType) {
            case USER -> SessionUtil.getLoginUserId(session);
            case ADMIN -> SessionUtil.getLoginAdminId(session);
        };

        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인한 ID 값을 확인해주세요.");
        }
        log.info("로그인 ID: {}", id);

        Object[] args = joinPoint.getArgs();
        args[0] = id;

        return joinPoint.proceed(args);
    }
}
