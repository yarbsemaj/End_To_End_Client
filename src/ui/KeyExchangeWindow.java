package ui;

import encription.KeyExchangeController;
import network.Network;

import javax.swing.*;

/**
 * Created by yarbs on 30/03/2017.
 */
public class KeyExchangeWindow {
    private JTextField pTextField;
    private JTextField gTextField;
    private JButton stage1Button;
    private JPanel panel;
    private JTextField textField1;
    private JButton stage2Button;
    private JLabel aLabel;
    private JLabel ssLabel;
    private KeyExchangeController keyExchangeController;
    private Network network;
    public static JFrame frame;

    public KeyExchangeWindow() {
        network = new Network("127.0.0.1",25000);
        Thread t1 =new Thread(network);
        t1.start();

        stage1Button.addActionListener(e -> {
            keyExchangeController = new KeyExchangeController(Integer.valueOf(pTextField.getText()),Integer.valueOf(gTextField.getText()));
            aLabel.setText(String.valueOf(keyExchangeController.getPublicExponent()));
        });
        stage2Button.addActionListener(e -> ssLabel.setText(String.valueOf(keyExchangeController.getSharedSecret(Integer.valueOf(textField1.getText())))));
    }

    public static void main(String[] args) {
        frame = new JFrame("Key Exchange Window");
        frame.setContentPane(new KeyExchangeWindow().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

