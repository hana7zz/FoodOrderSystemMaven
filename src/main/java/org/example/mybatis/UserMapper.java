package org.example.mybatis;

import org.example.User;
import java.util.List;

public interface UserMapper {
    int insert(User user);

    void delete(int id);

    void update(User user);

    List<User> fetchAll();
}