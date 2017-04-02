package network;
import encription.KeyExchangeController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ui.ChatDialog;
import ui.Menu;

import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yarbs on 31/03/2017.
 */
public class Network implements Runnable {

    public static final int REQUEST_ID = 1;
    public static final int RETURN_ID = 2;
    public static final int REQUEST_CHAT = 3;
    public static final int KEY_STAGE_1 = 4;
    public static final int KEY_STAGE_2 = 5;
    public static final int MESSAGE = 6;
    public static final int ERROR = -1;

    public static final int NO_USER_ERROR = 1;
    public static final int INVALID_CHAT_ERROR = 2;

    protected int port;
    protected String ip;
    protected static Socket socket;
    private PrintWriter pwrite;
    private BufferedReader receiveRead;

    private HashMap<String,ChatDialog> chats = new HashMap<>();

    public Network(String ip, int port){
        try {
            this.ip = ip;
            this. port = port;
            socket = new Socket(ip, port);
            // sending to client (pwrite object)
            OutputStream ostream = socket.getOutputStream();
            pwrite = new PrintWriter(ostream, true);

            // receiving from server ( receiveRead  object)
            InputStream istream = socket.getInputStream();
            receiveRead = new BufferedReader(new InputStreamReader(istream));

        }catch (ConnectException e) {
            JOptionPane.showMessageDialog(Menu.frame,
                    "Cannot connect to the server",
                    "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(Menu.frame,
                    "Unknown host",
                    "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
            pwrite.println(data);       // sending to server
            pwrite.flush();// flush the data
    }

    @Override
    public void run() {
        while (true) {
            try {
                String receiveMessage;
                if ((receiveMessage = receiveRead.readLine()) != null) //receive from server
                {
                    System.out.println(receiveMessage); // displaying at DOS prompt
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(receiveMessage);
                    JSONObject object = (JSONObject) obj;
                    ChatDialog chatDialog;
                    switch (((Long) object.get("PacketType")).intValue()){
                        case RETURN_ID:
                            Menu.i.setID((String) object.get("ID"));
                            break;
                        case REQUEST_CHAT:
                            chatDialog = new ChatDialog(this,(String) object.get("ChatID"),(JSONArray)object.get("Members"));
                            chats.put((String) object.get("ChatID"),chatDialog);
                            break;
                        case KEY_STAGE_1:
                            chatDialog=chats.get((String) object.get("ChatID"));
                            chatDialog.keyExchangeController = new KeyExchangeController(((Long) object.get("p")).intValue(),((Long) object.get("q")).intValue());
                            keyStage2((String) object.get("ChatID"),chatDialog.keyExchangeController.getPublicExponent());
                            break;
                        case KEY_STAGE_2:
                            chatDialog=chats.get((String) object.get("ChatID"));
                            chatDialog.setSharedSecret(chatDialog.keyExchangeController.getSharedSecret(((Long) object.get("A")).intValue()));
                            break;
                        case ERROR:
                            errorHandler(((Long) object.get("ErrorCode")).intValue());
                            break;
                        case MESSAGE:
                            chatDialog=chats.get((String) object.get("ChatID"));
                            chatDialog.newMessage((String) object.get("Message"));
                    }
                }
            }catch(SocketException e){
                JOptionPane.showMessageDialog(Menu.frame,
                        "The server has cut your connection",
                        "Server error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch(IOException e){
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void errorHandler(int errorCode){
        switch (errorCode){
            case NO_USER_ERROR:
                JOptionPane.showMessageDialog(Menu.frame, "The user ID you have entered is not valid");
                break;
            case INVALID_CHAT_ERROR:
                JOptionPane.showMessageDialog(Menu.frame, "The chat your trying to communicate with is no longer valid");
                break;
        }
    }

    public void keyStage2(String chatName, int a){
        JSONObject obj = new JSONObject();
        obj.put("PacketType", Network.KEY_STAGE_2);
        obj.put("ChatID", chatName);
        obj.put("A", a);

        send(obj.toJSONString());
    }

    public void sendMessage(String chatName, String message){
        JSONObject obj = new JSONObject();
        obj.put("PacketType", Network.MESSAGE);
        obj.put("ChatID", chatName);
        obj.put("Message", message);

        send(obj.toJSONString());
    }

}
