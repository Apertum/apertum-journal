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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ServiceLoader;
import ru.apertum.journal.model.PatientBlank;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 *
 * @author Evgeniy Egorov
 */
public class About {

    public static String ver = "";
    public static String date = "";

    public static void load() {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/ru/apertum/journal/cfg/version.properties");

        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }
        ver = settings.getProperty(FAbout.VERSION);
        date = settings.getProperty(FAbout.DATE);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Uses.loadPlugins("./plugins/");

        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/ru/apertum/journal/cfg/version.properties");

        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }
        load();
        System.out.println("*** Journal ***");
        System.out.println(" version " + ver);
        System.out.println(" date " + date);
        
        System.out.println("*** Editor \"" + PatientBlank.getInstance().getDescription() + "\" ID=" + PatientBlank.getInstance().getId());

        for (final IDocController doc : ServiceLoader.load(IDocController.class)) {
            System.out.println(" - Document \"" + doc.getName() + "\" docID=" + doc.getId());
        }
    }

}
