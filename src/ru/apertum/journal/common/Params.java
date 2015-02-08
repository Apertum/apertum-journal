/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.journal.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Просто настройки проги. Хранятся они в файлике, есть всегда.
 * @author Evgeniy Egorov
 */
public class Params {

    final static String CFG_FILE_NAME = "config/journal.properties";
    final PropertiesConfiguration cnf;

    private Params() {
        try {
            cnf = new PropertiesConfiguration(CFG_FILE_NAME);
        } catch (ConfigurationException ex) {
            throw new ClientException("Не прочитался файл настроек " + CFG_FILE_NAME, ex);
        }

    }

    public static Params getInstance() {
        return ParamsHolder.INSTANCE;
    }

    private static class ParamsHolder {

        private static final Params INSTANCE = new Params();
    }

    public void save() {
        try {
            cnf.save();
        } catch (ConfigurationException ex) {
            throw new ClientException("Не сохранился файл настроек " + CFG_FILE_NAME, ex);
        }
    }

    public int getPortDatabits() {
        return cnf.getInt("port.databits", 8);
    }

    public void setPortDatabits(int portDatabits) {
        cnf.setProperty("port.databits", portDatabits);
    }

    public int getPortParity() {
        return cnf.getInt("port.parity", 0);
    }

    public void setPortParity(int portParity) {
        cnf.setProperty("port.parity", portParity);
    }

    public int getPortSpeed() {
        return cnf.getInt("port.speed", 921600);
    }

    public void setPortSpeed(int portSpeed) {
        cnf.setProperty("port.speed", portSpeed);
    }

    public int getPortStopbits() {
        return cnf.getInt("port.stopbits", 1);
    }

    public void setPortStopbits(int portStopbits) {
        cnf.setProperty("port.stopbits", portStopbits);
    }

    public int getColorSectors() {
        return cnf.getInt("colore.sectors", 16);
    }
}
