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
package ru.apertum.journal.model.exam;

import java.util.HashSet;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public abstract class AComboBoxArrayModel implements ComboBoxModel {

    abstract protected String[] getItems();
    private int pos = 0;

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem instanceof String) {
            for (int i = 0; i < getItems().length; i++) {
                if (getItems()[i].equals(anItem)) {
                    pos = i;
                    return;
                }
            }
        } else {
            throw new ServerException("Тип это String ...");
        }
    }

    public String getStrById(int index) {
        if (index >= 0 && index < getSize()) {
            return getItems()[index];
        } else {
            throw new IndexOutOfBoundsException("index=" + index + ", но  значение индекса должно быть в пределах [0, " + (getSize() - 1) + "]");
        }
    }

    public int getIdByStr(String str) {
        for (int i = 0; i < getItems().length; i++) {
            if (getItems()[i].equalsIgnoreCase(str)) {
                return i;
            }
        }
        throw new ServerException("Строка \"" + str + "\" не найдена в списке возножных значений.");
    }

    @Override
    public Object getSelectedItem() {
        return getItems()[pos];
    }

    @Override
    public int getSize() {
        return getItems().length;
    }

    @Override
    public Object getElementAt(int index) {
        return getItems()[index];
    }
    final Set<ListDataListener> li = new HashSet<>();

    @Override
    public void addListDataListener(ListDataListener l) {
        li.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        li.remove(l);
    }
}
