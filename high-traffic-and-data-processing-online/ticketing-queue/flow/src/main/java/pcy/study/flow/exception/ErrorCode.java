package pcy.study.flow.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode {

    QUEUE_ALREADY_REGISTERED_USER(HttpStatus.CONFLICT, "UQ-0001", "Already registered in Queue"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String reason;

    public ApplicationException build() {
        return new ApplicationException(status, code, reason);
    }
}
