package ui;

import encription.KeyExchangeController;
import network.Network;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.ByteUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by yarbs on 31/03/2017.
 */
public class ChatDialog {
    private JTextField textField1;
    private JButton sendButton;
    private JTextPane bBodyBTextPane;
    private JPanel panel;
    private Network network;
    private SecretKeySpec secretKeySpec;

    private String chatName;

    public KeyExchangeController keyExchangeController;

    public ChatDialog(Network network, String chatName, JSONArray members) {
        JFrame window = new JFrame("Chat Dialog");
        window.setContentPane(panel);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.pack();
        window.setVisible(true);

        addMessage("<b>Members :</b>"+ members.toJSONString());

        JMenuBar jMenuBar =new JMenuBar();

        JMenuItem jMenuItem = new JMenuItem("New Key");

        jMenuBar.add(jMenuItem);

        window.setJMenuBar(jMenuBar);

        this.network = network;
        this.chatName = chatName;

        sendButton.addActionListener(e -> {
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

                byte[] encrypted = cipher.doFinal((textField1.getText()).getBytes());

                network.sendMessage(chatName,ByteUtils.bytesToHex(encrypted));
                addMessage("<b style='color: red;'>You: </b>"+textField1.getText());

                textField1.setText("");

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e1) {
                e1.printStackTrace();
            }

        });
        jMenuItem.addActionListener(e -> requestKey());
    }

    public void newMessage(String message){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] original = cipher.doFinal(ByteUtils.hexStringToByteArray(message));
            String originalString = new String(original);
           addMessage("<b style='color: blue;'>Them: </b>"+originalString);
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    public void setSharedSecret(int sharedSecret) {
        addMessage("<b>Key changed</b><br>");
        try {
            byte[] key = (String.valueOf(sharedSecret)).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void requestKey(){
        JSONObject obj = new JSONObject();
        obj.put("PacketType", Network.KEY_STAGE_1);
        obj.put("ChatID", chatName);

        network.send(obj.toJSONString());
    }

    public void addMessage(String message){
        bBodyBTextPane.setText(message+bBodyBTextPane.getText()+"<br>");
    }

}
