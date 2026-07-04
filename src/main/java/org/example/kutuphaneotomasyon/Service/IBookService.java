package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoBookIU;
import org.example.kutuphaneotomasyon.Dto.DtoSystemStatus;

import java.util.List;

public interface IBookService {

    DtoBook saveBook(DtoBookIU dto);

    DtoBook updateBook(Integer id, DtoBookIU dto);

    String deleteBook(Integer id);

    List<DtoBook> getAllBooks();

    DtoBook findById(Integer id);

    List<DtoBook> searchBooksByName(String keyword);

    DtoSystemStatus getSystemStatus();
}
