/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.journal.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

/**
 *
 * @author egorov
 */

@Entity
@Table(name = "book")
public class Book implements Serializable {

    
    @ManyToMany(mappedBy = "books")
    private List<Author> authors;

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Book() {
    }

    public Book(String name) {
        this.name = name;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "name", nullable = false, length = 1024, columnDefinition = "")
    protected String name = "Name of book";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[id=" + id + "/" + name + "]";
    }

    @PostLoad
    public void someMethod() {
        System.out.println("@PostLoad");
    }

    @PreRemove
    public void someMethod2() {
        System.out.println("@PreRemove");
    }

    @PostRemove
    public void someMethod21() {
        System.out.println("@PostRemove");
    }

    @PreUpdate
    public void someMethod3() {
        System.out.println("@PreUpdate");
    }

    @PostUpdate
    public void someMethod4() {
        System.out.println("@PostUpdate");
    }
}
