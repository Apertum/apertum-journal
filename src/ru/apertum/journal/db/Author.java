/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.journal.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@FilterDefs({
    @FilterDef(name = "bookNameFilter", parameters = {
        @ParamDef(name = "nameString", type = "string")})
})
@Entity
@Table(name = "author")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    public Author() {
    }

    public Author(String name) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Author)) {
            return false;
        }
        Author other = (Author) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[id=" + id + "/" + name + "]";
    }
    @Column(name = "name", nullable = false, length = 128, columnDefinition = "")
    protected String name = "John Dou";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /*
    @Filters({
        @Filter(name = "bookNameFilter", condition = "(name = :nameString))")
    })
     * 
     */
    @ManyToMany()
    //@JoinTable(name="author_book", joinColumns=@JoinColumn(name="author_id"), inverseJoinColumns=@JoinColumn(name="book_id"))
    private List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
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
