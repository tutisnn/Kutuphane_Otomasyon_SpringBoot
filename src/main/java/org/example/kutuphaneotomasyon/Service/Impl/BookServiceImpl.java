package org.example.kutuphaneotomasyon.Service.Impl;

import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoBookIU;
import org.example.kutuphaneotomasyon.Dto.DtoSystemStatus;
import org.example.kutuphaneotomasyon.Entity.Author;
import org.example.kutuphaneotomasyon.Entity.Book;
import org.example.kutuphaneotomasyon.Entity.Category;
import org.example.kutuphaneotomasyon.Entity.Durum;
import org.example.kutuphaneotomasyon.Entity.Publisher;
import org.example.kutuphaneotomasyon.Mapper.BookMapperIU;
import org.example.kutuphaneotomasyon.Mapper.BookMapperView;
import org.example.kutuphaneotomasyon.Repository.AuthorRepository;
import org.example.kutuphaneotomasyon.Repository.BookRepository;
import org.example.kutuphaneotomasyon.Repository.CategoryRepository;
import org.example.kutuphaneotomasyon.Repository.PublisherRepository;
import org.example.kutuphaneotomasyon.Service.IBookService;
import org.example.kutuphaneotomasyon.exception.BaseException;
import org.example.kutuphaneotomasyon.exception.ErrorMessage;
import org.example.kutuphaneotomasyon.exception.MessageType;
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
    public DtoBook saveBook(DtoBookIU dto) {
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
        return bookMapperView.bookToDto(saved);
    }

    @Override
    @CachePut(value = "books", key = "#id", unless = "#result == null")
    public DtoBook updateBook(Integer id, DtoBookIU dto) {
        System.out.println("updateBook called with DTO...");

        Optional<Book> optional = bookRepository.findById(id);
        if (optional.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, id.toString()));
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
        return bookMapperView.bookToDto(updated);
    }

    @Override
    @CacheEvict(value = "books", key = "#id")
    public String deleteBook(Integer id) {
        System.out.println("deleteBook called...");

        Book bookExists = bookRepository.findBookById(id);
        if (bookExists == null) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, id.toString()));
        }

        bookRepository.delete(bookExists);
        return "Kitap başarıyla silindi.";
    }

    @Override
    public List<DtoBook> getAllBooks() {
        System.out.println("getAllBooks called...");
        List<Book> books = bookRepository.findAllWithRelations();

        if (books.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, "kitap listesi"));
        }

        return books.stream()
                .filter(book -> book.getAuthor() != null && book.getPublisher() != null && book.getCategory() != null)
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "books", key = "#id", unless = "#result == null")
    public DtoBook findById(Integer id) {
        System.out.println("findById called from database...");
        Book book = bookRepository.findBookById(id);

        if (book == null) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, id.toString()));
        }

        return bookMapperView.bookToDto(book);
    }

    @Override
    public List<DtoBook> searchBooksByName(String keyword) {
        System.out.println("searchBooksByName called...");

        List<Book> foundBooks = bookRepository.searchByName(keyword);

        if (foundBooks.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, keyword));
        }

        return foundBooks.stream()
                .filter(book -> book.getAuthor() != null && book.getPublisher() != null && book.getCategory() != null)
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoSystemStatus getSystemStatus() {
        long toplam = bookRepository.count();
        long musait = bookRepository.countByDurum(Durum.MUSAIT);
        long odunc = bookRepository.countByDurum(Durum.ODUNC_VERILDI);

        return new DtoSystemStatus(toplam, musait, odunc);
    }
}
