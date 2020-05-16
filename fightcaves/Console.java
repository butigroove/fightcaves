/*
 * Copyright 2019, ProjectPact
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.fightcaves;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.Border;

public class Console {

    private JFrame frame = new JFrame();
    private JTextPane textPane = new JTextPane();
    private JScrollPane scrollPane = new JScrollPane();
    private Border border = BorderFactory.createLineBorder(Color.BLACK);
    private StringBuilder sb;

    public Console(FightCavePlugin plugin) {
        frame = new JFrame();
        textPane = new JTextPane();
        frame.setTitle("Fight Caves - DO NOT CLOSE");

        sb = new StringBuilder();

        textPane.setPreferredSize(new Dimension(775, 400));
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        textPane.setBackground(Color.BLACK);
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
        scrollPane = new JScrollPane(textPane);

        frame.getContentPane().add(scrollPane);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Console closed!");
                plugin.setConsoleRunning(false);
            }
        });
        frame.pack();
    }

    public void log(String message) {
        // System.out.println("Logging : " + message);
        if(message.contains("Current Wave NPCs:")){
            sb.append("<span style=\"color:green\">" + message + "</span><br>");
        } else if(message.contains("NEXT WAVE")) {
            sb.append("<span style=\"color:white\">" + message + "</span><br>");
        } else if(message.contains("INFO") || message.contains("TIP")) {
            sb.append("<span style=\"color:yellow\">" + message + "</span><br>");
        } else if(message.contains("Next Wave NPCs")) {
            sb.append("<span style=\"color:red\">" + message + "</span><br>");
        } else if(message.contains("PRAY MAGE! * ")) {
            sb.append("<font size=" + "\"" + "7" + "\"" + "><span style=\"color:#00BFFF\">" + message + "</span></font><br>");
        } else if(message.contains("PRAY RANGE!  --> ")) {
            sb.append("<font size=" + "\"" + "7" + "\"" + "><span style=\"color:#7FFF00\">" + message + "</span></font><br>");
        } else {
            sb.append("<span style=\"color:white\">" + message + "</span><br>");
        }
        if(textPane != null) {
            textPane.setText(sb.toString());
        }
    }

    public void clear() {
        textPane.setText("");
        sb.setLength(0);
    }

    public void init() {
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    private String getTime() {
        return new SimpleDateFormat("hh:mm:ss aa").format(Calendar.getInstance().getTime());
    }

}
