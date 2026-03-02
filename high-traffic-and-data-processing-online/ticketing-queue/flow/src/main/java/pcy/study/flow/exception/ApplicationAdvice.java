package pcy.study.flow.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ApplicationAdvice {

    @ExceptionHandler(ApplicationException.class)
    public Mono<ResponseEntity<ServerExceptionResponse>> applicationExceptionHandler(ApplicationException ex) {
        return Mono.just(ResponseEntity
                .status(ex.getStatus())
                .body(new ServerExceptionResponse(ex.getCode(), ex.getReason())));
    }
}
