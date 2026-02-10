package pcy.study.server.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String msg) {
        super(msg);
    }
}
