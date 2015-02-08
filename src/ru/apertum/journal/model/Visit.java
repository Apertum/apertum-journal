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
import org.hibernate.Session;
import org.hibernate.annotations.SortNatural;
import ru.apertum.journal.db.HibernateUtil;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Посещение пациентом врача.
 * Тут вся информация по этому событию.
 * @author Evgeniy Egorov, Aperum Projects
 */
@Entity
@Table(name = "jrl_visit")
//@javax.persistence.TableGenerator(name = "VISIT_GEN_ID",
//        table = "jrl_TABLE_GEN_ID",
//        allocationSize = 1)
public class Visit implements Serializable {

    public Visit() {
    }

    public Visit(Patient patient) {
        this.patient = patient;
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VISIT_GEN_ID")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /*
     * С форенкеем в зависимой, с обратной связью. name="patient_id" это поле таблици форенкий
    @ManyToOne
    @JoinColumn(name="patient_id")
     * 
     */
    @ManyToOne
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "visit_id")
    @OrderBy(value = "doc_date desc")
    private Set<Document> docs = new HashSet<>();

    public Set<Document> getDocs() {
        return docs;
    }

    public void setExams(Set<Document> exams) {
        this.docs = exams;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "visit_id")
    @OrderBy(value = "id desc")
    private Set<Attached> attached = new HashSet<>();

    public Set<Attached> getAttached() {
        return attached;
    }

    public void setAttached(Set<Attached> attached) {
        this.attached = attached;
    }

    
    
    
    /**
     * Дата обращения
     */
    @Column(name = "visit_date", nullable = false)
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
    /**
     * Вес на дату обращения
     */
    @Column(name = "weight", nullable = false)
    private Integer weight = 70;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    @Column(name = "common_status", nullable = false, length = 2500)
    private String commonStatus = "";

    public String getCommonStatus() {
        return commonStatus;
    }

    public void setCommonStatus(String commonStatus) {
        this.commonStatus = commonStatus;
    }
    @Column(name = "left_leg_info", nullable = false, length = 2500)
    private String leftLegInfo = "";

    public String getLeftLegInfo() {
        return leftLegInfo;
    }

    public void setLeftLegInfo(String leftLegInfo) {
        this.leftLegInfo = leftLegInfo;
    }
    @Column(name = "right_leg_info", nullable = false, length = 2500)
    private String rigthLegInfo = "";

    public String getRigthLegInfo() {
        return rigthLegInfo;
    }

    public void setRigthLegInfo(String rigthLegInfo) {
        this.rigthLegInfo = rigthLegInfo;
    }
    @Column(name = "kod", nullable = false, length = 25)
    private String kod = "";

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    //***********************************************************************************************************************************************************************
    //********* DAO
    //***********************************************************************************************************************************************************************
    synchronized static public void saveVisit(Visit visit) throws ClientException {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            sess.saveOrUpdate(visit);
            sess.getTransaction().commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка сохранения посещения.", ex);
            sess.getTransaction().rollback();
            throw new ClientException("Ошибка сохранения посещения.", ex);
        } finally {
            sess.close();
        }
    }
    
    public String getTextInfo(){
        return "<span style='font-size:20.0pt;color:red'>&nbsp;Обращение</span><span style='font-size:16.0pt;color:black'><br>"
                + "&nbsp;Дата обращения: " + Uses.format_dd_MMMM_yyyy.format(getDate())
                + "<br>&nbsp;Код: "
                + getKod()
                + "<br>&nbsp;"
                + "Вес на дату обращения: "
                + getWeight()
                + "<br>&nbsp;"
                + getComments()
                + "</span>";
    }
}
