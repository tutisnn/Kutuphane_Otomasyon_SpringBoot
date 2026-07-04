
package org.example.kutuphaneotomasyon.Controller;

import org.example.kutuphaneotomasyon.Dto.DtoBookIU;
import org.example.kutuphaneotomasyon.ResponseMessage.GenericResponse;

import org.example.kutuphaneotomasyon.Service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/Book")
public class BookController  {
    @Autowired
    private IBookService bookService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/save")
    public GenericResponse<?> saveBook(@RequestBody DtoBookIU dto) {
        return bookService.saveBook(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/update/{id}")
    public GenericResponse<?> updateBook(
            @PathVariable Integer id,
            @RequestBody DtoBookIU dto
    ) {
        return bookService.updateBook(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/delete/{id}")
    public GenericResponse<?> deleteBook(@PathVariable(name="id")Integer id) {
        return bookService.deleteBook(id);
    }

    @GetMapping(path ="/listAll")
    public GenericResponse<?> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping(path ="/list/{id}")
    public GenericResponse<?> findById(@PathVariable(name="id") Integer id) {
        return bookService.findById(id);
    }

    @GetMapping("/search")
    public GenericResponse<?> searchBooksByName(@RequestParam String keyword) {
        return bookService.searchBooksByName(keyword);
    }

    @GetMapping("/system/status")
    public GenericResponse<?> getSystemStatus() {
        return bookService.getSystemStatus();
    }



}
