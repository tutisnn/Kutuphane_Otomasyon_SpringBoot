package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.DtoAuthor;
import org.example.kutuphaneotomasyon.Dto.DtoAuthorIU;
import org.example.kutuphaneotomasyon.Dto.DtoBook;

import java.util.List;

public interface IAuthorService {
    DtoAuthor saveAuthor(DtoAuthorIU dto);
    List<DtoAuthor> getAllAuthors();
    DtoAuthor getAuthorById(Integer id);
    DtoAuthor updateAuthor(Integer id, DtoAuthorIU dto);
    String deleteAuthor(Integer id);
    List<DtoBook> getBooksByAuthorId(Integer authorId);
}
