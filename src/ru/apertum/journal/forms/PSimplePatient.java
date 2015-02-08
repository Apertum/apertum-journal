/*
 * Copyright (C) 2014 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.journal.forms;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import javax.swing.JPanel;
import ru.apertum.journal.IPatientController;
import ru.apertum.journal.model.Patient;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 *
 * @author Evgeniy Egorov
 */
public class PSimplePatient extends javax.swing.JPanel implements IPatientController {

    /**
     * Creates new form PSimplePatient
     */
    public PSimplePatient() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        taComment = new javax.swing.JTextArea();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(PSimplePatient.class);
        setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("Form.border.title"))); // NOI18N
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        taComment.setColumns(20);
        taComment.setFont(resourceMap.getFont("taComment.font")); // NOI18N
        taComment.setRows(5);
        taComment.setName("taComment"); // NOI18N
        jScrollPane1.setViewportView(taComment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea taComment;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getDescription() {
        return "Простая форма по умолчанию.";
    }

    @Override
    public JPanel getGUI() {
        taComment.setText(data.getComments());
        return this;
    }

    private final static HashMap<Patient, PSimplePatient.Data> cache = new HashMap();

    @Override
    public void loadPatientData(Patient patient) {
        this.patient = patient;
        final Gson gson = GsonPool.getInstance().borrowGson();
        try {
            final PSimplePatient.Data d = gson.fromJson(patient.getPatientData(), PSimplePatient.Data.class);
            data = (d == null) ? new PSimplePatient.Data() : d;
        } catch (JsonSyntaxException ex) {
            throw new ClientException("Не возможно интерпритировать данные документа.", ex);
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        cache.put(patient, data);
        getGUI();
    }

    @Override
    public Patient savePatientData() {
        data.setComments(taComment.getText());

        final String damp;
        final Gson gson = GsonPool.getInstance().borrowGson();
        try {
            damp = gson.toJson(data);
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        patient.setPatientData(damp);
        cache.put(patient, data);
        return patient;
    }

    private PSimplePatient.Data data = new PSimplePatient.Data();
    private Patient patient;

    @Override
    public void print() {
        
    }

    @Override
    public void export() {
        
    }

    @Override
    public String caption1() {
        //return "";
        return "Фамилия..";
    }

    @Override
    public String caption2() {
        //return "";
        return "Имя, отчество..";
    }

    @Override
    public String caption3() {
        //return "";
        return "Дата рождения..";
    }

    @Override
    public Long getId() {
        return 17L;
    }

    @Override
    public int extColumnsCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int columnIndex, Patient patient) {
        Data d = cache.get(patient);
        if (d == null) {
            loadPatientData(patient);
            d = data;
            cache.put(patient, data);
        }
        switch (columnIndex) {
            case 0:
                return d.comments;
            default:
                throw new AssertionError(columnIndex);
        }
    }

    @Override
    public String getExtColumnName(int column) {
        switch (column) {
            case 0:
                return "Комментарии";
            default:
                throw new AssertionError(column);
        }
    }

    @Override
    public Class<?> getExtColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            default:
                throw new AssertionError(columnIndex);
        }
    }

    @Override
    public String getTextView(Patient patient) {
        Data d = cache.get(patient);
        if (d == null) {
            loadPatientData(patient);
            d = data;
            cache.put(patient, data);
        }
        return "&nbsp;Комментарии:<br>" + d.comments;
    }

    private static class Data {

        public Data() {
        }

        public Data(String cmt) {
            comments = cmt;
        }

        @Expose
        @SerializedName("comments")
        private String comments = "";

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
