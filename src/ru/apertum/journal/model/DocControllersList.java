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

import java.util.LinkedList;
import java.util.ServiceLoader;
import ru.apertum.journal.IDocController;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.model.ATListModel;

/**
 *
 * @author Evgeniy Egorov
 */
public class DocControllersList extends ATListModel<IDocController> {

    private DocControllersList() {
    }

    public static DocControllersList getInstance() {
        return DocListHolder.INSTANCE;
    }

    @Override
    protected LinkedList<IDocController> load() {
        final LinkedList<IDocController> l = new LinkedList<>();
        // подключения плагинов, которые стартуют в самом начале.
        // поддержка расширяемости плагинами
        QLog.l().logger().info("Загрузка SPI расширений в качестве документов.");
        for (final IDocController doc : ServiceLoader.load(IDocController.class)) {
            QLog.l().logger().trace("   Загрузка \"" + doc.getName() + "\"");
            l.add(doc);
        }
        return l;
    }

    private static class DocListHolder {

        private static final DocControllersList INSTANCE = new DocControllersList();
    }
}
