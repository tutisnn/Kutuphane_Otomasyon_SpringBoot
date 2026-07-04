
package org.example.kutuphaneotomasyon.Controller;

import org.example.kutuphaneotomasyon.Dto.DtoBookIU;
import org.example.kutuphaneotomasyon.Model.RootEntity;

import org.example.kutuphaneotomasyon.Service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/Book")
public class BookController extends RestBaseController {
    @Autowired
    private IBookService bookService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/save")
    public RootEntity<?> saveBook(@RequestBody DtoBookIU dto) {
        return ok(bookService.saveBook(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/update/{id}")
    public RootEntity<?> updateBook(
            @PathVariable Integer id,
            @RequestBody DtoBookIU dto
    ) {
        return ok(bookService.updateBook(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/delete/{id}")
    public RootEntity<?> deleteBook(@PathVariable(name="id")Integer id) {
        return ok(bookService.deleteBook(id));
    }

    @GetMapping(path ="/listAll")
    public RootEntity<?> getAllBooks() {
        return ok(bookService.getAllBooks());
    }

    @GetMapping(path ="/list/{id}")
    public RootEntity<?> findById(@PathVariable(name="id") Integer id) {
        return ok(bookService.findById(id));
    }

    @GetMapping("/search")
    public RootEntity<?> searchBooksByName(@RequestParam String keyword) {
        return ok(bookService.searchBooksByName(keyword));
    }

    @GetMapping("/system/status")
    public RootEntity<?> getSystemStatus() {
        return ok(bookService.getSystemStatus());
    }



}
