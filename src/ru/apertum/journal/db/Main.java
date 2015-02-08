/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.journal.db;

import java.util.ArrayList;
import java.util.LinkedList;
import org.hibernate.Filter;
import org.hibernate.Session;
import ru.apertum.journal.db.HibernateUtil;

/**
 *
 * @author egorov
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Session sess = HibernateUtil.getInstance().getSession();

        //Filter filter = sess.enableFilter("bookNameFilter");
        //filter.setParameter("nameString", "krassnaia");


        sess.beginTransaction();
        
        
        
        
        
        sess.getTransaction().commit();
        sess.close();

        /*
        Author a = new Author("Chehow");
        ArrayList<Book> bs = new ArrayList<Book>();

        Book b = new Book("bukvar");
        ArrayList<Author> as = new ArrayList<Author>();
        as.add(a);
        b.setAuthors(as);
        bs.add(b);
        sess.saveOrUpdate(b);

        b = new Book("krassnaia");
        as = new ArrayList<Author>();
        as.add(a);
        b.setAuthors(as);
        bs.add(b);
        sess.saveOrUpdate(b);

        b = new Book("bolsha");
        as = new ArrayList<Author>();
        as.add(a);
        b.setAuthors(as);
        bs.add(b);
        sess.saveOrUpdate(b);

        a.setBooks(bs);


        Book b2 = new Book("Kolobok");
        Author a2 = new Author("Narod");
        ArrayList<Author> as2 = new ArrayList<Author>();
        as2.add(a2);
        b2.setAuthors(as2);
        ArrayList<Book> bs2 = new ArrayList<Book>();
        bs2.add(b2);
        a2.setBooks(bs2);
        sess.saveOrUpdate(a2);


        sess.saveOrUpdate(a);
        sess.saveOrUpdate(b2);
        sess.getTransaction().commit();


        b = (Book) sess.get(Book.class, new Long(1));
        if (b != null) {
            System.out.println("b = " + b.getName() + " " + b.getAuthors());
        } else {
            System.out.println("b = null");
        }



        a = (Author) sess.get(Author.class, new Long(32768));
        if (a != null) {
            System.out.println("a = " + a.getName() + " " + a.getBooks());
        } else {
            System.out.println("a = null");
        }

        LinkedList<Author> ll = new LinkedList(sess.createCriteria(Author.class).list());
        for (Author item : ll) {
            
            String suu = "";
            for (Book au : item.getBooks()) {
                suu = suu + "  " +  au.name;
            }
            
            System.out.println("  list   author = " + item.getName() + " " + item.getBooks()+ "; books:" + suu);
        }

        LinkedList<Book> llb = new LinkedList(sess.createCriteria(Book.class).list());
        for (Book item : llb) {

            String suu = "";
            for (Author au : item.getAuthors()) {
                suu = suu + "  " + au.name;
            }

            System.out.println("  list   book = " + item.getName() + "; autors: " + suu);
        }
         


        sess.close();
         * 
         */
    }
}
