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
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Evgeniy Egorov
 */
public final class TarTableModel extends AbstractTableModel {

    private LinkedList<Integer> tars;

    public LinkedList<Integer> getTars() {
        return tars;
    }

    public void setTars(LinkedList<Integer> tars1) {
        this.tars = tars1 == null ? null : (LinkedList<Integer>) tars1.clone();
        if (tars == null) {
            tars = new LinkedList<>();
            for (int i = 0; i < 256; i++) {
                tars.add(i);
            }
        }
        for (int i = tars.size(); i < 256; i++) {
            tars.add(i);
        }
    }

    public TarTableModel(LinkedList<Integer> tars) {
        setTars(tars);
    }

    @Override
    public int getRowCount() {
        return 32;
    }

    @Override
    public int getColumnCount() {
        return 16;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex % 2 == 0) {
            return columnIndex / 2 * 32 + rowIndex;
        } else {
            return tars.get((columnIndex - 1) / 2 * 32 + rowIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return "" + (column % 2 == 0 ? (column / 2 * 32 + 32) : "");
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex % 2 == 0 ? Short.class : Integer.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex % 2 == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        tars.set((columnIndex - 1) / 2 * 32 + rowIndex, (int) aValue);
        refreshChart();
    }
    private JFreeChart chart;

    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
        refreshChart();
    }

    private void refreshChart() {
        if (chart != null) {
            chart.fireChartChanged();
        }
    }
}
