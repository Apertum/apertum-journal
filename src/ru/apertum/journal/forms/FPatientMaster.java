/*
 * Copyright (C) 2015 Evgeniy Egorov
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

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import ru.apertum.journal.IPatientController;
import ru.apertum.journal.model.Attached;
import ru.apertum.journal.model.Patient;
import ru.apertum.journal.model.PatientBlank;
import ru.apertum.journal.model.Storage;
import ru.apertum.journal.model.patients.AttachedTableModel;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 *
 * @author Evgeniy Egorov
 */
public class FPatientMaster extends javax.swing.JDialog {

    public static FPatientMaster getInstance() {
        return NewSingletonHolder.INSTANCE;
    }

    private static class NewSingletonHolder {

        private static final FPatientMaster INSTANCE = new FPatientMaster(null, true);
    }

    /**
     * Creates new form FPatientMaster
     *
     * @param parent
     * @param modal
     */
    public FPatientMaster(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // будет закрывать по esc
        final ActionListener escListener = (ActionEvent e) -> {
            setVisible(false);
        };

        getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        try {
            setIconImage(ImageIO.read(FPatientMaster.class.getResource("/ru/apertum/journal/forms/resources/favicon_yellow.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }

        attachedTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        attachedTable.getColumnModel().getColumn(0).setWidth(10);
        attachedTable.getColumnModel().getColumn(0).setMaxWidth(50);
        attachedTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        attachedTable.getColumnModel().getColumn(0).setMaxWidth(1000);

        label1.setVisible(PatientBlank.getInstance().caption1() != null && !PatientBlank.getInstance().caption1().isEmpty());
        textFieldSecondName.setVisible(PatientBlank.getInstance().caption1() != null && !PatientBlank.getInstance().caption1().isEmpty());
        label1.setText(PatientBlank.getInstance().caption1());

        label2.setVisible(PatientBlank.getInstance().caption2() != null && !PatientBlank.getInstance().caption2().isEmpty());
        textFieldFirstName.setVisible(PatientBlank.getInstance().caption2() != null && !PatientBlank.getInstance().caption2().isEmpty());
        label2.setText(PatientBlank.getInstance().caption2());

        label3.setVisible(PatientBlank.getInstance().caption3() != null && !PatientBlank.getInstance().caption3().isEmpty());
        spinnerBirthday.setVisible(PatientBlank.getInstance().caption3() != null && !PatientBlank.getInstance().caption3().isEmpty());
        label3.setText(PatientBlank.getInstance().caption3());
    }

    /**
     * Диалог редактирования или создания нового пациента.
     *
     * @param patient пациент для редактирования, может быть null, в этом случае создается новый пациент.
     * @return готовый или отредактированный пациент. Если вернет null, значит был отказ от редактирования.
     */
    static Patient editPatient(Patient patient, IPatientController pc) {
        getInstance().setTitle(java.text.MessageFormat.format(trn.getString("client_title_master"), new Object[] {(patient == null ? "Новый" : (patient.getName()))}));
        getInstance().setPatient(patient, pc);
        getInstance().setLocationRelativeTo(null);
        flag = false;
        getInstance().setVisible(true);
        return flag ? getInstance().getPatient() : null;
    }
    private static boolean flag = false;
    private static final ResourceBundle trn = ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal", Locales.getInstance().getLangCurrent());
    private Patient patient = null;
    private IPatientController blanc = null;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient, IPatientController pc) {
        this.patient = patient;
        this.blanc = pc;
        panel.removeAll();
        panel.setLayout(new GridLayout(1, 1));
        panel.add(blanc.getGUI());

        if (patient == null) {
            textFieldFirstName.setText("");
            textFieldSecondName.setText("");
            spinnerBirthday.setDate(new Date());
        } else {
            textFieldFirstName.setText(patient.getFirstName());
            textFieldSecondName.setText(patient.getSecondName());
            spinnerBirthday.setDate(patient.getBirthday());
            blanc.loadPatientData(patient);

            //таблица приложений
            ((AttachedTableModel) (attachedTable.getModel())).update(patient.getAttached());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelButtons = new javax.swing.JPanel();
        buttonApply = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonPrint = new javax.swing.JButton();
        buttonExport = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnRemoveAttached = new javax.swing.JButton();
        btnAddAttached = new javax.swing.JButton();
        btnEditAttached = new javax.swing.JButton();
        btnDownloadAttached = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        attachedTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        textFieldSecondName = new javax.swing.JTextField();
        textFieldFirstName = new javax.swing.JTextField();
        label1 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        spinnerBirthday = new com.toedter.calendar.JDateChooser();
        label3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelButtons.setBorder(new javax.swing.border.MatteBorder(null));

        buttonApply.setBackground(new java.awt.Color(255, 255, 0));
        buttonApply.setText(trn.getString("apply")); // NOI18N
        buttonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonApplyActionPerformed(evt);
            }
        });

        buttonCancel.setBackground(new java.awt.Color(255, 0, 0));
        buttonCancel.setText(trn.getString("cancel")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonPrint.setText(trn.getString("print")); // NOI18N
        buttonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPrintActionPerformed(evt);
            }
        });

