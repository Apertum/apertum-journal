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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SortNatural;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import ru.apertum.journal.IPatientController;
import ru.apertum.journal.db.HibernateUtil;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Карточка пациента: ФИО Дата рождения Пол Вес Адрес проживания Контактный телефон Профессия Направление на обследование (источник) Симптомы Диагноз
 * (заполняется по результатам обследований) Назначения врача
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
@Entity
@Table(name = "jrl_patient")
@javax.persistence.TableGenerator(name = "PATIENT_GEN_ID",
        table = "jrl_TABLE_GEN_ID",
        allocationSize = 1)
public class Patient implements Serializable {

    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PATIENT_GEN_ID")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /*
     * docs.jboss.org/hibernate/annotations/3.5/reference/en/html/entity.html#entity-mapping-association
     */
    /*
     Это вариает OneToMany через вспомогательную таблицу, без форкнкея в подчиненной табл. Без обратной зависимости
     @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
     @JoinTable(name = "patient_visit", joinColumns = {
     @JoinColumn(name = "patient_id")}, inverseJoinColumns = {
     @JoinColumn(name = "visit_id")})
     * 
     */
    /* С форенкеем в зависимой, с обратной связью. mappedBy="patient" имя переменной, которая завязвнв на мастер-таблицу в зависимом классе
     @OneToMany(mappedBy="patient", fetch = FetchType.EAGER)
     * 
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @OrderBy(value = "date desc")
    private Set<Visit> visits = new HashSet<>();

    public Set<Visit> getVisits() {
        return visits;
    }

    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @OrderBy(value = "id desc")
    private Set<Attached> attached = new HashSet<>();

    public Set<Attached> getAttached() {
        return attached;
    }

    public void setAttached(Set<Attached> attached) {
        this.attached = attached;
    }
    /**
     * Имя пациента
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName = "";

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Фамилия пациента
     */
    @Column(name = "second_name", nullable = false, length = 100)
    @SortNatural()
    private String secondName = "";

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    /*
     * Отчество пациента
     *
     @Column(name = "middle_name", nullable = false, length = 100)
     private String middleName = "";

     public String getMiddleName() {
     return middleName;
     }

     public void setMiddleName(String middleName) {
     this.middleName = middleName;
     }
     */
    /**
     * Дата последнего посещения
     */
    @Column(name = "last_visit", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date lastVisit = new Date();

    public Date getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }
    /**
     * дата рождения
     */
    @Column(name = "birthday", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthday = new Date();

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * Сериализовые данные документа
     */
    @Column(name = "patient_data", nullable = false, length = 64000)
    private String patientData = "";

    public String getPatientData() {
        return patientData;
    }

    public void setPatientData(String patientData) {
        this.patientData = patientData;
    }

    //***********************************************************************************************************************************************************************
    //********* DAO
    //***********************************************************************************************************************************************************************
    synchronized static public void savePatient(Patient patient) throws ClientException {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            sess.saveOrUpdate(patient);
            sess.getTransaction().commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка сохранения пациента.", ex);
            sess.getTransaction().rollback();
            throw new ClientException("Ошибка сохранения пациента.", ex);
        } finally {
            sess.close();
        }
    }

