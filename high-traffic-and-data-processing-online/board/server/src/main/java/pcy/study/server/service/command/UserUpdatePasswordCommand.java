package pcy.study.server.service.command;

public record UserUpdatePasswordCommand(
        Long id,
        String beforePassword,
        String afterPassword
) {
}
