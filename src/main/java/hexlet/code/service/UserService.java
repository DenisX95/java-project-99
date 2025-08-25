package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        var users = repository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findById(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO create(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        repository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO update(UserUpdateDTO userData, Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(userData, user);
        repository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
