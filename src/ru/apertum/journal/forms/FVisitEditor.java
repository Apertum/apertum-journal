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
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.application.Action;
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
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Форма отображения информации по конкретному посещению. Имеет возможность переключения на следующее/предыдущее посещение или на произвольное.
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class FVisitEditor extends javax.swing.JFrame {

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
                    "Данные по посещению были изменены. Сохранить изменения?",
                    "Сохранение посещения",
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
                "Данные о визите были изменены. Сохранить изменения?",
                "Сохранение визита",
                JOptionPane.YES_NO_OPTION) == 0) {
            saveVisit();
            return;
        }
        this.visit = visit;
        final boolean isNow = isNow(visit.getDate());
        labelPatientInfo.setText("<html>" + visit.getPatient().getTextInfo(PatientBlank.getInstance()));
        textFieldVisitDescription.setText(visit.getComments());
        labelVisitDate.setText("Дата визита " + Uses.format_dd_MM_yyyy.format(visit.getDate()));
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(visit.getPatient().getBirthday());
        labelAgeOnDate.setText("Полных лет на дату посещения " + visit.getPatient().getAgeOnDate(visit.getDate()));
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

        org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FVisitEditor.class, this).get("saveVisit").setEnabled(isNow);

        org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FVisitEditor.class, this).get("createNewExam").setEnabled(isNow);
        org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FVisitEditor.class, this).get("removeExam").setEnabled(isNow);

        btnAddAttached.setEnabled(isNow);
        btnRemoveAttached.setEnabled(isNow);

        buttonNextVisit.setEnabled(getVisitCloseOf(visit, true) != null);
        buttonPreviousVisit.setEnabled(getVisitCloseOf(visit, false) != null);

        //таблица приложений
        ((AttachedTableModel) (attachedTable.getModel())).update(visit.getAttached());

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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelPatientInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablePatientVisits = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        labelAgeOnDate = new javax.swing.JLabel();
        spinnerWeightOnData = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        labelVisitDate = new javax.swing.JLabel();
        textFieldKod = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textFieldVisitDescription = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        buttonNextVisit = new javax.swing.JButton();
        buttonPreviousVisit = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        textAreaLeftLeg = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        textAreaRightLeg = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaCommonStatus = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        buttonAddExam = new javax.swing.JButton();
        buttonRemoveExam = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        treeExams = new javax.swing.JTree();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btnRemoveAttached = new javax.swing.JButton();
        btnAddAttached = new javax.swing.JButton();
        btnEditAttached = new javax.swing.JButton();
        btnDownloadAttached = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        attachedTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FVisitEditor.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setDividerLocation(505);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        labelPatientInfo.setText(resourceMap.getString("labelPatientInfo.text")); // NOI18N
        labelPatientInfo.setToolTipText(resourceMap.getString("labelPatientInfo.toolTipText")); // NOI18N
        labelPatientInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelPatientInfo.setName("labelPatientInfo"); // NOI18N
        jScrollPane1.setViewportView(labelPatientInfo);

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel2.setName("jPanel2"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tablePatientVisits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "20.05.2004", "Общее направление"},
                {"2", "14.06.2004", "Повторное обследование"},
                {"3", "13.12.10", "Профилактика"},
                {null, null, null}
            },
            new String [] {
                "№", "Дата", "Описание"
            }
        ));
        tablePatientVisits.setName("tablePatientVisits"); // NOI18N
        tablePatientVisits.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tablePatientVisits);
        if (tablePatientVisits.getColumnModel().getColumnCount() > 0) {
            tablePatientVisits.getColumnModel().getColumn(0).setPreferredWidth(15);
            tablePatientVisits.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tablePatientVisits.columnModel.title0")); // NOI18N
            tablePatientVisits.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tablePatientVisits.columnModel.title1")); // NOI18N
            tablePatientVisits.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tablePatientVisits.columnModel.title2")); // NOI18N
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        jPanel3.setName("jPanel3"); // NOI18N

        jPanel4.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.setName("jPanel4"); // NOI18N

        labelAgeOnDate.setText(resourceMap.getString("labelAgeOnDate.text")); // NOI18N
        labelAgeOnDate.setName("labelAgeOnDate"); // NOI18N

        spinnerWeightOnData.setModel(new javax.swing.SpinnerNumberModel(75, 3, 475, 1));
        spinnerWeightOnData.setName("spinnerWeightOnData"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        labelVisitDate.setFont(resourceMap.getFont("labelVisitDate.font")); // NOI18N
        labelVisitDate.setText(resourceMap.getString("labelVisitDate.text")); // NOI18N
        labelVisitDate.setName("labelVisitDate"); // NOI18N

        textFieldKod.setText(resourceMap.getString("textFieldKod.text")); // NOI18N
        textFieldKod.setName("textFieldKod"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        textFieldVisitDescription.setText(resourceMap.getString("textFieldVisitDescription.text")); // NOI18N
        textFieldVisitDescription.setName("textFieldVisitDescription"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelVisitDate)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(labelAgeOnDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(textFieldKod, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(spinnerWeightOnData, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(textFieldVisitDescription)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelVisitDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAgeOnDate)
                    .addComponent(textFieldKod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinnerWeightOnData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldVisitDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel5.setName("jPanel5"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FVisitEditor.class, this);
        buttonNextVisit.setAction(actionMap.get("nextVisit")); // NOI18N
        buttonNextVisit.setName("buttonNextVisit"); // NOI18N

        buttonPreviousVisit.setAction(actionMap.get("previousVisit")); // NOI18N
        buttonPreviousVisit.setName("buttonPreviousVisit"); // NOI18N

        buttonSave.setAction(actionMap.get("saveVisit")); // NOI18N
        buttonSave.setBackground(resourceMap.getColor("buttonSave.background")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonPreviousVisit)
                .addGap(18, 18, 18)
                .addComponent(buttonSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonNextVisit)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonNextVisit)
                    .addComponent(buttonPreviousVisit)
                    .addComponent(buttonSave))
                .addContainerGap())
        );

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jSplitPane2.setDividerLocation(100);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setContinuousLayout(true);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jSplitPane3.setDividerLocation(90);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        textAreaLeftLeg.setColumns(20);
        textAreaLeftLeg.setFont(resourceMap.getFont("textAreaLeftLeg.font")); // NOI18N
        textAreaLeftLeg.setRows(5);
        textAreaLeftLeg.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textAreaLeftLeg.border.title"))); // NOI18N
        textAreaLeftLeg.setName("textAreaLeftLeg"); // NOI18N
        jScrollPane4.setViewportView(textAreaLeftLeg);

        jSplitPane3.setTopComponent(jScrollPane4);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        textAreaRightLeg.setColumns(20);
        textAreaRightLeg.setFont(resourceMap.getFont("textAreaRightLeg.font")); // NOI18N
        textAreaRightLeg.setRows(5);
        textAreaRightLeg.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textAreaRightLeg.border.title"))); // NOI18N
        textAreaRightLeg.setName("textAreaRightLeg"); // NOI18N
        jScrollPane5.setViewportView(textAreaRightLeg);

        jSplitPane3.setRightComponent(jScrollPane5);

        jSplitPane2.setBottomComponent(jSplitPane3);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        textAreaCommonStatus.setColumns(20);
        textAreaCommonStatus.setFont(resourceMap.getFont("textAreaCommonStatus.font")); // NOI18N
        textAreaCommonStatus.setRows(5);
        textAreaCommonStatus.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textAreaCommonStatus.border.title"))); // NOI18N
        textAreaCommonStatus.setName("textAreaCommonStatus"); // NOI18N
        jScrollPane3.setViewportView(textAreaCommonStatus);

        jSplitPane2.setLeftComponent(jScrollPane3);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        jPanel8.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel8.setName("jPanel8"); // NOI18N

        buttonAddExam.setAction(actionMap.get("createNewExam")); // NOI18N
        buttonAddExam.setText(resourceMap.getString("buttonAddExam.text")); // NOI18N
        buttonAddExam.setName("buttonAddExam"); // NOI18N

        buttonRemoveExam.setAction(actionMap.get("removeExam")); // NOI18N
        buttonRemoveExam.setText(resourceMap.getString("buttonRemoveExam.text")); // NOI18N
        buttonRemoveExam.setName("buttonRemoveExam"); // NOI18N

        jButton1.setAction(actionMap.get("showExam")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(110, Short.MAX_VALUE)
                .addComponent(buttonRemoveExam)
                .addGap(22, 22, 22)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonAddExam)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddExam)
                    .addComponent(buttonRemoveExam)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("В типе обови 1");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Вкладки типа 1");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Стоя 11:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Ходьба 13:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Бег 12:13");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Вкладки типа 2");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Стоя 11:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Ходьба 13:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Бег 12:13");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("В типе обови 2");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Вкладки типа 1");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Стоя 11:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Ходьба 13:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Бег 12:13");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Вкладки типа 2");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Стоя 11:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Ходьба 13:45");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Бег 12:13");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeExams.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeExams.setName("treeExams"); // NOI18N
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
        jScrollPane6.setViewportView(treeExams);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        jPanel9.setName("jPanel9"); // NOI18N

        jPanel10.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel10.setName("jPanel10"); // NOI18N

        btnRemoveAttached.setText(resourceMap.getString("btnRemoveAttached.text")); // NOI18N
        btnRemoveAttached.setName("btnRemoveAttached"); // NOI18N
        btnRemoveAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAttachedActionPerformed(evt);
            }
        });

        btnAddAttached.setText(resourceMap.getString("btnAddAttached.text")); // NOI18N
        btnAddAttached.setName("btnAddAttached"); // NOI18N
        btnAddAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAttachedActionPerformed(evt);
            }
        });

        btnEditAttached.setText(resourceMap.getString("btnEditAttached.text")); // NOI18N
        btnEditAttached.setName("btnEditAttached"); // NOI18N
        btnEditAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditAttachedActionPerformed(evt);
            }
        });

        btnDownloadAttached.setText(resourceMap.getString("btnDownloadAttached.text")); // NOI18N
        btnDownloadAttached.setName("btnDownloadAttached"); // NOI18N
        btnDownloadAttached.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadAttachedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
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
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoveAttached)
                    .addComponent(btnAddAttached)
                    .addComponent(btnEditAttached)
                    .addComponent(btnDownloadAttached))
                .addContainerGap())
        );

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        attachedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        attachedTable.setName("attachedTable"); // NOI18N
        attachedTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane8.setViewportView(attachedTable);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel9.TabConstraints.tabTitle"), jPanel9); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setLeftComponent(jPanel3);

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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (isChangedVisit() && JOptionPane.showConfirmDialog(this,
                "Данные о визите были изменены. Сохранить изменения?",
                "Сохранение визита",
                JOptionPane.YES_NO_OPTION) == 0) {
            saveVisit();
        }
    }//GEN-LAST:event_formWindowClosing

    private void treeExamsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeExamsMouseClicked
        if (evt.getClickCount() > 1) {
            showExam();
        }
    }//GEN-LAST:event_treeExamsMouseClicked

    private void treeExamsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeExamsKeyTyped
        if (evt.getKeyChar() == '\n') {
            showExam();
        }
    }//GEN-LAST:event_treeExamsKeyTyped

    private void btnAddAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAttachedActionPerformed
        addNewAttahed();
    }//GEN-LAST:event_btnAddAttachedActionPerformed

    private void btnRemoveAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAttachedActionPerformed
        removeAttached();
    }//GEN-LAST:event_btnRemoveAttachedActionPerformed

    private void btnEditAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditAttachedActionPerformed
        editAttached();
    }//GEN-LAST:event_btnEditAttachedActionPerformed

    private void btnDownloadAttachedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadAttachedActionPerformed
        try {
            downloadAttached();
        } catch (IOException ex) {
            throw new RuntimeException("No blob. ", ex);
        }
    }//GEN-LAST:event_btnDownloadAttachedActionPerformed

    @Action
    public void showVisit() {
    }

    @Action
    public void nextVisit() {
        final Visit v = getVisitCloseOf(visit, true);
        if (v != null) {
            showData(v);
            selectByVisit(v);
        }
    }

    @Action
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

    @Action
    public void createNewExam() {
        QLog.l().logger().trace("Создадим новый документ.");
        final IDocController blanc;
        if (DocControllersList.getInstance().getSize() == 1) {
            JOptionPane.showMessageDialog(this,
                    "Сейчас у вас доступен только один вид документа.\nРасширте список доступных видов документов добавлением плагинов.",
                    "Выбор документа",
                    JOptionPane.INFORMATION_MESSAGE);
            blanc = DocControllersList.getInstance().getElementAt(0);
        } else {
            blanc = FDocChooser.getDocument("Выберите документ для создания", this, true);
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

    @Action
    public void removeExam() {
        QLog.l().logger().trace("Удалим документ.");
        final TreeNode node = (TreeNode) treeExams.getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && JOptionPane.showConfirmDialog(this,
                "Документ \"" + DocControllersList.getInstance().getById(((DocNode) node).getDoc().getDocId()).getName() + " " + node.toString() + "\" будет удален безвозвратно?",
                "Удаление документа",
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

    @Action
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

    @Action
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
                    "Ошибка сохранения визита.",
                    "Сохранение визита",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Данные о визите были успешно сохранены",
                "Сохранение визита",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void addNewAttahed() {

        QLog.l().logger().trace("Добавим новое вложение.");
        // если сегодня уже создали визит то его не нужно еще раз создавать, просто покажем его

        if (JOptionPane.showConfirmDialog(this,
                "Будет добавлено новое вложение для посещения.",
                "Новое вложение",
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
                        "Ошибка создания пиложения.",
                        "Сохранение приложения",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ((AttachedTableModel) attachedTable.getModel()).update(visit.getAttached());
            ((AttachedTableModel) attachedTable.getModel()).fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Не верные параметры вложения.",
                    "Новое вложение",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeAttached() {
        if (!attachedTable.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Удалим вложение.");
            int row = attachedTable.convertRowIndexToModel(attachedTable.getSelectedRow());
            AttachedTableModel model = (AttachedTableModel) attachedTable.getModel();

            final Attached attached = model.getRowAt(row);
            if (attached == null || JOptionPane.showConfirmDialog(this,
                    "Вложение \"" + attached.getTitle() + "\" будет удалено безвозвратно?",
                    "Удаление вложения",
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
                            "Ошибка создания пиложения.",
                            "Сохранение приложения",
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
            fc.setDialogTitle("Сохранение вложения");
            fc.setCurrentDirectory(new File("."));
            fc.setSelectedFile(new File(attached.getFileName()));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (fc.getSelectedFile().exists() && JOptionPane.showConfirmDialog(this,
                        "Файл \"" + fc.getSelectedFile().getName() + "\" существует. Заменить новым?",
                        "Сохранение вложения",
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelAgeOnDate;
    private javax.swing.JLabel labelPatientInfo;
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
