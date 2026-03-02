package pcy.study.flow.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApplicationException extends RuntimeException{

    private final HttpStatus status;

    private final String code;

    private final String reason;
}
