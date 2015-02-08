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
package ru.apertum.journal.db;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Получение сесии работы с БД. Использует механизмы Hibernate.
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class HibernateUtil {

    private final SessionFactory sessionFactory;

    private HibernateUtil() {
        try {
            final Configuration configuration = new Configuration();
            configuration.configure("/ru/apertum/journal/cfg/hibernate.cfg.xml");
            final Properties prop = new Properties();
            final File f = new File("config/journal.properties");
            if (f.exists()) {
                 prop.load(new FileInputStream(f));
            }
            configuration.addProperties(prop);
            final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            // Log exception!
            throw new ExceptionInInitializerError(ex);
        }
        //sessionFactory = new AnnotationConfiguration().addAnnotatedClass(Book.class).buildSessionFactory();
    }

    public static HibernateUtil getInstance() {
        return HibernateUtilHolder.INSTANCE;
    }

    private static class HibernateUtilHolder {

        private static final HibernateUtil INSTANCE = new HibernateUtil();
    }

    public Session getSession() throws HibernateException {
        return sessionFactory.openSession();
    }
}
