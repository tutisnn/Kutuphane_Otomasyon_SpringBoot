package org.example.kutuphaneotomasyon.Service.Impl;

import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoCategory;
import org.example.kutuphaneotomasyon.Dto.DtoCategoryIU;
import org.example.kutuphaneotomasyon.Entity.Book;
import org.example.kutuphaneotomasyon.Entity.Category;
import org.example.kutuphaneotomasyon.Mapper.BookMapperView;
import org.example.kutuphaneotomasyon.Mapper.CategoryMapper;
import org.example.kutuphaneotomasyon.Repository.BookRepository;
import org.example.kutuphaneotomasyon.Repository.CategoryRepository;
import org.example.kutuphaneotomasyon.Service.ICategoryService;
import org.example.kutuphaneotomasyon.exception.BaseException;
import org.example.kutuphaneotomasyon.exception.ErrorMessage;
import org.example.kutuphaneotomasyon.exception.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    private final BookMapperView bookMapperView = new BookMapperView();

    @Override
    public DtoCategory saveCategory(DtoCategoryIU dto) {
        Category category = CategoryMapper.dtoToCategory(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.categoryToDto(saved);
    }

    @Override
    public List<DtoCategory> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_LIST, "categories"));
        }

        return categories.stream()
                .map(CategoryMapper::categoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoCategory getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        return CategoryMapper.categoryToDto(category);
    }

    @Override
    public DtoCategory updateCategory(Integer id, DtoCategoryIU dto) {
        Category dbCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        CategoryMapper.updateCategoryFromDto(dto, dbCategory);
        Category updated = categoryRepository.save(dbCategory);
        return CategoryMapper.categoryToDto(updated);
    }

    @Override
    public String deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        categoryRepository.delete(category);
        return "Kategori başarıyla silindi.";
    }

    @Override
    public List<DtoBook> getBooksByCategoryId(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, categoryId.toString()));
        }

        List<Book> books = bookRepository.findByCategoryId(categoryId);
        if (books.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, "Bu kategoriye ait kitap bulunamadı."));
        }

        return books.stream()
                .filter(book -> book.getAuthor() != null && book.getPublisher() != null && book.getCategory() != null)
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());
    }
}
