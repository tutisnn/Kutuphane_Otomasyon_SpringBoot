package org.example.kutuphaneotomasyon.Service;
import org.example.kutuphaneotomasyon.Dto.DtoBookIU;
import org.example.kutuphaneotomasyon.ResponseMessage.GenericResponse;

public interface IBookService {
    GenericResponse<?> saveBook(DtoBookIU dto);
    GenericResponse<?>updateBook(Integer id, DtoBookIU dto);
    GenericResponse<?>deleteBook(Integer id);
    GenericResponse<?>getAllBooks();

    GenericResponse<?> findById(Integer id);
    GenericResponse<?> searchBooksByName(String keyword);
    public GenericResponse<?> getSystemStatus();




}
