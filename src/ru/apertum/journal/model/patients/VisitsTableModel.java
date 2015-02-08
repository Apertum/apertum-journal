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
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import ru.apertum.journal.model.Patient;
import ru.apertum.journal.model.Visit;

/**
 * Модель таблици посещения пациента.
 * @author Evgeniy Egorov, Aperum Projects
 */
public class VisitsTableModel extends AbstractTableModel {

    /**
     * тот чьи визиты
     */
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }
    final private LinkedList<Visit> visits = new LinkedList<>();

    public void update(Patient owner, Set<Visit> items) {
        patient = owner;
        visits.clear();
        if (items != null) {
            visits.addAll(items);
        }

    }

    @Override
    public int getRowCount() {
        return visits.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return visits.get(rowIndex).getDate();
            case 2:
                return visits.get(rowIndex).getComments();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "№";
            case 1:
                return "Дата посещения";
            case 2:
                return "Описание";
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return Date.class;
            case 2:
                return String.class;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Поиск визита пациента по дате посещения
     * @param date
     * @return найденный визит, null если не нашлось
     */
    public Visit getVisitByDate(Date date) {
        for (Visit visit : visits) {
            if (isSameDates(date, visit.getDate())) {
                return visit;
            }
        }
        return null;
    }
    
    /**
     * одинаковые ли даты с точностью до дня
     * @param date1
     * @param date2
     * @return 
     */
    public static boolean isSameDates(Date date1, Date date2) {
        final GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTime(date1);
        final GregorianCalendar gc2 = new GregorianCalendar();
        gc2.setTime(date2);
        return (gc1.get(GregorianCalendar.DAY_OF_YEAR) == gc2.get(GregorianCalendar.DAY_OF_YEAR)
                && gc1.get(GregorianCalendar.YEAR) == gc2.get(GregorianCalendar.YEAR));
    }
}
