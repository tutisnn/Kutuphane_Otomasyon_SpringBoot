package org.example.kutuphaneotomasyon.Service.Impl;

import org.example.kutuphaneotomasyon.Dto.DtoBook;
import org.example.kutuphaneotomasyon.Dto.DtoPublisher;
import org.example.kutuphaneotomasyon.Dto.DtoPublisherIU;
import org.example.kutuphaneotomasyon.Entity.Book;
import org.example.kutuphaneotomasyon.Entity.Publisher;
import org.example.kutuphaneotomasyon.Mapper.BookMapperView;
import org.example.kutuphaneotomasyon.Mapper.PublisherMapper;
import org.example.kutuphaneotomasyon.Repository.BookRepository;
import org.example.kutuphaneotomasyon.Repository.PublisherRepository;
import org.example.kutuphaneotomasyon.Service.IPublisherService;
import org.example.kutuphaneotomasyon.exception.BaseException;
import org.example.kutuphaneotomasyon.exception.ErrorMessage;
import org.example.kutuphaneotomasyon.exception.MessageType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements IPublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final BookMapperView bookMapperView = new BookMapperView();

    public PublisherServiceImpl(PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public DtoPublisher savePublisher(DtoPublisherIU dto) {
        Publisher publisher = PublisherMapper.dtoToPublisher(dto);
        Publisher saved = publisherRepository.save(publisher);
        return PublisherMapper.publisherToDto(saved);
    }

    @Override
    public List<DtoPublisher> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();
        if (publishers.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_LIST, "publishers"));
        }

        return publishers.stream()
                .map(PublisherMapper::publisherToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DtoPublisher getPublisherbyId(Integer id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        return PublisherMapper.publisherToDto(publisher);
    }

    @Override
    public DtoPublisher updatePublisher(Integer id, DtoPublisherIU dto) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        PublisherMapper.updatePublisherFromDto(dto, publisher);
        Publisher updated = publisherRepository.save(publisher);
        return PublisherMapper.publisherToDto(updated);
    }

    @Override
    public String deletePublisher(Integer id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        publisherRepository.delete(publisher);
        return "Yayınevi başarıyla silindi.";
    }

    @Override
    public List<DtoBook> getBooksByPublisher(Integer publisherId) {
        if (!publisherRepository.existsById(publisherId)) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, publisherId.toString()));
        }

        List<Book> books = bookRepository.findByPublisherId(publisherId);
        if (books.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXISTS, "Bu yayınevine ait kitap bulunamadı."));
        }

        return books.stream()
                .map(bookMapperView::bookToDto)
                .collect(Collectors.toList());
    }
}
