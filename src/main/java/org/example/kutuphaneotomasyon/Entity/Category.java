package org.example.kutuphaneotomasyon.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Setter
@Entity
@SQLRestriction("deleted=false")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    private String ad;
    private boolean deleted=false;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "category_book",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private List<Book> bookList;

    /*@OneToMany(mappedBy = "category")
    private List<Book> books;*/
}
