package pcy.study.flow.exception;

public record ServerExceptionResponse(
        String code,
        String reason
) {
}
