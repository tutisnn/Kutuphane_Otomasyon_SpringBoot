package org.example.kutuphaneotomasyon.Repository;

import org.example.kutuphaneotomasyon.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    User findUserById(Integer id);
    @Query(value="SELECT * FROM user WHERE username ILIKE %:keyword%",nativeQuery = true)
    List<User> searchByUsername(String keyword);
}
