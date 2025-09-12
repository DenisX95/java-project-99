package hexlet.code.service;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.model.User;

import java.util.List;

public interface UserService {
    List<UserDTO> getAll();
    UserDTO findById(Long id);
    UserDTO create(UserCreateDTO userData);
    UserDTO update(UserUpdateDTO userData, Long id);
    void delete(Long id);
    User getCurrentUser();
}
