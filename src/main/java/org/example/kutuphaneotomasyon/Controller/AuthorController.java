package org.example.kutuphaneotomasyon.Controller;

import org.example.kutuphaneotomasyon.Dto.DtoAuthorIU;
import org.example.kutuphaneotomasyon.Model.RootEntity;
import org.example.kutuphaneotomasyon.Service.IAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/Author")
public class AuthorController extends RestBaseController {

    @Autowired
    private IAuthorService authorService;

    @PostMapping("/save")
    public RootEntity<?> saveAuthor(@RequestBody DtoAuthorIU dto) {
        return ok(authorService.saveAuthor(dto));
    }

    @GetMapping("/listAllAuthors")
    public RootEntity<?> getAllAuthors() {
        return ok(authorService.getAllAuthors());
    }

    @GetMapping("/list/{id}")
    public RootEntity<?> getAuthorById(@PathVariable(name = "id") Integer id) {
        return ok(authorService.getAuthorById(id));
    }

    @PutMapping("/update/{id}")
    public RootEntity<?> updateAuthor(@PathVariable(name = "id") Integer id,
                                      @RequestBody DtoAuthorIU dto) {
        return ok(authorService.updateAuthor(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<?> deleteAuthor(@PathVariable(name = "id") Integer id) {
        return ok(authorService.deleteAuthor(id));
    }

    @GetMapping("/{authorId}/books")
    public RootEntity<?> getBooksByAuthorId(@PathVariable(name = "authorId") Integer authorId) {
        return ok(authorService.getBooksByAuthorId(authorId));
    }
}
