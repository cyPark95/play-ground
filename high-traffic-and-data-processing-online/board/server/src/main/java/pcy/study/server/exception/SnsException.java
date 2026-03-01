package pcy.study.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SnsException extends RuntimeException {

    private final HttpStatus status;

    public SnsException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public SnsException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public SnsException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
