/*
 *  Copyright (C) 2015 Apertum{Projects}. web: http://apertum.ru Е-mail:  info@apertum.ru
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.journal.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.apertum.journal.db.HibernateUtil;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Обслелование. Врач проводит обследование - создается этот класс.
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
@Entity
@Table(name = "jrl_document")
public class Document implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    //@OneToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name = "visit_id", nullable = false)
    @ManyToOne
    @JoinColumn(name = "visit_id")
    private Visit visit;

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doc_id")
    @OrderBy(value = "id desc")
    private Set<Attached> attached = new HashSet<>();

    public Set<Attached> getAttached() {
        return attached;
    }

    public void setAttached(Set<Attached> attached) {
        this.attached = attached;
    }
    /**
     * ID типа документа
     */
    @Column(name = "doc_id", nullable = false)
    private Long docId;

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    /**
     * Сериализовые данные документа
     */
    @Column(name = "doc_data", nullable = false, length = 64000)
    private String docData = "";

    public String getDocData() {
        return docData;
    }

    public void setDocData(String docData) {
        this.docData = docData;
    }

    /**
     * Номер документа
     */
    @Column(name = "number", nullable = false)
    private String number = "";

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Дата созания
     */
    @Column(name = "doc_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "№\" " + getNumber() + " \"  от " + Uses.format_dd_MMMM_yyyy.format(getDate());
    }

    //***********************************************************************************************************************************************************************
    //********* DAO
    //***********************************************************************************************************************************************************************
    synchronized static public void saveDoc(Document doc) throws ClientException {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            sess.saveOrUpdate(doc);
            sess.getTransaction().commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка сохранения параметров обследования.", ex);
            sess.getTransaction().rollback();
            throw new ClientException("Ошибка сохранения параметров обследования.", ex);
        } finally {
            sess.close();
        }
    }

    synchronized static public void removeDoc(Document doc) {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            final Query query = sess.createQuery("delete Storage where docId = :ID");
            query.setParameter("ID", doc.getId());
            int result = query.executeUpdate();
            System.out.println("Storage deleted: " + result);
            sess.delete(doc);
            sess.getTransaction().commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка удаления параметров обследования.", ex);
            sess.getTransaction().rollback();
        } finally {
            sess.close();
        }
    }

    synchronized static public LinkedList<Document> getDocs(Visit visit) {
        final Session sess = HibernateUtil.getInstance().getSession();
        try {
            return new LinkedList<>(sess.createCriteria(Document.class).add(Restrictions.eq("visit", visit)).list());
        } finally {
            sess.close();
        }
    }
}
