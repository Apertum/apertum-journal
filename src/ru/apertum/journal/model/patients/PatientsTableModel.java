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
package ru.apertum.journal.model.patients;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;
import ru.apertum.journal.model.Patient;
import ru.apertum.journal.model.PatientBlank;

/**
 * Модель таблици пациентов.
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class PatientsTableModel extends AbstractTableModel {

    public PatientsTableModel() {
        patients = Patient.getPatients(null);
    }
    final private LinkedList<Patient> patients;

    public void filter(HashMap<String, Object> params) {
        patients.clear();
        patients.addAll(Patient.getPatients(params));
    }

    @Override
    public int getRowCount() {
        return patients.size();
    }

    private int rem = 0;
    private final boolean col1 = PatientBlank.getInstance().caption1() == null || PatientBlank.getInstance().caption1().isEmpty();
    private final boolean col2 = PatientBlank.getInstance().caption2() == null || PatientBlank.getInstance().caption2().isEmpty();
    private final boolean col3 = PatientBlank.getInstance().caption3() == null || PatientBlank.getInstance().caption3().isEmpty();

    @Override
    public int getColumnCount() {
        rem = (col1 ? 1 : 0)
                + (col2 ? 1 : 0)
                + (col3 ? 1 : 0);
        return 5 + PatientBlank.getInstance().extColumnsCount() - rem;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex > 4 - rem && columnIndex <= 4 - rem + PatientBlank.getInstance().extColumnsCount()) {
            return PatientBlank.getInstance().getValueAt(columnIndex - 4 + rem - 1, patients.get(rowIndex));
        } else {

            if (columnIndex > 0 && col1) {
                columnIndex++;
            }
            if (columnIndex > 1 && col2) {
                columnIndex++;
            }
            if (columnIndex > 2 && col3) {
                columnIndex++;
            }

            switch (columnIndex) {
                case 0:
                    return patients.get(rowIndex).getId();
                case 1:
                    return " " + patients.get(rowIndex).getSecondName();
                case 2:
                    return " " + patients.get(rowIndex).getFirstName();
                case 3:
                    return patients.get(rowIndex).getBirthday();
                case 4:
                    return patients.get(rowIndex).getLastVisit();
                default:
                    throw new AssertionError();
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        if (column > 4 - rem && column <= 4 - rem + PatientBlank.getInstance().extColumnsCount()) {
            return PatientBlank.getInstance().getExtColumnName(column - 4 + rem - 1);
        } else {

            if (column > 0 && col1) {
                column++;
            }
            if (column > 1 && col2) {
                column++;
            }
            if (column > 2 && col3) {
                column++;
            }

            switch (column) {
                case 0:
                    return "№";
                case 1:
                    return PatientBlank.getInstance().caption1();
                case 2:
                    return PatientBlank.getInstance().caption2();
                case 3:
                    return PatientBlank.getInstance().caption3();
                case 4:
                    return "Последний визит";
                default:
                    throw new AssertionError();
            }
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex > 4 - rem && columnIndex <= 4 - rem + PatientBlank.getInstance().extColumnsCount()) {
            return PatientBlank.getInstance().getExtColumnClass(columnIndex - 4 + rem - 1);
        } else {

            if (columnIndex > 0 && col1) {
                columnIndex++;
            }
            if (columnIndex > 1 && col2) {
                columnIndex++;
            }
            if (columnIndex > 2 && col3) {
                columnIndex++;
            }

            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return Date.class;
                case 4:
                    return Date.class;
                default:
                    throw new AssertionError();
            }
        }
    }

    public void add(Patient patient) {
        patients.add(patient);
    }

    /**
     * Поиск пациента по id
     *
     * @param id
     * @return найденный пациент, null если не нашлось
     */
    public Patient getPatientById(Long id) {
        for (Patient patient : patients) {
            if (id.equals(patient.getId())) {
                return patient;
            }
        }
        return null;
    }

    public void removePatient(Patient patient) {
        patients.remove(patient);
        Patient.removePatient(patient);
        fireTableDataChanged();
    }
}