        buttonExport.setText(trn.getString("export")); // NOI18N
        buttonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportActionPerformed(evt);
            }
        });

        buttonOK.setBackground(new java.awt.Color(0, 255, 0));
        buttonOK.setText(trn.getString("ok")); // NOI18N
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonApply, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonPrint)
                    .addComponent(buttonExport)
                    .addComponent(buttonApply)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 367, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(trn.getString("params"), panel); // NOI18N

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));

        btnRemoveAttached.setBackground(new java.awt.Color(255, 0, 0));
        btnRemoveAttached.setText(trn.getString("remove")); // NOI18N
        btnRemoveAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAttachedActionPerformed(evt);
            }
        });

        btnAddAttached.setBackground(new java.awt.Color(0, 255, 0));
        btnAddAttached.setText(trn.getString("add")); // NOI18N
        btnAddAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAttachedActionPerformed(evt);
            }
        });

        btnEditAttached.setBackground(new java.awt.Color(255, 255, 0));
        btnEditAttached.setText(trn.getString("edit")); // NOI18N
        btnEditAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditAttachedActionPerformed(evt);
            }
        });

        btnDownloadAttached.setText(trn.getString("download")); // NOI18N
        btnDownloadAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadAttachedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDownloadAttached)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemoveAttached)
                .addGap(18, 18, 18)
                .addComponent(btnEditAttached)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddAttached)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoveAttached)
                    .addComponent(btnAddAttached)
                    .addComponent(btnEditAttached)
                    .addComponent(btnDownloadAttached))
                .addContainerGap())
        );

        attachedTable.setModel(new AttachedTableModel());
        jScrollPane1.setViewportView(attachedTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(trn.getString("attachments"), jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));

        textFieldSecondName.setText("jTextField1");

        textFieldFirstName.setText("jTextField2");

        label1.setText(trn.getString("sourname")); // NOI18N

        label2.setText(trn.getString("name")); // NOI18N

        label3.setText(trn.getString("date_of_birth")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label1)
                            .addComponent(label2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldFirstName)
                            .addComponent(textFieldSecondName)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(label3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldSecondName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spinnerBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonApplyActionPerformed
        apply();
        JOptionPane.showMessageDialog(this,
                trn.getString("client_data_was_saved_successfully"),
                trn.getString("saving"),
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_buttonApplyActionPerformed

    private void apply() {
        // Обязательные поля
        if (textFieldSecondName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("enter_param"), new Object[] {PatientBlank.getInstance().caption1()}),
                    trn.getString("not_enaught_data"),
                    JOptionPane.NO_OPTION);
            textFieldSecondName.requestFocusInWindow();
            return;
        }
        /*
         if (Math.abs(((Date) spinnerBirthday.getDate()).getTime() - new Date().getTime()) / 1000000 < 93312) {
         JOptionPane.showMessageDialog(null,
         "Укажите дату рождения пациента",
         "Не полные данные",
         JOptionPane.NO_OPTION);
         spinnerBirthday.requestFocusInWindow();
         return;
         }
         */

        if (patient == null) {
            patient = new Patient();
        }
        patient.setFirstName(textFieldFirstName.getText());
        patient.setSecondName(textFieldSecondName.getText());
        patient.setBirthday((Date) spinnerBirthday.getDate());
        patient.setPatientData(blanc.savePatientData().getPatientData());

        try {
            Patient.savePatient(patient);
        } catch (ClientException ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(this,
                    trn.getString("error_save_client"),
                    trn.getString("saving"),
                    JOptionPane.ERROR_MESSAGE);
        }

        flag = true;
    }


    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void btnAddAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAttachedActionPerformed
        if (patient == null || patient.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    trn.getString("not_saved_client_need_save_before"),
                    trn.getString("new_attachment"),
                    JOptionPane.WARNING_MESSAGE);
        } else {
            addNewAttahed();
        }
    }//GEN-LAST:event_btnAddAttachedActionPerformed

    private void btnEditAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditAttachedActionPerformed
        editAttached();
    }//GEN-LAST:event_btnEditAttachedActionPerformed

    private void btnRemoveAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAttachedActionPerformed
        removeAttached();
    }//GEN-LAST:event_btnRemoveAttachedActionPerformed

    private void btnDownloadAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadAttachedActionPerformed
        try {
            downloadAttached();
        } catch (IOException ex) {
            throw new RuntimeException("No blob. ", ex);
        }
    }//GEN-LAST:event_btnDownloadAttachedActionPerformed

    private void buttonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPrintActionPerformed
        PatientBlank.getInstance().print();
    }//GEN-LAST:event_buttonPrintActionPerformed

    private void buttonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportActionPerformed
        PatientBlank.getInstance().export();
    }//GEN-LAST:event_buttonExportActionPerformed

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        apply();
        setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed

    public void addNewAttahed() {

        QLog.l().logger().trace("Добавим новое вложение пациенту.");
        // если сегодня уже создали визит то его не нужно еще раз создавать, просто покажем его

        if (JOptionPane.showConfirmDialog(this,
                trn.getString("will_add_new_att_for_client"),
                trn.getString("new_attachment"),
                JOptionPane.YES_NO_OPTION) == 1) {
            return;
        }
        final FAttachedDlg aa = new FAttachedDlg(null, true);
        Uses.setLocation(aa);
        aa.setVisible(true);
        if (aa.isOK()) {
            Attached att;
            patient.getAttached().add(att = new Attached(patient));
            att.setFileName(aa.getFile().getName());
            att.setTitle(aa.getTitleDoc());
            att.setComments(aa.getComment());
            try {
                Attached.saveAttached(att);
                Storage.saveStorage(aa.getFile(), att.getId(), patient.getId(), null, null, att.getTitle());
            } catch (ClientException ex) {
                JOptionPane.showMessageDialog(this,
                        trn.getString("error_create_att"),
                        trn.getString("saving_att"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ((AttachedTableModel) attachedTable.getModel()).update(patient.getAttached());
            ((AttachedTableModel) attachedTable.getModel()).fireTableDataChanged();
        } else {
            if (aa.isOKpress()) {
                JOptionPane.showMessageDialog(this,
                        trn.getString("discorrec_data_for_att"),
                        trn.getString("new_attachment"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void removeAttached() {
        if (!attachedTable.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Удалим вложение клиента.");
            int row = attachedTable.convertRowIndexToModel(attachedTable.getSelectedRow());
            AttachedTableModel model = (AttachedTableModel) attachedTable.getModel();

            final Attached attached = model.getRowAt(row);
            if (attached == null || JOptionPane.showConfirmDialog(this,
                    java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("will_add_remove"), new Object[] {attached.getTitle()}),
                    trn.getString("remove_att"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("removing_the_att"), new Object[] {attached.getTitle()}));
            // удалим навсегда

            patient.getAttached().remove(attached);
            model.removeAttached(attached);
            Attached.removeAttached(attached);
        }
    }

    public void editAttached() {
        if (!attachedTable.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("с вложение.");
            int row = attachedTable.convertRowIndexToModel(attachedTable.getSelectedRow());
            AttachedTableModel model = (AttachedTableModel) attachedTable.getModel();

            final Attached attached = model.getRowAt(row);

            QLog.l().logger().debug("Редактируем приложение \"" + attached.getTitle() + "\"");
            // удалим навсегда
            final FAttachedDlg aa = new FAttachedDlg(null, true);
            Uses.setLocation(aa);
            aa.setComment(attached.getComments());
            aa.setTitleDoc(attached.getTitle());
            aa.setNoFile();

            aa.setVisible(true);
            if (aa.isOKnoFile()) {
                attached.setTitle(aa.getTitleDoc());
                attached.setComments(aa.getComment());
                try {
                    Attached.saveAttached(attached);
                } catch (ClientException ex) {
                    JOptionPane.showMessageDialog(this,
                            trn.getString("error_create_att"),
                            trn.getString("saving_att"),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ((AttachedTableModel) attachedTable.getModel()).update(patient.getAttached());
                ((AttachedTableModel) attachedTable.getModel()).fireTableDataChanged();
            }
        }
    }

    public void downloadAttached() throws FileNotFoundException, IOException {

        if (!attachedTable.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Загрузим вложение.");
            int row = attachedTable.convertRowIndexToModel(attachedTable.getSelectedRow());
            AttachedTableModel model = (AttachedTableModel) attachedTable.getModel();

            final Attached attached = model.getRowAt(row);
            if (attached == null) {
                return;
            }
            QLog.l().logger().debug("Загрузим приложение \"" + attached.getTitle() + "\"");

            final byte[] bb = Storage.loadStorage(attached.getId());

            final JFileChooser fc = new JFileChooser();
            fc.setDialogTitle(trn.getString("saving_att"));
            fc.setCurrentDirectory(new File("."));
            fc.setSelectedFile(new File(attached.getFileName()));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (fc.getSelectedFile().exists() && JOptionPane.showConfirmDialog(this,
                        java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("replace_file"), new Object[] {fc.getSelectedFile().getName()}),
                        trn.getString("saving_att"),
                        JOptionPane.YES_NO_OPTION) == 1) {
                    return;
                }

                try (FileOutputStream outputStream = new FileOutputStream(fc.getSelectedFile())) {
                    outputStream.write(bb);
                    outputStream.flush();
                }

                final Desktop desktop = Desktop.getDesktop();
                desktop.open(fc.getSelectedFile().getParentFile());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable attachedTable;
    private javax.swing.JButton btnAddAttached;
    private javax.swing.JButton btnDownloadAttached;
    private javax.swing.JButton btnEditAttached;
    private javax.swing.JButton btnRemoveAttached;
    private javax.swing.JButton buttonApply;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonExport;
    private javax.swing.JButton buttonOK;
    private javax.swing.JButton buttonPrint;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelButtons;
    private com.toedter.calendar.JDateChooser spinnerBirthday;
    private javax.swing.JTextField textFieldFirstName;
    private javax.swing.JTextField textFieldSecondName;
    // End of variables declaration//GEN-END:variables
}
