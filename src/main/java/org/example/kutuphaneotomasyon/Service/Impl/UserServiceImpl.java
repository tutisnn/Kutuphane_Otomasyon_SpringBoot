package org.example.kutuphaneotomasyon.Service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.kutuphaneotomasyon.Dto.UserDto;
import org.example.kutuphaneotomasyon.Dto.UserDtoIU;
import org.example.kutuphaneotomasyon.Entity.User;
import org.example.kutuphaneotomasyon.Mapper.UserMapper;
import org.example.kutuphaneotomasyon.Repository.UserRepository;
import org.example.kutuphaneotomasyon.Service.UserService;
import org.example.kutuphaneotomasyon.exception.BaseException;
import org.example.kutuphaneotomasyon.exception.ErrorMessage;
import org.example.kutuphaneotomasyon.exception.MessageType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> allUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToDto)
                .toList();
    }

    @Override
    public UserDto deleteUser(Integer id) {
        User userExists = userRepository.findUserById(id);
        if (userExists == null) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString()));
        }

        userExists.setDeleted(true);
        userRepository.save(userExists);
        return userMapper.userToDto(userExists);
    }

    @Override
    public UserDto updateUser(Integer id, UserDtoIU dto) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString()));
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User updated = userRepository.save(user);
        return userMapper.userToDto(updated);
    }

    @Override
    public UserDto findById(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString()));
        }
        return userMapper.userToDto(user);
    }

    @Override
    public List<UserDto> searchByUserName(String keyword) {
        return userRepository.searchByUsername(keyword)
                .stream()
                .map(userMapper::userToDto)
                .toList();
    }
}
