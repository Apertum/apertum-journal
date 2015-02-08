/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.journal.db;

import java.util.HashSet;
import org.hibernate.Session;
import ru.apertum.journal.model.Patient;
import ru.apertum.journal.model.Visit;

/**
 *
 * @author Evgeniy Egorov
 */
public class testbd {
        public static void main(String[] args) {
        Session sess = HibernateUtil.getInstance().getSession();
        
        sess.beginTransaction();
        
        Patient p = new Patient();
        p.setSecondName("Петров4");
        HashSet<Visit> exs = new HashSet<>();
        exs.add(new Visit());
        exs.add(new Visit());
        p.setVisits(exs);
        sess.saveOrUpdate(p);
        sess.getTransaction().commit();
        sess.close();
    }
}
