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
 * 
 */
package ru.apertum.journal.common;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Evgeniy Egorov
 */
public class Player implements Runnable {

    private final String resourceName;

    public Player(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public void run() {
        doSound(resourceName);
    }

    public static void playAudioResource(String resourceName) {
        new Thread(new Player(resourceName)).start();
    }

    private static synchronized void doSound(String resourceName) {
        //System.err.println("Пытаемся воспроизвести звуковой ресурс \"" + resourceName + "\"");
        AudioInputStream ais = null;
        try {
            ais = AudioSystem.getAudioInputStream(Object.class.getResource(resourceName));
            //get the AudioFormat for the AudioInputStream 
            AudioFormat audioformat = ais.getFormat();
            //printAudioFormatInfo(audioformat);
            //ULAW & ALAW format to PCM format conversion 
            if ((audioformat.getEncoding() == AudioFormat.Encoding.ULAW)
                    || (audioformat.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat newformat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        audioformat.getSampleRate(),
                        audioformat.getSampleSizeInBits() * 2,
                        audioformat.getChannels(),
                        audioformat.getFrameSize() * 2,
                        audioformat.getFrameRate(),
                        true);
                ais = AudioSystem.getAudioInputStream(newformat, ais);
                audioformat = newformat;
                //printAudioFormatInfo(audioformat);
            }
            //checking for a supported output line 
            DataLine.Info datalineinfo = new DataLine.Info(SourceDataLine.class, audioformat);
            if (!AudioSystem.isLineSupported(datalineinfo)) {
                System.out.println("Line matching " + datalineinfo + " is not supported.");
            } else {
                byte[] sounddata;
                try (SourceDataLine sourcedataline = (SourceDataLine) AudioSystem.getLine(datalineinfo)) {
                    sourcedataline.open(audioformat);
                    sourcedataline.start();
                    int framesizeinbytes = audioformat.getFrameSize();
                    int bufferlengthinframes = sourcedataline.getBufferSize() / 8;
                    int bufferlengthinbytes = bufferlengthinframes * framesizeinbytes;
                    sounddata = new byte[bufferlengthinbytes];
                    int numberofbytesread = 0;
                    while ((numberofbytesread = ais.read(sounddata)) != -1) {
                        sourcedataline.write(sounddata, 0, numberofbytesread);
                    }
                    int frPos = -1;
                    while (frPos != sourcedataline.getFramePosition()) {
                        frPos = sourcedataline.getFramePosition();
                        Thread.sleep(50);
                    }
                }
                sounddata = null;
            }

            //printAudioFormatInfo(audioformat);
        } catch (InterruptedException ex) {
            System.err.println("InterruptedException: " + ex);
        } catch (LineUnavailableException lue) {
            System.err.println("LineUnavailableException: " + lue.toString());
        } catch (UnsupportedAudioFileException uafe) {
            System.err.println("UnsupportedAudioFileException: " + uafe.toString());
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.toString());
        } finally {
            try {
                if (ais != null) {
                    ais.close();
                }
            } catch (IOException ex) {
                System.err.println("IOException при освобождении входного потока медиаресурса: " + ex);
            }
        }
    }
}
