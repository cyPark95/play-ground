package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pcy.study.server.domain.User;

@Mapper
public interface UserMapper {

    void insertUser(User user);

    User findById(@Param("id") Long id);

    User findByIdAndPassword(
            @Param("id") Long id,
            @Param("password") String password
    );

    User findByUserIdAndPassword(
            @Param("userId") String userId,
            @Param("password") String password
    );

    boolean existUserId(String userId);

    void updateUser(User user);

    void deleteUser(@Param("id") Long id);
}
