package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.service.AttendanceService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeTracker extends JPanel {

    private JLabel status;
    private JCheckBox toggle;

    private AttendanceService service;
    private String employeeID;

    public TimeTracker(String employeeID){

        this.employeeID = employeeID;
        this.service = new AttendanceService();

        setLayout(new BorderLayout(15,15));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        add(createHeader(), BorderLayout.NORTH);
        add(createCard(), BorderLayout.CENTER);
    }

    private JLabel createHeader(){
        JLabel lbl = new JLabel("Time Tracker");
        lbl.setFont(UIStyle.FONT_TITLE);
        return lbl;
    }

    private JPanel createCard(){

        JPanel card = UIStyle.createElevatedCard();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JButton in = UIStyle.createPrimaryButton("Time In");
        JButton out = UIStyle.createSecondaryButton("Time Out");

        toggle = new JCheckBox("12-hour format");
        status = new JLabel("Not logged in");

        in.addActionListener(e->timeIn());
        out.addActionListener(e->timeOut());

        card.add(in);
        card.add(Box.createVerticalStrut(10));
        card.add(out);
        card.add(Box.createVerticalStrut(10));
        card.add(toggle);
        card.add(Box.createVerticalStrut(10));
        card.add(status);

        return card;
    }

    private void timeIn(){
        LocalTime t = LocalTime.now();
        service.recordTimeIn(employeeID,t);

        status.setText("Time In: "+format(t));
    }

    private void timeOut(){
        LocalTime t = LocalTime.now();
        service.recordTimeOut(employeeID,t);

        status.setText(status.getText()+" | Time Out: "+format(t));
    }

    private String format(LocalTime t){
        return toggle.isSelected()
                ? t.format(DateTimeFormatter.ofPattern("hh:mm a"))
                : t.toString();
    }
}