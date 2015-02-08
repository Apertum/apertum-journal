/*
 *  Copyright (C) 2015 Apertum{Projects}. web: http://apertum.ru Е-mail:  info@apertum.ru
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
package ru.apertum.journal.redactor;

import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Evgeniy Egorov
 */
public final class SensorZoneTableModel extends AbstractTableModel {

    private LinkedList<CZone> zone;

    public LinkedList<CZone> getZone() {
        return zone;
    }

    public void setZone(LinkedList<CZone> zone1) {
        this.zone = zone1 == null ? null : (LinkedList<CZone>) zone1.clone();
        if (zone == null || zone.isEmpty()) {
            zone = new LinkedList<>();
            zone.add(new CZone());
        }
    }

    public SensorZoneTableModel(LinkedList<CZone> zone) {
        setZone(zone);
    }

    @Override
    public int getRowCount() {
        return zone.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return zone.get(rowIndex).getZone();
            case 1:
                return zone.get(rowIndex).getCoeff();
            default:
                throw new AssertionError();
        }
    }
    
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Зоны";
            case 1:
                return "Коэффициенты";
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
                return Double.class;
            default:
                throw new AssertionError();
        }
    }
   
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                zone.get(rowIndex).setZone((int)aValue);
                break;
            case 1:
                zone.get(rowIndex).setCoeff((double)aValue);
                break;
            default:
                throw new AssertionError();
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
}
