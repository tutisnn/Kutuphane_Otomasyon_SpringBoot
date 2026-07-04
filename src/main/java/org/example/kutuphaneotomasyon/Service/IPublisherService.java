package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoPublisher;
import org.example.kutuphaneotomasyon.Dto.DtoPublisherIU;

import java.util.List;

public interface IPublisherService {
    DtoPublisher savePublisher(DtoPublisherIU dto);
    List<DtoPublisher> getAllPublishers();
    DtoPublisher getPublisherbyId(Integer id);
    DtoPublisher updatePublisher(Integer id, DtoPublisherIU dto);
    String deletePublisher(Integer id);
    List<DtoBook> getBooksByPublisher(Integer publisherId);
}
