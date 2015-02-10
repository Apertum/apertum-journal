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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import ru.apertum.journal.IDocController;
import ru.apertum.journal.model.Attached;
import ru.apertum.journal.model.DocControllersList;
import ru.apertum.journal.model.Document;
import ru.apertum.journal.model.PatientBlank;
import ru.apertum.journal.model.Storage;
import ru.apertum.journal.model.Visit;
import ru.apertum.journal.model.exam.DocNode;
import ru.apertum.journal.model.exam.DocsTreeModel;
import ru.apertum.journal.model.exam.RootNodeDoc;
import ru.apertum.journal.model.patients.AttachedTableModel;
import ru.apertum.journal.model.patients.VisitsTableModel;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 *
 * @author Evgeniy Egorov
 */
public class FVisitEditor extends javax.swing.JFrame {
    
    private static final ResourceBundle trn = ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal", Locales.getInstance().getLangCurrent());
    
    private static String l(String key) {
        return trn.getString(key);
    }
    
    private Visit visit = null;

    /**
     *
     * @param visit не null
     * @param tModel модель для таблици посещений
     */
    public FVisitEditor(Visit visit, TableModel tModel) {
        initComponents();
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
            if (isChangedVisit() && JOptionPane.showConfirmDialog(null,
                    trn.getString("visit_changed"),
                    trn.getString("saving_visit"),
                    JOptionPane.YES_NO_OPTION) == 0) {
                saveVisit();
            } else {
                setVisible(false);
            }
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        try {
            setIconImage(ImageIO.read(FVisitEditor.class.getResource("/ru/apertum/journal/forms/resources/favicon_red.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        attachedTable.setModel(new AttachedTableModel());
        attachedTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        attachedTable.getColumnModel().getColumn(0).setWidth(10);
        attachedTable.getColumnModel().getColumn(0).setMaxWidth(50);
        attachedTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        attachedTable.getColumnModel().getColumn(0).setMaxWidth(1000);
        
        tablePatientVisits.setModel(tModel);
        tablePatientVisits.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (tablePatientVisits.getModel().getRowCount() != 0 && e.getFirstIndex() >= 0 && e.getLastIndex() >= 0 && tablePatientVisits.getSelectedRow() >= 0) {
                final Date date = (Date) tablePatientVisits.getModel().getValueAt(tablePatientVisits.getSelectedRow(), 1);
                final Visit v = ((VisitsTableModel) tablePatientVisits.getModel()).getVisitByDate(date);
                showData(v);
            }
        });
        selectByVisit(visit);
        
        tablePatientVisits.getColumnModel().getColumn(0).setPreferredWidth(10);
        tablePatientVisits.getColumnModel().getColumn(0).setWidth(10);
        tablePatientVisits.getColumnModel().getColumn(0).setMaxWidth(50);
        tablePatientVisits.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePatientVisits.getColumnModel().getColumn(0).setMaxWidth(1000);
    }

    /**
     * Показываем инфу о визите, выставляем текущий отображаемый визит
     *
     * @param patient
     * @param visit
     */
    private void showData(Visit visit) {
        QLog.l().logger().trace("Посмотрим визит \"" + visit.toString());
        if (this.visit != null && isChangedVisit() && JOptionPane.showConfirmDialog(this,
                trn.getString("visit_changed"),
                trn.getString("saving_visit"),
                JOptionPane.YES_NO_OPTION) == 0) {
            saveVisit();
            return;
        }
        this.visit = visit;
        setTitle(visit.toString());
        final boolean isNow = isNow(visit.getDate());
        labelPatientInfo.setContentType("text/html");
        labelPatientInfo.setText("<html>" + visit.getPatient().getTextInfo(PatientBlank.getInstance()));
        textFieldVisitDescription.setText(visit.getComments());
        labelVisitDate.setText(l("visit_date") + " " + Uses.format_dd_MM_yyyy.format(visit.getDate()));
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(visit.getPatient().getBirthday());
        labelAgeOnDate.setText(l("age_on_visit") + " " + visit.getPatient().getAgeOnDate(visit.getDate()));
        textFieldKod.setText(visit.getKod());
        spinnerWeightOnData.getModel().setValue(visit.getWeight());
        textAreaCommonStatus.setText(visit.getCommonStatus());
        textAreaLeftLeg.setText(visit.getLeftLegInfo());
        textAreaRightLeg.setText(visit.getRigthLegInfo());
        if (isNow) {
            // сегодняшнее посещение, новое
        } else {
            // смотрим старое посещение
        }
        textFieldVisitDescription.setEditable(isNow);
        spinnerWeightOnData.setEnabled(isNow);
        textFieldKod.setEditable(isNow);
        textAreaCommonStatus.setEditable(isNow);
        textAreaLeftLeg.setEditable(isNow);
        textAreaRightLeg.setEditable(isNow);
        
        treeExams.setModel(new DocsTreeModel(visit));
        
        buttonSave.setEnabled(isNow);
        buttonAddExam.setEnabled(isNow);
        buttonRemoveExam.setEnabled(isNow);
        
        btnAddAttached.setEnabled(isNow);
        btnRemoveAttached.setEnabled(isNow);
        
        buttonNextVisit.setEnabled(getVisitCloseOf(visit, true) != null);
        buttonPreviousVisit.setEnabled(getVisitCloseOf(visit, false) != null);

        //таблица приложений
        ((AttachedTableModel) attachedTable.getModel()).update(visit.getAttached());
        ((AttachedTableModel) attachedTable.getModel()).fireTableDataChanged();
        
    }

    /**
     * сегодняшняя ли это дата
     *
     * @param date
     * @return
     */
    public boolean isNow(Date date) {
        return isSameDates(date, new Date());
    }

    /**
     * одинаковые ли даты с точностью до дня
     *
     * @param date1
     * @param date2
     * @return
     */
    public boolean isSameDates(Date date1, Date date2) {
        final GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTime(date1);
        final GregorianCalendar gc2 = new GregorianCalendar();
        gc2.setTime(date2);
        return (gc1.get(GregorianCalendar.DAY_OF_YEAR) == gc2.get(GregorianCalendar.DAY_OF_YEAR)
                && gc1.get(GregorianCalendar.YEAR) == gc2.get(GregorianCalendar.YEAR));
    }
    
    private boolean isChangedVisit() {
        final boolean isNow = isNow(visit.getDate());
        return isNow
                && !(visit.getComments().equals(textFieldVisitDescription.getText())
                && visit.getKod().equals(textFieldKod.getText())
                && visit.getWeight().equals(spinnerWeightOnData.getModel().getValue())
                && visit.getCommonStatus().equals(textAreaCommonStatus.getText())
                && visit.getLeftLegInfo().equals(textAreaLeftLeg.getText())
                && visit.getRigthLegInfo().equals(textAreaRightLeg.getText()));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        buttonSave = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        labelVisitDate = new javax.swing.JLabel();
        labelAgeOnDate = new javax.swing.JLabel();
        spinnerWeightOnData = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        textFieldVisitDescription = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        textFieldKod = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        textAreaLeftLeg = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        textAreaRightLeg = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaCommonStatus = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        treeExams = new javax.swing.JTree();
        jPanel11 = new javax.swing.JPanel();
        buttonAddExam = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        buttonRemoveExam = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        btnAddAttached = new javax.swing.JButton();
        btnEditAttached = new javax.swing.JButton();
        btnRemoveAttached = new javax.swing.JButton();
        btnDownloadAttached = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        attachedTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        buttonNextVisit = new javax.swing.JButton();
        buttonPreviousVisit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePatientVisits = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        labelPatientInfo = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jSplitPane1.setDividerLocation(650);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setContinuousLayout(true);

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        buttonSave.setText(trn.getString("save_changes")); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonSave)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonSave)
                .addContainerGap())
        );

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));

        labelVisitDate.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        labelVisitDate.setText("Дата посещения 13.12.10");

        labelAgeOnDate.setText("Полных лет на дату посещения 91");

        jLabel3.setText(trn.getString("param_on_visit")); // NOI18N

        jLabel4.setText(trn.getString("description")); // NOI18N

        jLabel5.setText(trn.getString("code")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldVisitDescription))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelVisitDate)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerWeightOnData, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labelAgeOnDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldKod, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelVisitDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAgeOnDate)
                    .addComponent(textFieldKod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerWeightOnData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldVisitDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));

        jSplitPane2.setDividerLocation(100);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setContinuousLayout(true);

        jSplitPane3.setDividerLocation(100);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setContinuousLayout(true);

        textAreaLeftLeg.setColumns(20);
        textAreaLeftLeg.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaLeftLeg.setRows(1);
        textAreaLeftLeg.setBorder(javax.swing.BorderFactory.createTitledBorder(trn.getString("purpose_visit")));
        jScrollPane4.setViewportView(textAreaLeftLeg);

        jSplitPane3.setTopComponent(jScrollPane4);

        textAreaRightLeg.setColumns(20);
        textAreaRightLeg.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaRightLeg.setRows(1);
        textAreaRightLeg.setBorder(javax.swing.BorderFactory.createTitledBorder(trn.getString("comments")));
        jScrollPane5.setViewportView(textAreaRightLeg);

        jSplitPane3.setRightComponent(jScrollPane5);

        jSplitPane2.setBottomComponent(jSplitPane3);

        textAreaCommonStatus.setColumns(20);
        textAreaCommonStatus.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaCommonStatus.setRows(1);
        textAreaCommonStatus.setBorder(javax.swing.BorderFactory.createTitledBorder(trn.getString("resons_visit")));
        jScrollPane3.setViewportView(textAreaCommonStatus);

        jSplitPane2.setLeftComponent(jScrollPane3);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );

        jTabbedPane1.addTab(trn.getString("visit_info"), jPanel5); // NOI18N

        treeExams.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeExamsMouseClicked(evt);
            }
        });
        treeExams.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                treeExamsKeyTyped(evt);
            }
        });
        jScrollPane7.setViewportView(treeExams);

        jPanel11.setBorder(new javax.swing.border.MatteBorder(null));

        buttonAddExam.setText(trn.getString("create_doc")); // NOI18N
        buttonAddExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddExamActionPerformed(evt);
            }
        });

        jButton1.setText(trn.getString("show")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonRemoveExam.setText(trn.getString("remove_doc")); // NOI18N
        buttonRemoveExam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveExamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(90, Short.MAX_VALUE)
                .addComponent(buttonRemoveExam)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonAddExam)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddExam)
                    .addComponent(jButton1)
                    .addComponent(buttonRemoveExam))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(trn.getString("visit_created_docs"), jPanel8);

        jPanel12.setBorder(new javax.swing.border.MatteBorder(null));

        btnAddAttached.setText(trn.getString("add")); // NOI18N
        btnAddAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAttachedActionPerformed(evt);
            }
        });

        btnEditAttached.setText(trn.getString("change")); // NOI18N
        btnEditAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditAttachedActionPerformed(evt);
            }
        });

        btnRemoveAttached.setText(trn.getString("remove")); // NOI18N
        btnRemoveAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAttachedActionPerformed(evt);
            }
        });

        btnDownloadAttached.setText(trn.getString("download")); // NOI18N
        btnDownloadAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadAttachedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
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
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddAttached)
                    .addComponent(btnEditAttached)
                    .addComponent(btnRemoveAttached)
                    .addComponent(btnDownloadAttached))
                .addContainerGap())
        );

        attachedTable.setModel(new AttachedTableModel());
        jScrollPane6.setViewportView(attachedTable);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(trn.getString("attachments"), jPanel10);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setLeftComponent(jPanel4);

        jPanel7.setBorder(new javax.swing.border.MatteBorder(null));

        buttonNextVisit.setText(trn.getString("next_visit")); // NOI18N
        buttonNextVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNextVisitActionPerformed(evt);
            }
        });

        buttonPreviousVisit.setText(trn.getString("pred_visit")); // NOI18N
        buttonPreviousVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPreviousVisitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonPreviousVisit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonNextVisit)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonNextVisit)
                    .addComponent(buttonPreviousVisit))
                .addContainerGap())
        );

        tablePatientVisits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablePatientVisits.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tablePatientVisits);

        labelPatientInfo.setEditable(false);
        jScrollPane2.setViewportView(labelPatientInfo);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        saveVisit();
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonNextVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNextVisitActionPerformed
        nextVisit();
    }//GEN-LAST:event_buttonNextVisitActionPerformed

    private void buttonPreviousVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPreviousVisitActionPerformed
        previousVisit();
    }//GEN-LAST:event_buttonPreviousVisitActionPerformed

    private void treeExamsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeExamsKeyTyped
        if (evt.getKeyChar() == '\n') {
            showExam();
        }
    }//GEN-LAST:event_treeExamsKeyTyped

    private void treeExamsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeExamsMouseClicked
        if (evt.getClickCount() > 1) {
            showExam();
        }
    }//GEN-LAST:event_treeExamsMouseClicked

    private void buttonAddExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddExamActionPerformed
        createNewExam();
    }//GEN-LAST:event_buttonAddExamActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showExam();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void buttonRemoveExamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveExamActionPerformed
        removeExam();
    }//GEN-LAST:event_buttonRemoveExamActionPerformed

    private void btnAddAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAttachedActionPerformed
        addNewAttahed();
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
    
    public void showVisit() {
    }
    
    public void nextVisit() {
        final Visit v = getVisitCloseOf(visit, true);
        if (v != null) {
            showData(v);
            selectByVisit(v);
        }
    }
    
    public void previousVisit() {
        final Visit v = getVisitCloseOf(visit, false);
        if (v != null) {
            showData(v);
            selectByVisit(v);
        }
    }
    
    private void selectByVisit(Visit v) {
        for (int i = 0; i < tablePatientVisits.getModel().getRowCount(); i++) {
            if (isSameDates((Date) tablePatientVisits.getModel().getValueAt(i, 1), v.getDate())) {
                tablePatientVisits.getSelectionModel().addSelectionInterval(i, i);
                break;
            }
        }
    }

    /**
     * Получить ближайший визит относительно какого-то визита
     *
     * @param currentVisit относительно этого визита будет искаться ближайший визит
     * @param isNext true - следующий за, false - предыдущий за
     * @return ближайший визит, либо null если нет ближайшего в этом направлениии
     */
    private Visit getVisitCloseOf(Visit currentVisit, boolean isNext) {
        Visit res = null;
        for (Visit vis : currentVisit.getPatient().getVisits()) {
            if (vis.getId().equals(currentVisit.getId())) {
                continue;
            }
            boolean f = isNext ? vis.getDate().after(currentVisit.getDate()) : vis.getDate().before(currentVisit.getDate());
            if (f) {
                if (res == null) {
                    res = vis;
                } else {
                    f = isNext ? res.getDate().after(vis.getDate()) : res.getDate().before(vis.getDate());
                    if (f) {
                        res = vis;
                    }
                }
            }
        }
        return res;
    }
    
    public void createNewExam() {
        QLog.l().logger().trace("Создадим новый документ.");
        final IDocController blanc;
        if (DocControllersList.getInstance().getSize() == 1) {
            JOptionPane.showMessageDialog(this,
                    trn.getString("only_one_doc"),
                    trn.getString("doc_selection"),
                    JOptionPane.INFORMATION_MESSAGE);
            blanc = DocControllersList.getInstance().getElementAt(0);
        } else {
            blanc = FDocChooser.getDocument(trn.getString("choose_doc"), this, true);
        }
        if (blanc != null) {
            final Document doc = new Document();
            doc.setNumber("");
            doc.setDate(new Date());
            doc.setDocId(blanc.getId());
            doc.setVisit(visit);
            blanc.loadDocData(doc);
            final FDocMaster master = new FDocMaster(this, true, visit, blanc, doc);
            master.setLocationRelativeTo(null);
            master.setVisible(true);
            if (master.getResult()) {
                final TreeNode newExam = ((RootNodeDoc) treeExams.getModel().getRoot()).addDoc(doc);
                ((DocsTreeModel) treeExams.getModel()).reload();
                final TreeNode[] nodes = ((DocsTreeModel) treeExams.getModel()).getPathToRoot(newExam);
                final TreePath path = new TreePath(nodes);
                treeExams.scrollPathToVisible(path);
                treeExams.setSelectionPath(path);
            }
        }
    }
    
    public void removeExam() {
        QLog.l().logger().trace("Удалим документ.");
        final TreeNode node = (TreeNode) treeExams.getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && JOptionPane.showConfirmDialog(this,
                java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("will_doc_remove"), new Object[]{DocControllersList.getInstance().getById(((DocNode) node).getDoc().getDocId()).getName(), node.toString()}),
                trn.getString("removing_doc"),
                JOptionPane.YES_NO_OPTION) == 0) {
            final DocNode eNode = (DocNode) node;
            Document.removeDoc(eNode.getDoc());
            final TreeNode parentFolder = ((RootNodeDoc) treeExams.getModel().getRoot()).removeDoc(eNode.getDoc());
            ((DocsTreeModel) treeExams.getModel()).reload();
            final TreeNode[] nodes = ((DocsTreeModel) treeExams.getModel()).getPathToRoot(parentFolder);
            final TreePath path = new TreePath(nodes);
            treeExams.scrollPathToVisible(path);
            treeExams.setSelectionPath(path);
            treeExams.expandPath(path);
        }
    }
    
    public void showExam() {
        QLog.l().logger().trace("Посмотрим документ.");
        final TreeNode node = (TreeNode) treeExams.getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && node.getParent() != null) {
            final DocNode eNode = (DocNode) node;
            final IDocController blanc = DocControllersList.getInstance().getById(eNode.getDoc().getDocId());
            blanc.loadDocData(eNode.getDoc());
            final FDocMaster master = new FDocMaster(this, true, visit, blanc, eNode.getDoc());
            master.setLocationRelativeTo(null);
            master.setVisible(true);
            if (master.getResult()) {
                
            }
        }
    }
    
    public final void saveVisit() {
        QLog.l().logger().info("Сохраняем посещение. " + visit.getDate());
        visit.setComments(textFieldVisitDescription.getText());
        visit.setKod(textFieldKod.getText());
        visit.setWeight((Integer) spinnerWeightOnData.getModel().getValue());
        visit.setCommonStatus(textAreaCommonStatus.getText());
        visit.setLeftLegInfo(textAreaLeftLeg.getText());
        visit.setRigthLegInfo(textAreaRightLeg.getText());
        try {
            Visit.saveVisit(visit);
        } catch (ClientException ex) {
            JOptionPane.showMessageDialog(this,
                    trn.getString("error_save_visit"),
                    trn.getString("saving_visit"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                trn.getString("save_visit_sucsessful"),
                trn.getString("saving_visit"),
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void addNewAttahed() {
        
        QLog.l().logger().trace("Добавим новое вложение.");
        // если сегодня уже создали визит то его не нужно еще раз создавать, просто покажем его

        if (JOptionPane.showConfirmDialog(this,
                trn.getString("new_att_for_visit"),
                trn.getString("new_att"),
                JOptionPane.YES_NO_OPTION) == 1) {
            return;
        }
        final FAttachedDlg aa = new FAttachedDlg(this, true);
        Uses.setLocation(aa);
        aa.setVisible(true);
        if (aa.isOK()) {
            final Attached att;
            visit.getAttached().add(att = new Attached(visit));
            att.setFileName(aa.getFile().getName());
            att.setTitle(aa.getTitleDoc());
            att.setComments(aa.getComment());
            try {
                Attached.saveAttached(att);
                Storage.saveStorage(aa.getFile(), att.getId(), null, visit.getId(), null, att.getTitle());
            } catch (ClientException ex) {
                JOptionPane.showMessageDialog(this,
                        trn.getString("err_create_att"),
                        trn.getString("saving_att"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ((AttachedTableModel) attachedTable.getModel()).update(visit.getAttached());
            ((AttachedTableModel) attachedTable.getModel()).fireTableDataChanged();
        } else {
            if (aa.isOKpress()) {
                JOptionPane.showMessageDialog(this,
                        trn.getString("err_att_params"),
                        trn.getString("new_att"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void removeAttached() {
        if (!attachedTable.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Удалим вложение.");
            int row = attachedTable.convertRowIndexToModel(attachedTable.getSelectedRow());
            AttachedTableModel model = (AttachedTableModel) attachedTable.getModel();
            
            final Attached attached = model.getRowAt(row);
            if (attached == null || JOptionPane.showConfirmDialog(this,
                    java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("will_att_remove"), new Object[]{attached.getTitle()}),
                    trn.getString("removing_att"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем приложение \"" + attached.getTitle() + "\"");
            // удалим навсегда

            visit.getAttached().remove(attached);
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
            final FAttachedDlg aa = new FAttachedDlg(this, true);
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
                            trn.getString("err_create_att"),
                            trn.getString("saving_att"),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                ((AttachedTableModel) attachedTable.getModel()).update(visit.getAttached());
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
            fc.setCurrentDirectory(new File(trn.getString(".")));
            fc.setSelectedFile(new File(attached.getFileName()));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (fc.getSelectedFile().exists() && JOptionPane.showConfirmDialog(this,
                        java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal").getString("file_exists_remove"), new Object[]{fc.getSelectedFile().getName()}),
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
    private javax.swing.JButton buttonAddExam;
    private javax.swing.JButton buttonNextVisit;
    private javax.swing.JButton buttonPreviousVisit;
    private javax.swing.JButton buttonRemoveExam;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelAgeOnDate;
    private javax.swing.JTextPane labelPatientInfo;
    private javax.swing.JLabel labelVisitDate;
    private javax.swing.JSpinner spinnerWeightOnData;
    private javax.swing.JTable tablePatientVisits;
    private javax.swing.JTextArea textAreaCommonStatus;
    private javax.swing.JTextArea textAreaLeftLeg;
    private javax.swing.JTextArea textAreaRightLeg;
    private javax.swing.JTextField textFieldKod;
    private javax.swing.JTextField textFieldVisitDescription;
    private javax.swing.JTree treeExams;
    // End of variables declaration//GEN-END:variables
}
