package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.LoanDto;
import org.example.kutuphaneotomasyon.Dto.LoanDtoIU;

import java.util.List;

public interface LoanService {
    LoanDto saveLoan(LoanDtoIU loanDtoIU);
    List<LoanDto> getAllLoans();
    LoanDto getLoanById(Integer id);
    String deleteLoanById(Integer id);
    LoanDto updateLoan(Integer id, LoanDtoIU updatedLoan);
}
