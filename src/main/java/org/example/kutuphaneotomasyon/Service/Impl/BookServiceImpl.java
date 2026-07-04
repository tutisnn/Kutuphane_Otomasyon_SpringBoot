package org.example.kutuphaneotomasyon.Service.Impl;


import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoBookIU;
import org.example.kutuphaneotomasyon.Dto.DtoSystemStatus;
import org.example.kutuphaneotomasyon.Entity.*;
import org.example.kutuphaneotomasyon.Mapper.BookMapperIU;
import org.example.kutuphaneotomasyon.Mapper.BookMapperView;
import org.example.kutuphaneotomasyon.Repository.AuthorRepository;
import org.example.kutuphaneotomasyon.Repository.BookRepository;
import org.example.kutuphaneotomasyon.Repository.CategoryRepository;
import org.example.kutuphaneotomasyon.Repository.PublisherRepository;
import org.example.kutuphaneotomasyon.ResponseMessage.Constants;
import org.example.kutuphaneotomasyon.ResponseMessage.GenericResponse;
import org.example.kutuphaneotomasyon.Service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements IBookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private final BookMapperIU bookMapperIU = new BookMapperIU();
    private final BookMapperView bookMapperView = new BookMapperView();

    @Override
    public GenericResponse<?> saveBook(DtoBookIU dto) {
        System.out.println("saveBook called...");

        Author author = authorRepository.findFirstByAdAndSoyad(dto.getAuthorAd(), dto.getAuthorSoyad())
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setAd(dto.getAuthorAd());
                    newAuthor.setSoyad(dto.getAuthorSoyad());
                    return authorRepository.save(newAuthor);
                });

        Publisher publisher = publisherRepository.findFirstByAd(dto.getPublisherAd())
                .orElseGet(() -> {
                    Publisher newPublisher = new Publisher();
                    newPublisher.setAd(dto.getPublisherAd());
                    return publisherRepository.save(newPublisher);
                });

        Category category = categoryRepository.findFirstByAd(dto.getCategoryAd())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setAd(dto.getCategoryAd());
                    return categoryRepository.save(newCategory);
                });

        Book book = bookMapperIU.dtoToBook(dto);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);

        Book saved = bookRepository.save(book);
        DtoBook responseDto = bookMapperView.bookToDto(saved);
        return GenericResponse.success(responseDto);
    }

    @Override
    @CachePut(value = "books", key = "#id", unless = "#result.data == null")
    public GenericResponse<?> updateBook(Integer id, DtoBookIU dto) {
        System.out.println("updateBook called with DTO...");

        Optional<Book> optional = bookRepository.findById(id);
        if (optional.isEmpty()) {
            return GenericResponse.error(Constants.EMPTY_ID);
        }

        Book book = optional.get();
        bookMapperIU.updateBookFromDto(dto, book);

        Author author = authorRepository.findFirstByAdAndSoyad(dto.getAuthorAd(), dto.getAuthorSoyad())
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setAd(dto.getAuthorAd());
                    newAuthor.setSoyad(dto.getAuthorSoyad());
                    return authorRepository.save(newAuthor);
                });

        Publisher publisher = publisherRepository.findFirstByAd(dto.getPublisherAd())
                .orElseGet(() -> {
                    Publisher newPublisher = new Publisher();
                    newPublisher.setAd(dto.getPublisherAd());
                    return publisherRepository.save(newPublisher);
                });

        Category category = categoryRepository.findFirstByAd(dto.getCategoryAd())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setAd(dto.getCategoryAd());
                    return categoryRepository.save(newCategory);
                });

        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);

        Book updated = bookRepository.save(book);
        DtoBook response = bookMapperView.bookToDto(updated);
        return GenericResponse.success(response);
    }

    @Override
    @CacheEvict(value = "books", key = "#id")
    public GenericResponse<?> deleteBook(Integer id) {
        System.out.println("deleteBook called...");

        Book bookExists = bookRepository.findBookById(id);
        if (bookExists == null) {
            return GenericResponse.error(Constants.EMPTY_ID);
        } else {
            bookRepository.delete(bookExists);
            return GenericResponse.success("Kitap başarıyla silindi.");
        }
    }

    @Override
    public GenericResponse<?> getAllBooks() {
        System.out.println("getAllBooks called...");
        List<Book> books = bookRepository.findAllWithRelations();

        if (books.isEmpty()) {
            return GenericResponse.error(Constants.EMPTY_LIST);
        }

        List<DtoBook> dtoBooks = books.stream()
                .filter(book -> book.getAuthor() != null && book.getPublisher() != null && book.getCategory() != null)
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());

        return GenericResponse.success(dtoBooks);
    }

    @Override
    @Cacheable(value = "books", key = "#id")
    public GenericResponse<?> findById(Integer id) {
        System.out.println("findById called from database...");
        Book book = bookRepository.findBookById(id);

        if (book == null) {
            return GenericResponse.error(Constants.EMPTY_ID);
        }

        DtoBook dto = bookMapperView.bookToDto(book);
        return GenericResponse.success(dto);
    }

    @Override
    public GenericResponse<?> searchBooksByName(String keyword) {
        System.out.println("searchBooksByName called...");

        List<Book> foundBooks = bookRepository.searchByName(keyword);

        if (foundBooks.isEmpty()) {
            return GenericResponse.error("Aradığınız kelimeyle eşleşen kitap bulunamadı.");
        }

        List<DtoBook> dtoList = foundBooks.stream()
                .filter(book -> book.getAuthor() != null && book.getPublisher() != null && book.getCategory() != null)
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());

        return GenericResponse.success(dtoList);
    }

    @Override
    public GenericResponse<?> getSystemStatus() {
        long toplam = bookRepository.count();
        long musait = bookRepository.countByDurum(Durum.MUSAIT);
        long odunc = bookRepository.countByDurum(Durum.ODUNC_VERILDI);

        DtoSystemStatus dto = new DtoSystemStatus(toplam, musait, odunc);
        return GenericResponse.success(dto);
    }

}
