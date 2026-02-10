package pcy.study.server.service;

import pcy.study.server.service.command.UserSaveCommand;
import pcy.study.server.service.command.UserUpdatePasswordCommand;
import pcy.study.server.service.info.UserInfo;

public interface UserService {

    void save(UserSaveCommand command);

    UserInfo getUser(Long id);

    UserInfo login(String userId, String password);

    boolean isDuplicatedId(String userId);

    void updatePassword(UserUpdatePasswordCommand command);

    void delete(Long id, String password);
}
