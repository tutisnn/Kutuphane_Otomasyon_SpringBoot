package org.example.kutuphaneotomasyon.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.kutuphaneotomasyon.Dto.LoanDto;
import org.example.kutuphaneotomasyon.Dto.LoanDtoIU;
import org.example.kutuphaneotomasyon.Entity.Book;
import org.example.kutuphaneotomasyon.Entity.Durum;
import org.example.kutuphaneotomasyon.Entity.Loan;
import org.example.kutuphaneotomasyon.Entity.User;
import org.example.kutuphaneotomasyon.Mapper.LoanMapper;
import org.example.kutuphaneotomasyon.Repository.BookRepository;
import org.example.kutuphaneotomasyon.Repository.LoanRepository;
import org.example.kutuphaneotomasyon.Repository.UserRepository;
import org.example.kutuphaneotomasyon.Service.LoanService;
import org.example.kutuphaneotomasyon.exception.BaseException;
import org.example.kutuphaneotomasyon.exception.ErrorMessage;
import org.example.kutuphaneotomasyon.exception.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanMapper loanMapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public LoanDto saveLoan(LoanDtoIU loanDtoIU) {
        User user = userRepository.findById(loanDtoIU.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_USER, loanDtoIU.getUserId().toString())));
        Book book = bookRepository.findById(loanDtoIU.getBookId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_BOOK, loanDtoIU.getBookId().toString())));

        if (book.getDurum() != Durum.MUSAIT) {
            throw new BaseException(new ErrorMessage(MessageType.BOOK_NOT_AVAILABLE, book.getDurum().name()));
        }

        book.setDurum(Durum.ODUNC_VERILDI);
        bookRepository.save(book);

        Loan loan = loanMapper.dtoToLoan(loanDtoIU, user, book);
        Loan saved = loanRepository.save(loan);
        return loanMapper.loanToDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<LoanDto> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::loanToDto)
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public LoanDto getLoanById(Integer id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        return loanMapper.loanToDto(loan);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String deleteLoanById(Integer id) {
        if (!loanRepository.existsById(id)) {
            throw new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString()));
        }
        loanRepository.deleteById(id);
        return "Deleted successfully";
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public LoanDto updateLoan(Integer id, LoanDtoIU updatedLoan) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_ID, id.toString())));
        User user = userRepository.findById(updatedLoan.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_USER, updatedLoan.getUserId().toString())));
        Book book = bookRepository.findById(updatedLoan.getBookId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMPTY_BOOK, updatedLoan.getBookId().toString())));

        loan.setBorrowDate(updatedLoan.getBorrowDate());
        loan.setReturnDate(updatedLoan.getReturnDate());
        loan.setReturned(updatedLoan.isReturned());
        loan.setUser(user);
        loan.setBook(book);

        if (updatedLoan.isReturned()) {
            book.setDurum(Durum.MUSAIT);
            bookRepository.save(book);
        }

        Loan updated = loanRepository.save(loan);
        return loanMapper.loanToDto(updated);
    }
}
