package ui;

import network.Network;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by yarbs on 31/03/2017.
 */
public class Menu {
    private JButton startChatButton;
    private JPanel panel1;
    private JTextField userIDTextField;
    private JTextField textField2;
    private static String ID;
    public static Menu i;
    public static JFrame frame;

    private Network network;

    public static void main(String[] args) {
        frame = new JFrame("Menu");
        Menu.i = new Menu();
        frame.setContentPane(i.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Menu(){
        connectToServer("127.0.0.1");
        startChatButton.addActionListener(e -> {
            JSONObject obj = new JSONObject();
            String thereID = userIDTextField.getText();

            if(!thereID.equals(ID)) {
                obj.put("PacketType", Network.REQUEST_CHAT);
                obj.put("ID", thereID);
                network.send(obj.toJSONString());
            }
            else{
                JOptionPane.showMessageDialog(frame,"You cannot connect to yourself","Error",JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void setID(String ID){
        Menu.ID = ID;
        textField2.setText(ID);
    }

    public void connectToServer(String ip){
        network = new Network(ip,25000);
        Thread t1 =new Thread(network);
        t1.start();
    }

}
