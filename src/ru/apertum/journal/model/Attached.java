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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.SortNatural;
import ru.apertum.journal.db.HibernateUtil;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Блобы с файлами Списки приложенных этих файлов к посещению и к документам
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
@Entity
@Table(name = "jrl_attached")
//@javax.persistence.TableGenerator(name = "ATTACHED_GEN_ID",
//      table = "jrl_TABLE_GEN_ID",
//    allocationSize = 1)
public class Attached implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "ATTACHED_GEN_ID")
    private Long id;

    public Attached(Visit visit) {
        this.visit = visit;
        this.doc = null;
        this.patient = null;
    }

    public Attached(Document doc) {
        this.visit = null;
        this.patient = null;
        this.doc = doc;
    }

    public Attached() {
    }

    public Attached(Patient patient) {
        this.patient = patient;
        this.doc = null;
        this.visit = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "visit_id", nullable = true, insertable = true, updatable = false)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Visit visit;

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    @ManyToOne()
    @JoinColumn(name = "doc_id", nullable = true, insertable = true, updatable = false)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Document doc;

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    @ManyToOne()
    @JoinColumn(name = "patient_id", nullable = true, insertable = true, updatable = false)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Дата добавления
     */
    @Column(name = "attached_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @SortNatural()
    private Date date = new Date();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "comments", nullable = false, length = 250)
    private String comments = "";

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "title", nullable = false, length = 250)
    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "file_name", nullable = false, length = 250)
    private String fileName = "";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    //***********************************************************************************************************************************************************************
    //********* DAO
    //***********************************************************************************************************************************************************************
    synchronized static public void saveAttached(Attached doc) throws ClientException {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            sess.saveOrUpdate(doc);
            sess.getTransaction().commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка сохранения посещения.", ex);
            sess.getTransaction().rollback();
            throw new ClientException("Ошибка сохранения посещения.", ex);
        } finally {
            sess.close();
        }
    }

    synchronized static public void removeAttached(Attached doc) {
        final Session sess = HibernateUtil.getInstance().getSession();
        final Transaction t = sess.beginTransaction();
        try {
            doc.setVisit(null);
            sess.delete(doc);

            Query query = sess.createQuery("delete Storage where id = :ID");
            query.setParameter("ID", doc.getId());

            int result = query.executeUpdate();
            System.out.println("Storage deleted: " + result);
            t.commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка удаления вложения.", ex);
            t.rollback();
        } finally {
            sess.close();
        }
    }

    public String getTextInfo() {
        return "<span style='font-size:20.0pt;color:red'>&nbsp;Приложение</span><span style='font-size:16.0pt;color:black'><br>"
                + "&nbsp;Дата приложения: " + Uses.format_dd_MMMM_yyyy.format(getDate())
                + "<br>&nbsp;Название: "
                + getTitle()
                + "<br>&nbsp;"
                + "Комментарии: "
                + getComments()
                + "</span>";
    }
}
