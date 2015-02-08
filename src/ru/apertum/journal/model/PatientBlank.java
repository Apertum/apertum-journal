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

import java.util.ServiceLoader;
import ru.apertum.journal.IPatientController;
import ru.apertum.journal.forms.PSimplePatient;
import ru.apertum.qsystem.common.QLog;

/**
 *
 * @author Evgeniy Egorov
 */
public class PatientBlank {

    private IPatientController pc = null;

    private PatientBlank() {
        // подключения плагинов, которые стартуют в самом начале.
        // поддержка расширяемости плагинами
        QLog.l().logger().info("Загрузка SPI расширений в качестве Редактора посетителя.");
        for (final IPatientController poc : ServiceLoader.load(IPatientController.class)) {
            QLog.l().logger().trace("   Загрузка \"" + poc.getName() + "\"");
            if (poc instanceof PSimplePatient && pc != null) {

            } else {
                pc = poc;
            }
        }
    }

    public static IPatientController getInstance() {
        return PatientBlankHolder.INSTANCE.pc;
    }

    private static class PatientBlankHolder {

        private static final PatientBlank INSTANCE = new PatientBlank();
    }
}
