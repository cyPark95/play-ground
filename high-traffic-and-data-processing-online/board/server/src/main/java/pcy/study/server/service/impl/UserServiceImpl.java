package pcy.study.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.domain.User;
import pcy.study.server.exception.AccessDeniedException;
import pcy.study.server.exception.DuplicateIdException;
import pcy.study.server.mapper.UserMapper;
import pcy.study.server.service.UserService;
import pcy.study.server.service.command.UserSaveCommand;
import pcy.study.server.service.command.UserUpdatePasswordCommand;
import pcy.study.server.service.info.UserInfo;
import pcy.study.server.utils.SHA256Util;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public void save(UserSaveCommand command) {
        boolean isDuplicated = isDuplicatedId(command.userId());
        if (isDuplicated) {
            throw new DuplicateIdException("중복된 아이디입니다.");
        }

        String encryptedPassword = SHA256Util.encryptSHA256(command.password());
        User user = command.toDomain(encryptedPassword);
        userMapper.insertUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfo getUser(Long id) {
        User user = userMapper.findById(id);
        return UserInfo.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfo login(String userId, String password) {
        String encryptedPassword = SHA256Util.encryptSHA256(password);

        User user = userMapper.findByUserIdAndPassword(userId, encryptedPassword);
        if(user == null) {
            throw new AccessDeniedException("사용자[%s] 로그인 실패했습니다.".formatted(userId));
        }

        return UserInfo.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedId(String userId) {
        return userMapper.existUserId(userId);
    }

    @Override
    public void updatePassword(UserUpdatePasswordCommand command) {
        String encryptedBeforePassword = SHA256Util.encryptSHA256(command.beforePassword());

        User user = userMapper.findByUserIdAndPassword(command.userId(), encryptedBeforePassword);
        if(user == null) {
            throw new IllegalArgumentException("비밀번호 수정에 실패했습니다.\n Params: %s".formatted(command));
        }

        String encryptedAfterPassword = SHA256Util.encryptSHA256(command.afterPassword());
        user.changePassword(encryptedAfterPassword);
        userMapper.updateUser(user);
    }

    @Override
    public void delete(Long id, String password) {
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        User user = userMapper.findByIdAndPassword(id, encryptedPassword);
        if(user == null) {
            throw new IllegalArgumentException("사용자[%d] 삭제에 실패했습니다.".formatted(id));
        }

        userMapper.deleteUser(id);
    }
}
