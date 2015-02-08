package ru.apertum.journal.forms;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import ru.apertum.journal.Journal;
import ru.apertum.journal.model.Attached;
import ru.apertum.journal.model.Document;
import ru.apertum.journal.model.Patient;
import ru.apertum.journal.model.PatientBlank;
import ru.apertum.journal.model.Visit;
import ru.apertum.journal.model.patients.PatientsTableModel;
import ru.apertum.journal.model.patients.VisitsTableModel;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.model.QTray;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.model.QCustomer;

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
/**
 *
 * @author Evgeniy Egorov
 */
public class FJournal extends javax.swing.JFrame {
    
    private static final ResourceBundle translate = ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal", Locales.getInstance().getLangCurrent());

    private String locMes(String key) {
        return translate.getString(key);
    }

    public FJournal() {
        initComponents();

        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
            this.setVisible(false);
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        try {
            this.setIconImage(ImageIO.read(FJournal.class.getResource("/ru/apertum/journal/forms/resources/favicon.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }

        tablePatients.setModel(new PatientsTableModel());
        tableVisits.setModel(new VisitsTableModel());

        final RowFilter<Object, Object> filter = new JoFilter();
        final TableRowSorter<TableModel> sorter = new TableRowSorter<>(tablePatients.getModel());
        sorter.setRowFilter(filter);
        tablePatients.setRowSorter(sorter);

        tablePatients.getColumnModel().getColumn(0).setPreferredWidth(10);
        tablePatients.getColumnModel().getColumn(0).setWidth(10);
        tablePatients.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (tablePatients.getRowSorter().getViewRowCount() != 0 && e.getFirstIndex() >= 0 && e.getLastIndex() >= 0 && tablePatients.getSelectedRow() >= 0) {
                if (tablePatients.getRowSorter() != null && tablePatients.getSelectedRow() != -1) {
                    int i = tablePatients.getRowSorter().convertRowIndexToModel(tablePatients.getSelectedRow());

                    final Long id = (Long) tablePatients.getModel().getValueAt(i, 0);
                    final Patient patient = ((PatientsTableModel) tablePatients.getModel()).getPatientById(id);
                    showPatientInfo(patient);

                } else {
                    showPatientInfo(null);
                }
            }
            if (tablePatients.getRowSorter().getViewRowCount() == 0) {
                showPatientInfo(null);
            }
        });
        if (tablePatients.getRowSorter().getViewRowCount() != 0) {
            tablePatients.getSelectionModel().addSelectionInterval(0, 0);
        }

        //***************************************************************************************
        //*** Visits
        tableVisits.getColumnModel().getColumn(0).setPreferredWidth(10);
        if (tableVisits.getModel().getRowCount() != 0) {
            tableVisits.getSelectionModel().addSelectionInterval(0, 0);
        }

        tablePatients.getColumnModel().getColumn(0).setMaxWidth(50);
        tablePatients.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePatients.getColumnModel().getColumn(0).setMaxWidth(1000);

        tableVisits.getColumnModel().getColumn(0).setMaxWidth(50);
        tableVisits.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableVisits.getColumnModel().getColumn(0).setMaxWidth(1000);

        label1.setVisible(PatientBlank.getInstance().caption1() != null && !PatientBlank.getInstance().caption1().isEmpty());
        textFieldFilterSecondName.setVisible(PatientBlank.getInstance().caption1() != null && !PatientBlank.getInstance().caption1().isEmpty());
        label1.setText(PatientBlank.getInstance().caption1());

        label3.setVisible(PatientBlank.getInstance().caption3() != null && !PatientBlank.getInstance().caption3().isEmpty());
        dateFilterBirthday.setVisible(PatientBlank.getInstance().caption3() != null && !PatientBlank.getInstance().caption3().isEmpty());
        label3.setText(PatientBlank.getInstance().caption3());
        
        
        int ii = 1;
        final ButtonGroup bg = new ButtonGroup();
        final String currLng = Locales.getInstance().getLangCurrName();
        for (String lng : Locales.getInstance().getAvailableLocales()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setCurrentLang();
                }
            });
            bg.add(item);
            item.setSelected(lng.equals(currLng));
            item.setText(lng); // NOI18N
            item.setName("QRadioButtonMenuItem" + (ii++)); // NOI18N
            menuLangs.add(item);
        }
    }
    
    public void setCurrentLang() {
        for (int i = 0; i < menuLangs.getItemCount(); i++) {
            if (((JRadioButtonMenuItem) menuLangs.getItem(i)).isSelected()) {
                Locales.getInstance().setLangCurrent(((JRadioButtonMenuItem) menuLangs.getItem(i)).getText());
            }
        }
    }

    private class JoFilter extends RowFilter<Object, Object> {

        @Override
        public boolean include(RowFilter.Entry entry) {
            if (!textFieldFilterSecondName.getText().isEmpty()) {
                return ((String) entry.getValue(1)).toUpperCase().startsWith(" " + textFieldFilterSecondName.getText().toUpperCase());
            }
            if ((Long) spinnerFilterKarta.getModel().getValue() != 0) {
                return (((Long) entry.getValue(0)).equals((Long) spinnerFilterKarta.getModel().getValue()));
            }
            if (dateFilterBirthday.getDate() != null && !isNow(dateFilterBirthday.getDate())) {
                return isSameDates((Date) entry.getValue(3), dateFilterBirthday.getDate());
            }
            if (dateFilterLastVisit.getDate() != null && !isNow(dateFilterLastVisit.getDate())) {
                return isSameDates((Date) entry.getValue(4), dateFilterLastVisit.getDate());
            }
            if (!textFieldFilterAttachedNAME.getText().isEmpty()) {
                final Patient p = ((PatientsTableModel) tablePatients.getModel()).getPatientById(((Long) entry.getValue(0)));
                boolean f = true;
                final String st = textFieldFilterAttachedNAME.getText().toLowerCase();
                if (p != null) {
                    for (Attached at : p.getAttached()) {
                        if (at.getTitle().toLowerCase().contains(st)) {
                            f = false;
                            break;
                        }
                    }
                    if (f) {
                        for (Visit visit : p.getVisits()) {
                            for (Attached at : visit.getAttached()) {
                                if (at.getTitle().toLowerCase().contains(st)) {
                                    f = false;
                                    break;
                                }
                            }
                            if (f) {
                                for (Document doc : visit.getDocs()) {
                                    for (Attached at : doc.getAttached()) {
                                        if (at.getTitle().toLowerCase().contains(st)) {
                                            f = false;
                                            break;
                                        }
                                    }
                                    if (!f) {
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
                if (f) {
                    return false;
                }
            }
            return true;
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
            if (date1 == null || date2 == null) {
                return false;
            }
            final GregorianCalendar gc1 = new GregorianCalendar();
            gc1.setTime(date1);
            final GregorianCalendar gc2 = new GregorianCalendar();
            gc2.setTime(date2);
            return (gc1.get(GregorianCalendar.DAY_OF_YEAR) == gc2.get(GregorianCalendar.DAY_OF_YEAR)
                    && gc1.get(GregorianCalendar.YEAR) == gc2.get(GregorianCalendar.YEAR));
        }

    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenuPatients = new javax.swing.JPopupMenu();
        jMenuItemNewPoc = new javax.swing.JMenuItem();
        jMenuItemEditPoc = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemRemovePoc = new javax.swing.JMenuItem();
        popupMenuVisits = new javax.swing.JPopupMenu();
        jMenuItemShowVisit = new javax.swing.JMenuItem();
        jMenuItemNewVisit = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        splitPanePatientsBase = new javax.swing.JSplitPane();
        panelPatientListArea = new javax.swing.JPanel();
        panelFilter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textFieldFilterSecondName = new javax.swing.JTextField();
        label1 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        buttonApplyFilter = new javax.swing.JButton();
        buttonClearFilter = new javax.swing.JButton();
        spinnerFilterKarta = new javax.swing.JSpinner();
        dateFilterLastVisit = new com.toedter.calendar.JDateChooser();
        dateFilterBirthday = new com.toedter.calendar.JDateChooser();
        textFieldFilterAttachedNAME = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        panelPatientsButtons = new javax.swing.JPanel();
        buttonEditPatient = new javax.swing.JButton();
        buttonAddPatient = new javax.swing.JButton();
        buttonRemoveParient = new javax.swing.JButton();
        buttonRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePatients = new javax.swing.JTable();
        panelPatientArea = new javax.swing.JPanel();
        panelPatientInfoButtons = new javax.swing.JPanel();
        buttonShowVisit = new javax.swing.JButton();
        buttonAddNewVisit = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableVisits = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        labelPatientInfo = new javax.swing.JLabel();
        labelVisitsCaption = new javax.swing.JLabel();
        menuBarJournal = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        menuLangs = new javax.swing.JMenu();
        miExit = new javax.swing.JMenuItem();

        jMenuItemNewPoc.setText("Новый пациент");
        jMenuItemNewPoc.setToolTipText("");
        jMenuItemNewPoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewPocActionPerformed(evt);
            }
        });
        popupMenuPatients.add(jMenuItemNewPoc);

        jMenuItemEditPoc.setText("Редактировать");
        jMenuItemEditPoc.setToolTipText("");
        jMenuItemEditPoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditPocActionPerformed(evt);
            }
        });
        popupMenuPatients.add(jMenuItemEditPoc);
        popupMenuPatients.add(jSeparator1);

        jMenuItemRemovePoc.setText("Удалить пациента");
        jMenuItemRemovePoc.setToolTipText("");
        jMenuItemRemovePoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemovePocActionPerformed(evt);
            }
        });
        popupMenuPatients.add(jMenuItemRemovePoc);

        jMenuItemShowVisit.setText("Посмотреть обращение");
        jMenuItemShowVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemShowVisitActionPerformed(evt);
            }
        });
        popupMenuVisits.add(jMenuItemShowVisit);

        jMenuItemNewVisit.setText("Новое посещение");
        jMenuItemNewVisit.setToolTipText("");
        jMenuItemNewVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewVisitActionPerformed(evt);
            }
        });
        popupMenuVisits.add(jMenuItemNewVisit);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Journal");

        splitPanePatientsBase.setDividerLocation(700);
        splitPanePatientsBase.setDividerSize(7);
        splitPanePatientsBase.setContinuousLayout(true);

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Фильтр списка посетителей"));

        jLabel1.setText("№ карточки");

        textFieldFilterSecondName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldFilterSecondNameKeyReleased(evt);
            }
        });

        label1.setText("Фамилия");

        label3.setText("Дата рождения");

        jLabel4.setText("Дата последнего обращения");

        buttonApplyFilter.setText("Искать");
        buttonApplyFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonApplyFilterActionPerformed(evt);
            }
        });

        buttonClearFilter.setText("Отчистить");
        buttonClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearFilterActionPerformed(evt);
            }
        });

        spinnerFilterKarta.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), null, Long.valueOf(1L)));

        dateFilterLastVisit.setDate(null);

        dateFilterBirthday.setDate(null);

        jLabel2.setText("Наименование вложения");

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label3)
                            .addComponent(jLabel1)
                            .addComponent(label1))
                        .addGap(10, 10, 10)
                        .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFilterLayout.createSequentialGroup()
                                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFilterLayout.createSequentialGroup()
                                        .addComponent(spinnerFilterKarta, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel4))
                                    .addComponent(dateFilterBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateFilterLastVisit, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(panelFilterLayout.createSequentialGroup()
                                .addComponent(textFieldFilterSecondName, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldFilterAttachedNAME))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonClearFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonApplyFilter)))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(spinnerFilterKarta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(dateFilterLastVisit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1)
                    .addComponent(textFieldFilterSecondName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldFilterAttachedNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label3)
                    .addComponent(dateFilterBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonApplyFilter)
                    .addComponent(buttonClearFilter))
                .addContainerGap())
        );

        panelPatientsButtons.setBorder(new javax.swing.border.MatteBorder(null));

        buttonEditPatient.setText("Редактировать");
        buttonEditPatient.setToolTipText("");
        buttonEditPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditPatientActionPerformed(evt);
            }
        });

        buttonAddPatient.setText("Новый посетитель");
        buttonAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddPatientActionPerformed(evt);
            }
        });

        buttonRemoveParient.setText("Удалить посетителя");
        buttonRemoveParient.setToolTipText("Удалить посетителя на всегда");
        buttonRemoveParient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveParientActionPerformed(evt);
            }
        });

        buttonRefresh.setText("Обновить список посетителей");
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPatientsButtonsLayout = new javax.swing.GroupLayout(panelPatientsButtons);
        panelPatientsButtons.setLayout(panelPatientsButtonsLayout);
        panelPatientsButtonsLayout.setHorizontalGroup(
            panelPatientsButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPatientsButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                .addComponent(buttonRemoveParient)
                .addGap(18, 18, 18)
                .addComponent(buttonAddPatient)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonEditPatient)
                .addContainerGap())
        );
        panelPatientsButtonsLayout.setVerticalGroup(
            panelPatientsButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPatientsButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPatientsButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEditPatient)
                    .addComponent(buttonAddPatient)
                    .addComponent(buttonRemoveParient)
                    .addComponent(buttonRefresh))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablePatients.setAutoCreateRowSorter(true);
        tablePatients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1323", "Иванов", "Петр Степанович", "13.10.1913", "13.12.2010"},
                {"3245", "Петров", "Сидор Михалыч", "12.10.1956", "31.12.2005"},
                {"144", "Сидоров", "Андрей Терентич", "25.01.2000", "21.05.2011"},
                {"634", "Путин", "Вэ.Вэ.", "16.09.1956", "04.12.2011"}
            },
            new String [] {
                "№", "Фамилия", "Имя, отчество", "Дата рождения", "Последний визит"
            }
        ));
        tablePatients.setComponentPopupMenu(popupMenuPatients);
        tablePatients.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablePatients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablePatientsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablePatients);

        javax.swing.GroupLayout panelPatientListAreaLayout = new javax.swing.GroupLayout(panelPatientListArea);
        panelPatientListArea.setLayout(panelPatientListAreaLayout);
        panelPatientListAreaLayout.setHorizontalGroup(
            panelPatientListAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPatientsButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        panelPatientListAreaLayout.setVerticalGroup(
            panelPatientListAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPatientListAreaLayout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPatientsButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        splitPanePatientsBase.setLeftComponent(panelPatientListArea);

        panelPatientInfoButtons.setBorder(new javax.swing.border.MatteBorder(null));

        buttonShowVisit.setText("Посмотреть визит");
        buttonShowVisit.setToolTipText("");
        buttonShowVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowVisitActionPerformed(evt);
            }
        });

        buttonAddNewVisit.setText("Новый визит");
        buttonAddNewVisit.setToolTipText("");
        buttonAddNewVisit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddNewVisitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPatientInfoButtonsLayout = new javax.swing.GroupLayout(panelPatientInfoButtons);
        panelPatientInfoButtons.setLayout(panelPatientInfoButtonsLayout);
        panelPatientInfoButtonsLayout.setHorizontalGroup(
            panelPatientInfoButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPatientInfoButtonsLayout.createSequentialGroup()
                .addContainerGap(161, Short.MAX_VALUE)
                .addComponent(buttonAddNewVisit)
                .addGap(18, 18, 18)
                .addComponent(buttonShowVisit)
                .addContainerGap())
        );
        panelPatientInfoButtonsLayout.setVerticalGroup(
            panelPatientInfoButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPatientInfoButtonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelPatientInfoButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonShowVisit)
                    .addComponent(buttonAddNewVisit))
                .addContainerGap())
        );

        tableVisits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "20.05.2004", "Общее направление"},
                {"2", "14.06.2004", "Повторное обследование"},
                {"3", "13.12.2010", "Профилактика"},
                {null, null, null}
            },
            new String [] {
                "№", "Дата", "Описание"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableVisits.setComponentPopupMenu(popupMenuVisits);
        tableVisits.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableVisits.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableVisitsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableVisits);

        jScrollPane3.setBorder(null);

        labelPatientInfo.setText("<html> <span style='font-size:20.0pt;color:red'>Нет данных"); // NOI18N
        labelPatientInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelPatientInfo.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane3.setViewportView(labelPatientInfo);

        labelVisitsCaption.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelVisitsCaption.setText("Визиты");

        javax.swing.GroupLayout panelPatientAreaLayout = new javax.swing.GroupLayout(panelPatientArea);
        panelPatientArea.setLayout(panelPatientAreaLayout);
        panelPatientAreaLayout.setHorizontalGroup(
            panelPatientAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPatientInfoButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
            .addGroup(panelPatientAreaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPatientAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                    .addGroup(panelPatientAreaLayout.createSequentialGroup()
                        .addComponent(labelVisitsCaption)
                        .addGap(0, 348, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelPatientAreaLayout.setVerticalGroup(
            panelPatientAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPatientAreaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelVisitsCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPatientInfoButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        splitPanePatientsBase.setRightComponent(panelPatientArea);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPanePatientsBase)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPanePatientsBase)
        );

        jMenuFile.setText("File");

        menuLangs.setText("Languages");
        menuLangs.setName("menuLangs"); // NOI18N
        jMenuFile.add(menuLangs);

        miExit.setText("Выход");
        miExit.setToolTipText("");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        jMenuFile.add(miExit);

        menuBarJournal.add(jMenuFile);

        setJMenuBar(menuBarJournal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1121, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 607, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldFilterSecondNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFilterSecondNameKeyReleased
        applyFilter();
    }//GEN-LAST:event_textFieldFilterSecondNameKeyReleased

    private void tablePatientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablePatientsMouseClicked
        if (tablePatients.getRowSorter() != null && tablePatients.getSelectedRow() != -1) {
            int i = tablePatients.getRowSorter().convertRowIndexToModel(tablePatients.getSelectedRow());
            final Long id = (Long) tablePatients.getModel().getValueAt(i, 0);
            final Patient patient = ((PatientsTableModel) tablePatients.getModel()).getPatientById(id);
            showPatientInfo(patient);
        } else {
            showPatientInfo(null);
        }

        //final Long id = (Long) tablePatients.getModel().getValueAt(tablePatients.getSelectedRow(), 0);
        //Patient patient = ((PatientsTableModel) tablePatients.getModel()).getPatientById(id);
        //       showPatientInfo(patient);
        if (evt.getClickCount() > 1) {
            editPatient();
        }
    }//GEN-LAST:event_tablePatientsMouseClicked

    private void tableVisitsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableVisitsMouseClicked
        if (evt.getClickCount() > 1) {
            showVisit();
        }
    }//GEN-LAST:event_tableVisitsMouseClicked

    private void buttonClearFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearFilterActionPerformed
        clearFilter();
    }//GEN-LAST:event_buttonClearFilterActionPerformed

    private void buttonApplyFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonApplyFilterActionPerformed
        applyFilter();
    }//GEN-LAST:event_buttonApplyFilterActionPerformed

    private void buttonRemoveParientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveParientActionPerformed
        removePotient();
    }//GEN-LAST:event_buttonRemoveParientActionPerformed

    private void buttonAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddPatientActionPerformed
        addPatient();
    }//GEN-LAST:event_buttonAddPatientActionPerformed

    private void buttonEditPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditPatientActionPerformed
        editPatient();
    }//GEN-LAST:event_buttonEditPatientActionPerformed

    private void buttonAddNewVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddNewVisitActionPerformed
        addNewVisit();
    }//GEN-LAST:event_buttonAddNewVisitActionPerformed

    private void buttonShowVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowVisitActionPerformed
        showVisit();
    }//GEN-LAST:event_buttonShowVisitActionPerformed

    private void jMenuItemNewPocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewPocActionPerformed
        addPatient();
    }//GEN-LAST:event_jMenuItemNewPocActionPerformed

    private void jMenuItemEditPocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditPocActionPerformed
        editPatient();
    }//GEN-LAST:event_jMenuItemEditPocActionPerformed

    private void jMenuItemRemovePocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemovePocActionPerformed
        removePotient();
    }//GEN-LAST:event_jMenuItemRemovePocActionPerformed

    private void jMenuItemShowVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemShowVisitActionPerformed
        showVisit();
    }//GEN-LAST:event_jMenuItemShowVisitActionPerformed

    private void jMenuItemNewVisitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewVisitActionPerformed
        addNewVisit();
    }//GEN-LAST:event_jMenuItemNewVisitActionPerformed

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        setVisible(false);
        System.exit(0);
    }//GEN-LAST:event_miExitActionPerformed

    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshActionPerformed
        tablePatients.setModel(new PatientsTableModel());
        tableVisits.setModel(new VisitsTableModel());

        final TableRowSorter<TableModel> sorter = new TableRowSorter<>(tablePatients.getModel());
        sorter.setRowFilter(new JoFilter());
        tablePatients.setRowSorter(sorter);

        tablePatients.getColumnModel().getColumn(0).setMaxWidth(50);
        tablePatients.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePatients.getColumnModel().getColumn(0).setMaxWidth(1000);

        tableVisits.getColumnModel().getColumn(0).setMaxWidth(50);
        tableVisits.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableVisits.getColumnModel().getColumn(0).setMaxWidth(1000);

        ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();
        if (tablePatients.getModel().getRowCount() != 0) {
            selectTablePatientsItem(((PatientsTableModel) tablePatients.getModel()).getPatientById(((Long) tablePatients.getModel().getValueAt(0, 0))));
        }
    }//GEN-LAST:event_buttonRefreshActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FJournal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        QLog.initial(args, 1);
        //Locale.setDefault(Locales.getInstance().getLangCurrent());
        // Загрузка плагинов из папки plugins
        if (QLog.l().isPlaginable()) {
            Uses.loadPlugins("./plugins/");
        }
        /*
         // Прикрутим сервер на сокет.
         final ServerSocket socket;
         try {
         socket = new ServerSocket(50015);
         } catch (IOException ex) {
         try {
         try (Socket ups = new Socket("localhost", 50015)) {
         ups.getOutputStream().close();
         }
         } catch (UnknownHostException ex1) {
         System.out.println("1 " + ex1);
         } catch (IOException ex1) {
         System.out.println("2 " + ex1);
         }
         throw new ServerException("Старт вторй копии приложения Journal.");
         }

         try {
         socket.close();
         } catch (IOException ex) {
         }
         final Thread thresd = new Thread(() -> {
         final ServerSocket socket1;
         try {
         socket1 = new ServerSocket(50015);
         socket1.setSoTimeout(5000);
         } catch (IOException ex) {
         throw new ServerException(ex.toString());
         }
         while (!Thread.interrupted()) {
         try {
         socket1.accept();
         QLog.l().logger().info("Откроем главную форму вместо повторного запуска.");
         fj.setAlwaysOnTop(true);
         fj.setVisible(true);
         fj.setState(JFrame.NORMAL);
         fj.setAlwaysOnTop(false);
         } catch (SocketTimeoutException ex) {
         } catch (IOException ex) {
         QLog.l().logger().error("Что-то с сеткой.", ex);
         }
         }
         });

         thresd.setDaemon(true);
         thresd.start();
         */
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    //System.out.println(info.getName());
                    /*Metal Nimbus CDE/Motif Windows   Windows Classic  //GTK+*/
                    if ("Windows".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
                if ("/".equals(File.separator)) {
                    final FontUIResource f = new FontUIResource(new Font("Serif", Font.PLAIN, 10));
                    final Enumeration<Object> keys = UIManager.getDefaults().keys();
                    while (keys.hasMoreElements()) {
                        final Object key = keys.nextElement();
                        final Object value = UIManager.get(key);
                        if (value instanceof FontUIResource) {
                            final FontUIResource orig = (FontUIResource) value;
                            final Font font1 = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                            UIManager.put(key, new FontUIResource(font1));
                        }
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }

            fj = new FJournal();

            fj.setLocationRelativeTo(null);
            // инициализим trayIcon, т.к. setSituation() требует работу с tray
            QTray tray = QTray.getInstance(fj, "/ru/apertum/journal/forms/resources/favicon.png", "Journal");
            tray.addItem("Journal", (ActionEvent e) -> {
                fj.setVisible(true);
                fj.setState(JFrame.NORMAL);
            });
            tray.addItem("-", (ActionEvent e) -> {
            });
            tray.addItem("Завершить работу", (ActionEvent e) -> {
                fj.dispose();
                System.exit(0);
            });

            fj.setVisible(true);
        });
    }

    static FJournal fj;

    public void setTrueFocus() {
        tablePatients.requestFocusInWindow();
        tablePatients.requestFocus();
    }

    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = Journal.getApplication().getMainFrame();
            aboutBox = new JournalAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        Journal.getApplication().show(aboutBox);
    }

    public void addPatient() {
        QLog.l().logger().trace("Добавим посетителя.");
        Patient patient = new Patient();
        PatientBlank.getInstance().loadPatientData(patient);
        patient = FPatientMaster.editPatient(patient, PatientBlank.getInstance());
        if (patient != null) {
            ((PatientsTableModel) tablePatients.getModel()).add(patient);

            ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();
            selectTablePatientsItem(patient);
        }
    }

    public void editPatient() {
        if (!tablePatients.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Отредактируем посетителя.");
            int i = tablePatients.getRowSorter().convertRowIndexToModel(tablePatients.getSelectedRow());
            final Long id = (Long) tablePatients.getModel().getValueAt(i, 0);
            Patient patient = ((PatientsTableModel) tablePatients.getModel()).getPatientById(id);
            final Patient selP = patient;
            PatientBlank.getInstance().loadPatientData(patient);
            patient = FPatientMaster.editPatient(patient, PatientBlank.getInstance());
            if (patient != null) {
                ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();
                selectTablePatientsItem(selP);
            }
        }
    }

    public void removePotient() {
        if (!tablePatients.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Удалим посетителя.");
            final Long id = (Long) tablePatients.getModel().getValueAt(tablePatients.getSelectedRow(), 0);
            final Patient patient = ((PatientsTableModel) tablePatients.getModel()).getPatientById(id);
            if (patient == null || JOptionPane.showConfirmDialog(this,
                    "Пациент \"" + patient.getName() + "\" будет удален безвозвратно?",
                    "Удаление пациента",
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем пользователя \"" + patient.getName() + "\"");
            int i = tablePatients.getSelectedRow();
            // удалим навсегда
            ((PatientsTableModel) tablePatients.getModel()).removePatient(patient);
            // в фокус запись рядом
            if (tablePatients.getRowCount() != 0) {
                if (tablePatients.getRowCount() - 1 < i) {
                    i--;
                }
                tablePatients.getSelectionModel().setSelectionInterval(i, i);
            }
        }
    }

    /**
     * Выделить в таблице строку с пациентом
     *
     * @param patient
     */
    public void selectTablePatientsItem(Patient patient) {
        for (int i = 0; i < tablePatients.getModel().getRowCount(); i++) {
            if (((Long) tablePatients.getModel().getValueAt(i, 0)).equals(patient.getId())) {
                tablePatients.getSelectionModel().addSelectionInterval(i, i);
                showPatientInfo(patient);
                break;
            }
        }
    }

    /**
     * выведем инфу о пациенте в область информации о пациенте
     *
     * @param patient
     */
    public final void showPatientInfo(Patient patient) {
        QLog.l().logger().trace("Выведем информацию по посетителю.");
        if (patient == null) {
            labelPatientInfo.setText("");
            ((VisitsTableModel) tableVisits.getModel()).update(null, null);
        } else {
            labelPatientInfo.setText("<html>" + patient.getTextInfo(PatientBlank.getInstance()));
            ((VisitsTableModel) tableVisits.getModel()).update(patient, patient.getVisits());
        }
        ((VisitsTableModel) tableVisits.getModel()).fireTableDataChanged();
        if (tablePatients.getRowSorter().getViewRowCount() != 0) {
            tableVisits.getSelectionModel().addSelectionInterval(0, 0);
        }
    }

    public void addNewVisit() {
        final Patient patient = ((VisitsTableModel) tableVisits.getModel()).getPatient();
        if (patient != null) {
            QLog.l().logger().trace("Добавим новый визит.");
            // если сегодня уже создали визит то его не нужно еще раз создавать, просто покажем его
            Visit visit = ((VisitsTableModel) tableVisits.getModel()).getVisitByDate(new Date());
            if (visit == null) {
                if (JOptionPane.showConfirmDialog(this,
                        "Будет создано новое посещение для пациента \"" + patient.getName() + "\"?",
                        "Новое посещение",
                        JOptionPane.YES_NO_OPTION) == 1) {
                    return;
                }
                patient.getVisits().add(visit = new Visit(patient));
                patient.setLastVisit(new Date());
                try {
                    Patient.savePatient(patient);
                } catch (ClientException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка создания посещения.",
                            "Сохранение посещения",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ((VisitsTableModel) tableVisits.getModel()).update(patient, patient.getVisits());
                ((VisitsTableModel) tableVisits.getModel()).fireTableDataChanged();
            }
            final FVisitEditor ve = new FVisitEditor(visit, tableVisits.getModel());
            ve.setLocationRelativeTo(null);
            ve.setVisible(true);
        }
    }

    public void showVisit() {
        if (!tableVisits.getSelectionModel().isSelectionEmpty()) {
            QLog.l().logger().trace("Смотрим визит.");
            final Date date = (Date) tableVisits.getModel().getValueAt(tableVisits.getSelectedRow(), 1);
            final Visit visit = ((VisitsTableModel) tableVisits.getModel()).getVisitByDate(date);
            final FVisitEditor ve = new FVisitEditor(visit, tableVisits.getModel());
            ve.setLocationRelativeTo(null);
            ve.setVisible(true);
        }
    }

    public void clearFilter() {
        QLog.l().logger().trace("Почистим фильтр.");
        /*
         FExamination fe= FExamination.getInstance();
         fe.setVisible(true);
         if (true) {
         return;
         }
         */
        dateFilterBirthday.setDate(null);
        dateFilterLastVisit.setDate(null);
        spinnerFilterKarta.getModel().setValue((long) 0);
        textFieldFilterSecondName.setText(null);
        textFieldFilterAttachedNAME.setText("");
        /*
         ((PatientsTableModel) tablePatients.getModel()).filter(null);
         * 
         */
        ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();

        if (tablePatients.getRowSorter().getViewRowCount() != 0) {
            tablePatients.getSelectionModel().addSelectionInterval(0, 0);
            final Long id = (Long) tablePatients.getModel().getValueAt(0, 0);
            final Patient patient = ((PatientsTableModel) tablePatients.getModel()).getPatientById(id);
            showPatientInfo(patient);
        } else {
            showPatientInfo(null);
        }
    }

    public void applyFilter() {
        QLog.l().logger().trace("Отфильтруем.");
        ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();
        /*
         final HashMap<String, Object> filter = new HashMap<>();
         if (!textFieldFilterSecondName.getText().isEmpty()) {
         filter.put("secondName", textFieldFilterSecondName.getText());
         }
         if ((Integer) spinnerFilterKarta.getModel().getValue() != 0) {
         filter.put("id", new Long((Integer) spinnerFilterKarta.getModel().getValue()));
         }
         if (!Uses.isNow((Date) spinnerFilterBirthday.getModel().getValue())) {
         filter.put("birthday", spinnerFilterBirthday.getModel().getValue());
         }
         if (!Uses.isNow((Date) spinnerFilterLastVisit.getModel().getValue())) {
         filter.put("lastVisit", spinnerFilterLastVisit.getModel().getValue());
         }
         ((PatientsTableModel) tablePatients.getModel()).filter(filter);
         ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();
         * 
         */
        if (tablePatients.getRowSorter().getViewRowCount() != 0) {
            tablePatients.getSelectionModel().addSelectionInterval(0, 0);
        }
    }

    public void applyFilter(QCustomer customer) {
        QLog.l().logger().trace("Отфильтруем2.");
        textFieldFilterSecondName.setText(customer.getInput_data());
        ((PatientsTableModel) tablePatients.getModel()).fireTableDataChanged();

        if (tablePatients.getRowSorter().getViewRowCount() != 0) {
            tablePatients.getSelectionModel().addSelectionInterval(0, 0);
        }
    }

    public void showConfig() {
        //FConfigDev.getInstance().load();
        //FConfigDev.getInstance().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddNewVisit;
    private javax.swing.JButton buttonAddPatient;
    private javax.swing.JButton buttonApplyFilter;
    private javax.swing.JButton buttonClearFilter;
    private javax.swing.JButton buttonEditPatient;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JButton buttonRemoveParient;
    private javax.swing.JButton buttonShowVisit;
    private com.toedter.calendar.JDateChooser dateFilterBirthday;
    private com.toedter.calendar.JDateChooser dateFilterLastVisit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemEditPoc;
    private javax.swing.JMenuItem jMenuItemNewPoc;
    private javax.swing.JMenuItem jMenuItemNewVisit;
    private javax.swing.JMenuItem jMenuItemRemovePoc;
    private javax.swing.JMenuItem jMenuItemShowVisit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel labelPatientInfo;
    private javax.swing.JLabel labelVisitsCaption;
    public javax.swing.JPanel mainPanel;
    public javax.swing.JMenuBar menuBarJournal;
    private javax.swing.JMenu menuLangs;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelPatientArea;
    private javax.swing.JPanel panelPatientInfoButtons;
    private javax.swing.JPanel panelPatientListArea;
    private javax.swing.JPanel panelPatientsButtons;
    private javax.swing.JPopupMenu popupMenuPatients;
    private javax.swing.JPopupMenu popupMenuVisits;
    private javax.swing.JSpinner spinnerFilterKarta;
    private javax.swing.JSplitPane splitPanePatientsBase;
    private javax.swing.JTable tablePatients;
    private javax.swing.JTable tableVisits;
    private javax.swing.JTextField textFieldFilterAttachedNAME;
    private javax.swing.JTextField textFieldFilterSecondName;
    // End of variables declaration//GEN-END:variables
private JDialog aboutBox;
}
