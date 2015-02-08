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
import javax.swing.JPanel;
import ru.apertum.journal.IDocController;
import ru.apertum.journal.model.Document;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 *
 * @author Evgeniy Egorov
 */
public class PCommentDoc extends javax.swing.JPanel implements IDocController {

    /**
     * Creates new form PCommentDoc
     */
    public PCommentDoc() {
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

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        taComment.setColumns(20);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(PCommentDoc.class);
        taComment.setFont(resourceMap.getFont("taComment.font")); // NOI18N
        taComment.setRows(5);
        taComment.setName("taComment"); // NOI18N
        jScrollPane1.setViewportView(taComment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea taComment;
    // End of variables declaration//GEN-END:variables

    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public String getName() {
        return "Комментарии";
    }

    @Override
    public String getDocDescription() {
        return "Документ содержит только текстовые данные. Используйте его для описания или комментирования посещений.";
    }

    @Override
    public JPanel getGUI() {
        taComment.setText(data.getComments());
        return this;
    }

    @Override
    public void loadDocData(Document doc) {
        this.doc = doc;
        final Gson gson = GsonPool.getInstance().borrowGson();
        try {
            final Data d = gson.fromJson(doc.getDocData(), Data.class);
            data = (d == null) ? new Data() : d;
        } catch (JsonSyntaxException ex) {
            throw new ClientException("Не возможно интерпритировать данные документа.", ex);
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
    }

    @Override
    public Document saveDocData() {
        data.setComments(taComment.getText());

        final String damp;
        final Gson gson = GsonPool.getInstance().borrowGson();
        try {
            damp = gson.toJson(data);
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        doc.setDocData(damp);
        return doc;
    }

    private Data data = new Data();
    private Document doc;

    @Override
    public void print() {
    }

    @Override
    public void export() {
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