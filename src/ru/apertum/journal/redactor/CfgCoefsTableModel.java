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
 * 
 */
package ru.apertum.journal.redactor;

import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Evgeniy Egorov
 */
public final class CfgCoefsTableModel extends AbstractTableModel {

    private LinkedList<Short> coefs;

    public LinkedList<Short> getCoefs() {
        return coefs;
    }

    public void setCoefs(LinkedList<Short> coefs) {
        if (coefs == null) {
            coefs = new LinkedList<>();
            for (int i = 0; i < 512; i++) {
                coefs.add((short) 0);
            }
        }
        for (int i = coefs.size(); i < 512; i++) {
            coefs.add((short) 0);
        }
        this.coefs = coefs;
    }

    public CfgCoefsTableModel(LinkedList<Short> coefs) {
        setCoefs(coefs);
    }

    @Override
    public int getRowCount() {
        return 32;
    }

    @Override
    public int getColumnCount() {
        return 32;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex % 2 == 0) {
            return columnIndex / 2 * 32 + rowIndex;
        } else {
            return coefs.get((columnIndex - 1) / 2 * 32 + rowIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return "" + (column % 2 == 0 ? (column / 2 * 32 + 32) : "");
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex % 2 == 0 ? Integer.class : Short.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex % 2 == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        coefs.set((columnIndex - 1) / 2 * 32 + rowIndex, (short) aValue);
    }
}
