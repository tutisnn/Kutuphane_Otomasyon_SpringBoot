package org.example.kutuphaneotomasyon.Controller;

import org.example.kutuphaneotomasyon.Dto.DtoPublisherIU;
import org.example.kutuphaneotomasyon.Entity.RootEntity;
import org.example.kutuphaneotomasyon.Service.IPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/Publisher")
public class PublisherController extends RestBaseController {

    @Autowired
    private IPublisherService publisherService;

    @PostMapping("/save")
    public RootEntity<?> savePublisher(@RequestBody DtoPublisherIU dto) {
        return ok(publisherService.savePublisher(dto));
    }

    @GetMapping("/listAllPublishers")
    public RootEntity<?> getAllPublishers() {
        return ok(publisherService.getAllPublishers());
    }

    @GetMapping("/list/{id}")
    public RootEntity<?> getPublisherbyId(@PathVariable(name = "id") Integer id) {
        return ok(publisherService.getPublisherbyId(id));
    }

    @PutMapping("/update/{id}")
    public RootEntity<?> updatePublisher(@PathVariable(name = "id") Integer id,
                                         @RequestBody DtoPublisherIU dto) {
        return ok(publisherService.updatePublisher(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<?> deletePublisher(@PathVariable(name = "id") Integer id) {
        return ok(publisherService.deletePublisher(id));
    }

    @GetMapping("/{publisherId}/books")
    public RootEntity<?> getBooksByPublisher(@PathVariable(name = "publisherId") Integer publisherId) {
        return ok(publisherService.getBooksByPublisher(publisherId));
    }
}
