/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.journal.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/hiber";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Date date = new Date(System.currentTimeMillis());
        Long l = 1L;

        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            conn.setAutoCommit(false);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            // the mysql insert statement
            String query = " insert into testy (id, dd, st1, lo1, st2, lo2)"
                    + " values (?, ?, ?, ?, ?, ?)";
            
            

            long st = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            while (end - st < 60000) {

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setLong(1, l++);
                preparedStmt.setDate(2, date);
                preparedStmt.setString(3, "qwerty");
                preparedStmt.setLong(4, 5000L);
                preparedStmt.setString(5, "NN");
                preparedStmt.setLong(6, 100500L);

                // execute the preparedstatement
                preparedStmt.execute();
                end = System.currentTimeMillis();
            }

            conn.commit();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");

        if (true) {
            return;
        }
        //********************************************************************************************************************
        //********************************************************************************************************************
        //********************************************************************************************************************
        //********************************************************************************************************************
        //********************************************************************************************************************
        //********************************************************************************************************************

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
