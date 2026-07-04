package org.example.kutuphaneotomasyon.Repository;


import org.example.kutuphaneotomasyon.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryById(Integer id);

    Optional<Category> findFirstByAd(String categoryAd);

}
