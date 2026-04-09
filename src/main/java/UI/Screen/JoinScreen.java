package UI.Screen;

import javax.swing.*;
import java.awt.*;

import Manager.GameManager;
import Model.Player;

public class JoinScreen {

    public void showJoinScreen(GameManager gm) {
        //QR-Code that lets people join through the browser
        JPanel panelJoin = new JPanel(new BorderLayout(5, 5));
        panelJoin.setBackground(Color.CYAN);

        JLabel title = new JLabel("DER GROSSE PREIS");
        title.setFont(new Font("Arial", Font.BOLD, 60));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panelJoin.add(title, BorderLayout.NORTH);

        //panelJoin.add(list of people, BorderLayout.WEST);

        //panelJoin.add(QR Code, BorderLayout.EAST);

        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(500, 100));
        startButton.addActionListener(e -> {
            if (gm.getPlayers().isEmpty()){
                gm.getPlayers().add(new Player("Test Player"));
            }
            gm.setQuestions();
            gm.getMainScreen().showMainScreen(gm);
        });
        panelJoin.add(startButton, BorderLayout.SOUTH);

        gm.add(panelJoin);
        gm.setVisible(true);
    }
}
