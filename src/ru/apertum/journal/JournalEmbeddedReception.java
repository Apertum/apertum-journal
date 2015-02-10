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

import java.awt.GridLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.swing.JPanel;
import ru.apertum.journal.forms.FJournal;
import ru.apertum.qsystem.client.forms.FReception;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.extra.IStartReception;

/**
 *
 * @author Evgeniy Egorov
 */
public class JournalEmbeddedReception implements IStartReception {

    @Override
    public void start(FReception fc) {
        
        // Прикрутим сервер на сокет.
        final ServerSocket socket;
        try {
            socket = new ServerSocket(50015);
        } catch (IOException ex) {
            try {
                try (Socket ups = new Socket("localhost", 50015)) {
                    ups.getOutputStream().close();
                }
            } catch (UnknownHostException ex1) {
                System.out.println("1 " + ex1);
            } catch (IOException ex1) {
                System.out.println("2 " + ex1);
            }
            QLog.l().logger().error("Старт вторй копии приложения Journal.");
            return;
        }
        try {
            socket.close();
        } catch (IOException ex) {
        }
        final Thread thresd = new Thread(() -> {
            final ServerSocket socket1;
            try {
                socket1 = new ServerSocket(50015);
                socket1.setSoTimeout(5000);
            } catch (IOException ex) {
                throw new ServerException(ex.toString());
            }
            while (!Thread.interrupted()) {
                try {
                    socket1.accept();
                    QLog.l().logger().info("НЕ Откроем главную форму вместо повторного запуска.");
                    
                } catch (SocketTimeoutException ex) {
                } catch (IOException ex) {
                    QLog.l().logger().error("Что-то с сеткой.", ex);
                }
            }
        });

        thresd.setDaemon(true);
        thresd.start();
        
        
        final JPanel panel = new JPanel();
        fc.tabsPane.addTab("Journal", panel);
        panel.setLayout(new GridLayout(1, 1));
        final FJournal form = new FJournal();
        panel.add(form.mainPanel);
    }

    @Override
    public String getDescription() {
        return "Embedded mode of Journal CRM for receptionist.";
    }

    @Override
    public long getUID() {
        return 16L;
    }

}
