package org.example.kutuphaneotomasyon.Controller;

import org.example.kutuphaneotomasyon.Dto.LoanDtoIU;
import org.example.kutuphaneotomasyon.Entity.RootEntity;
import org.example.kutuphaneotomasyon.Service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController extends RestBaseController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/save")
    public RootEntity<?> saveLoan(@RequestBody LoanDtoIU dtoLoan) {
        return ok(loanService.saveLoan(dtoLoan));
    }

    @GetMapping("/getAll")
    public RootEntity<?> getAllLoans() {
        return ok(loanService.getAllLoans());
    }

    @GetMapping("/getLoan/{id}")
    public RootEntity<?> getLoanById(@PathVariable Integer id) {
        return ok(loanService.getLoanById(id));
    }

    @DeleteMapping("/delete/{id}")
    public RootEntity<?> deleteLoanById(@PathVariable Integer id) {
        return ok(loanService.deleteLoanById(id));
    }

    @PutMapping("/update/{id}")
    public RootEntity<?> updateLoan(@PathVariable Integer id, @RequestBody LoanDtoIU dtoLoan) {
        return ok(loanService.updateLoan(id, dtoLoan));
    }
}
