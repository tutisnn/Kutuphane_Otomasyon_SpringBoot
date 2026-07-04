package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoCategory;
import org.example.kutuphaneotomasyon.Dto.DtoCategoryIU;

import java.util.List;

public interface ICategoryService {
    DtoCategory saveCategory(DtoCategoryIU dto);
    List<DtoCategory> getAllCategories();
    DtoCategory getCategoryById(Integer id);
    DtoCategory updateCategory(Integer id, DtoCategoryIU dto);
    String deleteCategory(Integer id);
    List<DtoBook> getBooksByCategoryId(Integer categoryId);
}
