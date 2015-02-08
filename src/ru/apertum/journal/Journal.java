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
package ru.apertum.journal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 * The main class of the application.
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class Journal extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        /*
        JournalView dw;
        try {
            show(dw = new JournalView(this));
        } catch (Exception ex) {
            QLog.l().logger().error("Приложение не может быть запущено", ex);
            
            JOptionPane.showMessageDialog(null,
                    "Приложение не может быть запущено.\n\n" + (ex.getMessage() != null ? ex.getMessage() : "") + ":\n\n" + ex,
                    "Journal",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        dw.setTrueFocus();
        */
    }

    /**
     * This method is to initialize the specified window by injecting resources. Windows shown in our application come fully initialized from the GUI builder,
     * so this additional configuration is not needed.
     *
     * @param root
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of Journal
     */
    
    public static Journal getApplication() {
        return Application.getInstance(Journal.class);
    }

    /**
     * Main method launching the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        QLog.initial(args, 1);
        //Locale.setDefault(Locales.getInstance().getLangCurrent());
        // Загрузка плагинов из папки plugins
        if (QLog.l().isPlaginable()) {
            Uses.loadPlugins("./plugins/");
        }
        
        
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
            throw new ServerException("Старт вторй копии приложения Journal.");
        }

        try {
            socket.close();
        } catch (IOException ex) {
        }
        Thread thresd = new Thread(() -> {
            final ServerSocket socket1;
            try {
                socket1 = new ServerSocket(50015);
                socket1.setSoTimeout(5000);
            }catch (IOException ex) {
                throw new ServerException(ex.toString());
            }
            while (!Thread.interrupted()) {
                try {
                    socket1.accept();
                    QLog.l().logger().info("Откроем главную форму вместо повторного запуска.");
                    getApplication().getMainFrame().setAlwaysOnTop(true);
                    getApplication().getMainFrame().setVisible(true);
                    getApplication().getMainFrame().setState(JFrame.NORMAL);
                    getApplication().getMainFrame().setAlwaysOnTop(false);
                }catch (SocketTimeoutException ex) {
                }catch (IOException ex) {
                    QLog.l().logger().error("Что-то с сеткой.", ex);
                }
            }
        });

        thresd.setDaemon(true);
        thresd.start();

        launch(Journal.class, args);
    }
}
