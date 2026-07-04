package org.example.kutuphaneotomasyon.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@SQLRestriction("deleted=false")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    public LocalDate borrowDate;
    public LocalDate returnDate;
    private boolean isReturned;
    private boolean deleted = false;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
