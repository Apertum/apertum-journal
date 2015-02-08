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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import ru.apertum.journal.db.HibernateUtil;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Блобы с файлами Списки приложенных этих файлов к посещению и к документам
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
@Entity
@Table(name = "jrl_storage")
public class Storage implements Serializable {

    public Storage() {
    }

    public Storage(Long id) {
        this.id = id;
    }

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "storage", nullable = true)
    @Lob
    private java.sql.Blob storage;

    public Blob getStorage() {
        return storage;
    }

    public void setStorage(Blob storage) {
        this.storage = storage;
    }

    @Id
    @Column(name = "id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "attached_id")
    private Long attachedId;

    public Long getAttachedId() {
        return attachedId;
    }

    public void setAttachedId(Long attachedId) {
        this.attachedId = attachedId;
    }

    @Column(name = "patient_id")
    private Long patientId;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    @Column(name = "visit_id")
    private Long visitId;

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    @Column(name = "doc_id")
    private Long docId;

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getDocId() {
        return docId;
    }

    @Column(name = "title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "file_name")
    private String file_name;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    //***********************************************************************************************************************************************************************
    //********* DAO
    //***********************************************************************************************************************************************************************
    synchronized static public void saveStorage(File file, Long attId, Long patientId, Long visitId, Long docId, String title) throws ClientException {
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            final Storage st = new Storage(attId);
            st.setPatientId(patientId);
            st.setVisitId(visitId);
            st.setDocId(docId);
            st.setTitle(title);
            st.setFile_name(file.getName());
            final FileInputStream inputStream = new FileInputStream(file);
            Blob blob = Hibernate.getLobCreator(sess).createBlob(inputStream, file.length());
            st.setStorage(blob);
            sess.save(st);
            sess.getTransaction().commit();
            blob.free();
        } catch (FileNotFoundException | SQLException ex) {
            QLog.l().logger().error("Ошибка сохранения посещения.", ex);
            sess.getTransaction().rollback();
            throw new ClientException("Ошибка сохранения посещения.", ex);
        } finally {
            sess.close();
        }
    }

    synchronized static public byte[] loadStorage(Long attId) throws ClientException {
        byte[] blobBytes;
        final Session sess = HibernateUtil.getInstance().getSession();
        sess.beginTransaction();
        try {
            final Storage st = (Storage) sess.get(Storage.class, attId);
            final Blob blob = st.getStorage();
            blobBytes = blob.getBytes(1, (int) blob.length());
            blob.free();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка сохранения посещения.", ex);
            throw new ClientException("Ошибка сохранения посещения.", ex);
        } finally {
            sess.close();
        }
        return blobBytes;
    }

}
