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
package ru.apertum.journal;

import javax.swing.JPanel;
import ru.apertum.journal.model.Patient;
import ru.apertum.qsystem.server.model.IidGetter;

/**
 *
 * @author Evgeniy Egorov
 */
public interface IPatientController extends IidGetter {

    public String getDescription();

    public JPanel getGUI();

    public void loadPatientData(Patient patient);

    public Patient savePatientData();

    public void print();

    public void export();

    /**
     * @return caption
     */
    public String caption1();

    /**
     * @return caption
     */
    public String caption2();

    /**
     * @return caption
     */
    public String caption3();

    public int extColumnsCount();

    public Object getValueAt(int columnIndex, Patient patient);

    public String getExtColumnName(int column);

    public Class<?> getExtColumnClass(int columnIndex);
    
    public String getTextView(Patient patient);

}
