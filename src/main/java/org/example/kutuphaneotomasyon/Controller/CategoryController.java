package org.example.kutuphaneotomasyon.Controller;

import org.example.kutuphaneotomasyon.Dto.DtoCategoryIU;
import org.example.kutuphaneotomasyon.Entity.RootEntity;
import org.example.kutuphaneotomasyon.Service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/Category")
public class CategoryController extends RestBaseController {

    @Autowired
    private ICategoryService categoryService;

    @PostMapping("/save")
    public RootEntity<?> saveCategory(@RequestBody DtoCategoryIU dto) {
        return ok(categoryService.saveCategory(dto));
    }

    @GetMapping("/listAllCategories")
    public RootEntity<?> getAllCategories() {
        return ok(categoryService.getAllCategories());
    }

    @GetMapping("/list/{id}")
    public RootEntity<?> getCategoryById(@PathVariable(name = "id") Integer id) {
        return ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/update/{id}")
    public RootEntity<?> updateCategory(@PathVariable(name = "id") Integer id,
                                        @RequestBody DtoCategoryIU dto) {
        return ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<?> deleteCategory(@PathVariable(name = "id") Integer id) {
        return ok(categoryService.deleteCategory(id));
    }

    @GetMapping("/{categoryId}/books")
    public RootEntity<?> getBooksByCategoryId(@PathVariable(name = "categoryId") Integer categoryId) {
        return ok(categoryService.getBooksByCategoryId(categoryId));
    }
}
