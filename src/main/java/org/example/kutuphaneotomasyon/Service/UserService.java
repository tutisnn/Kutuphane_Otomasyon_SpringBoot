package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.UserDto;
import org.example.kutuphaneotomasyon.Dto.UserDtoIU;

import java.util.List;

public interface UserService {
    List<UserDto> allUsers();
    UserDto deleteUser(Integer id);
    UserDto updateUser(Integer id, UserDtoIU dto);
    UserDto findById(Integer id);
    List<UserDto> searchByUserName(String keyword);
}
