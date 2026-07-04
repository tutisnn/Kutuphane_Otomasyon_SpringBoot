package org.example.kutuphaneotomasyon.Service.Impl;

import org.example.kutuphaneotomasyon.Dto.DtoAuthor;
import org.example.kutuphaneotomasyon.Dto.DtoAuthorIU;
import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Entity.Author;
import org.example.kutuphaneotomasyon.Entity.Book;
import org.example.kutuphaneotomasyon.Mapper.AuthorMapper;
import org.example.kutuphaneotomasyon.Mapper.BookMapperView;
import org.example.kutuphaneotomasyon.Repository.AuthorRepository;
import org.example.kutuphaneotomasyon.Repository.BookRepository;
import org.example.kutuphaneotomasyon.Service.IAuthorService;
import org.example.kutuphaneotomasyon.exception.BaseException;
import org.example.kutuphaneotomasyon.exception.ErrorMessage;
import org.example.kutuphaneotomasyon.exception.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements IAuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorMapper authorMapper;

    private final BookMapperView bookMapperView = new BookMapperView();

    @Override
    public DtoAuthor saveAuthor(DtoAuthorIU dto) {
        Author author = authorMapper.dtoToAuthor(dto);
        Author saved = authorRepository.save(author);
        return authorMapper.authorToDto(saved);
    }

    @Override
    public List<DtoAuthor> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_LIST, "authors"));
        }

        return authors.stream()
                .map(authorMapper::authorToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoAuthor getAuthorById(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        return authorMapper.authorToDto(author);
    }

    @Override
    public DtoAuthor updateAuthor(Integer id, DtoAuthorIU dto) {
        Author dbAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        authorMapper.updateAuthorFromDto(dto, dbAuthor);
        Author updated = authorRepository.save(dbAuthor);
        return authorMapper.authorToDto(updated);
    }

    @Override
    public String deleteAuthor(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        authorRepository.delete(author);
        return "Yazar başarıyla silindi.";
    }

    @Override
    public List<DtoBook> getBooksByAuthorId(Integer authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, authorId.toString()));
        }

        List<Book> books = bookRepository.findByAuthorId(authorId);
        if (books.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, "Bu yazara ait kitap bulunamadı."));
        }

        return books.stream()
                .filter(book -> book.getAuthor() != null && book.getPublisher() != null && book.getCategory() != null)
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());
    }
}
