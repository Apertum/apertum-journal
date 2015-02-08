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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import ru.apertum.journal.forms.FJournal;
import ru.apertum.qsystem.client.forms.FClient;
import ru.apertum.qsystem.client.model.QTray;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.extra.IStartClient;

/**
 *
 * @author Evgeniy Egorov
 */
public class JournalEmbedded implements IStartClient {

    @Override
    public void start(FClient fc) {
        
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
        
        
        final FJournal form = new FJournal();
        final JMenuItem mi = new JMenuItem(new AbstractAction(null) {

            @Override
            public void actionPerformed(ActionEvent e) {
                form.setVisible(true);
                form.setState(JFrame.NORMAL);
            }
        });
        mi.setText("Journal");
        fc.fileMenu.add(new JSeparator());
        fc.fileMenu.add(mi);

        final java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (fc.getCustomer() != null && fc.getCustomer().getInput_data() != null) {
                    form.applyFilter(fc.getCustomer());
                }
                form.setVisible(true);
                form.setState(JFrame.NORMAL);
            }
        };

        fc.labelNextNumber.addMouseListener(ma);
        fc.labelNextCustomerInfo.addMouseListener(ma);

        form.setLocationRelativeTo(null);
        final JButton b = new JButton(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setVisible(true);
                fc.setState(JFrame.NORMAL);
            }
        });
        b.setText("Перейти к управлению очередью");
        b.setSize(b.getWidth(), 14);
        b.setPreferredSize(new Dimension(200, 12));
        form.menuBarJournal.add(b);

        QTray.getInstance(fc, null, null).addItem("-", (ActionEvent e) -> {
        });
        QTray.getInstance(fc, null, null).addItem("Journal", (ActionEvent e) -> {
            form.setVisible(true);
            form.setState(JFrame.NORMAL);
        });
    }

    @Override
    public String getDescription() {
        return "Встроенное использование журнала.";
    }

    @Override
    public long getUID() {
        return 16L;
    }

}
