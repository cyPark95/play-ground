package pcy.study.server.service.command;

public record UserUpdatePasswordCommand(
        String userId,
        String beforePassword,
        String afterPassword
) {
}
