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
package ru.apertum.journal.model;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Evgeniy Egorov
 */
public class DocsTableModel extends AbstractTableModel {

    @Override
    public int getRowCount() {
        return DocControllersList.getInstance().getItems().size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return DocControllersList.getInstance().getItems().get(rowIndex).getId();
            case 1:
                return DocControllersList.getInstance().getItems().get(rowIndex).getName();
            case 2:
                return DocControllersList.getInstance().getItems().get(rowIndex).getDocDescription();
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
                return "Документ";
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
                return String.class;
            case 2:
                return String.class;
            default:
                throw new AssertionError();
        }
    }

}
