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

import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import ru.apertum.journal.model.Attached;
import ru.apertum.qsystem.client.Locales;

/**
 * Модель таблици посещения пациента.
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class AttachedTableModel extends AbstractTableModel {

    private static final ResourceBundle translate = ResourceBundle.getBundle("ru/apertum/journal/forms/resources/FJournal", Locales.getInstance().getLangCurrent());

    private static String loc(String key) {
        return translate.getString(key);
    }

    final private LinkedList<Attached> atts = new LinkedList<>();

    public void update(Set<Attached> items) {
        atts.clear();
        if (items != null) {
            atts.addAll(items);
            Collections.sort(atts, (Attached o1, Attached o2) -> {
                return o1.getTitle().compareTo(o2.getTitle());
            });
        }
    }

    @Override
    public int getRowCount() {
        return atts.size();
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
                return atts.get(rowIndex).getTitle();
            case 2:
                return atts.get(rowIndex).getComments();
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
                return loc("title");
            case 2:
                return loc("description");
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
                return String.class;
            case 2:
                return String.class;
            default:
                throw new AssertionError();
        }
    }

    public Attached getRowAt(int row) {
        return atts.get(row);
    }

    public void removeAttached(Attached att) {
        atts.remove(att);
        fireTableDataChanged();
    }

}