    synchronized static public void removePatient(Patient patient) {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            Query query = sess.createQuery("delete Storage where patientId = :ID or docId in (:docIDs) or visitId in (:visitIDs)");
            query.setParameter("ID", patient.getId());
            final LinkedList<Long> vis = new LinkedList<>();
            final LinkedList<Long> dcs = new LinkedList<>();
            patient.getVisits().forEach((visit) -> {
                vis.add(visit.getId());
                visit.getDocs().forEach((doc) -> {
                    dcs.add(doc.getId());
                });
            });
            query.setParameterList("docIDs", dcs);
            query.setParameterList("visitIDs", vis);
            int result = query.executeUpdate();
            System.out.println("Storage deleted: " + result);
            sess.delete(patient);
            sess.getTransaction().commit();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка удаления пациента.", ex);
            sess.getTransaction().rollback();
        } finally {
            sess.close();
        }
    }

    /**
     * Criteria sc = session.createCriteria(clazz); ... // fix join return duplicates sc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
     * return sc.list();
     *
     *
     * Подразумевается что hashCode() и equals() вы у объектов уже поправили
     *
     * @param filter
     * @return
     */
    synchronized static public LinkedList<Patient> getPatients(HashMap<String, Object> filter) {
        final Session sess = HibernateUtil.getInstance().getSession();
        try {
            if (filter == null) {
                final LinkedList li = new LinkedList(sess.createCriteria(Patient.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list());
                if (li.isEmpty()) {
                    createIndexes();
                }
                return li;
                //  return new LinkedList(sess.createCriteria(Patient.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list());
                //  return new LinkedList(sess.createCriteria(Patient.class).list());
            } else {
                final Criteria criteria = sess.createCriteria(Patient.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                //final Criteria criteria = sess.createCriteria(Patient.class);
                filter.keySet().stream().forEach((key) -> {
                    criteria.add(Restrictions.eq(key, filter.get(key)));
                });
                //criteria.addOrder(Order.asc("second_name"));
                return new LinkedList<>(criteria.list());
            }
        } finally {
            sess.close();
        }
    }
    public static final String DB_URL = "jdbc:h2:~/H2/JournalDB";

    private static void createIndexes() {
        // jdbc Connection
        try {
            //Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            Class.forName("org.h2.Driver").newInstance();

            try (Statement stmt = DriverManager.getConnection(DB_URL, "diasled", "diasled").createStatement()) {
                stmt.execute("CREATE INDEX last_visit_idx ON patient (last_visit)");
                stmt.execute("CREATE INDEX birthday_idx ON patient (birthday)");
                stmt.execute("CREATE INDEX second_name_idx ON patient (second_name)");

                final StringBuffer sb = new StringBuffer();
                for (int i = 0; i < 256; i++) {
                    sb.append(",s").append(i).append(" SMALLINT");
                }
                stmt.execute("create table sensor (exam_id bigint, frame_id int " + sb.toString()
                        + ", sum_0 int, sum_1 int, sum_2 int, sum_3 int, sum_4 int, sum_5 int, sum_6 int, sum_7 int, sum_8 int, sum_9 int, sum_10 int, sum_11 int"
                        + ", b_x int, b_y int, b_z int" + ")");
                sb.setLength(0);
                stmt.execute("CREATE INDEX examid_idx ON sensor (exam_id)");
                //stmt.execute("CREATE INDEX diasled.frameid_idx ON diasled.sensor (frame_id)");
                //stmt.execute("CREATE INDEX diasled.sensorid_idx ON diasled.sensor (sensor_id)");
            }
        } catch (SQLException except) {
            System.out.println(except.getMessage());
            return;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException except) {
            QLog.l().logger().error("Indexes!", except);
        }
        QLog.l().logger().trace("Созданы индексы и таблица данных для сканирования.");
    }

    public String getName() {
        return getSecondName() + " " + getFirstName();
    }

    //***********************************************************************************************************************************************************************
    //********* SERVICES
    //***********************************************************************************************************************************************************************
    private static final ResourceBundle translate = ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal", Locales.getInstance().getLangCurrent());

    private static String loc(String key) {
        return translate.getString(key);
    }

    public String getTextInfo(IPatientController pc) {
        return "<span style='font-size:16.0pt;color:red'>"
                + "&nbsp;№" + getId()
                + "<br>&nbsp;"
                + getName()
                + "<br>&nbsp;"
                + loc("last_visit_date") + " "
                + Uses.format_dd_MM_yyyy.format(getLastVisit())
                + "<br> </span><span style='font-size:12.0pt;color:black'>"
                + pc.getTextView(this);
    }

    public int getAgeOnDate(Date date) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(getBirthday());
        final GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTime(date);
        return ((gc1.get(GregorianCalendar.YEAR) - gc.get(GregorianCalendar.YEAR)) + ((gc1.get(GregorianCalendar.DAY_OF_YEAR) - gc.get(GregorianCalendar.DAY_OF_YEAR)) >= 0 ? 0 : -1));
    }

    @Override
    public String toString() {
        return getName() + " " + Uses.format_dd_MM_yyyy.format(getBirthday());
    }
}
