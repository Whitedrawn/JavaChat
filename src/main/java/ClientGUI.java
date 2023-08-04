import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.*;
import java.util.ArrayList;
import java.util.Arrays;

/***************************************************
 *    Module: COMP1549 - Advanced Programming
 *    Title: Java application: Multi Thread Chat Server
 *    Author: David Buendia Soler(001076459), Sergiu Macsim (001120364)
 *    Group : 9
 *    Date: 29/03/2022

 Reference:

 *    Title: <java-chat>
 *    Author: <pchampio>
 *    Date: <12 August 2017>
 *    Availability: <https://github.com/pchampio/java-chat>
 ****************************************************/

public class ClientGUI extends Thread {

     JTextPane mainchat = new JTextPane();
     JTextPane userList = new JTextPane();
     JTextField msgBox = new JTextField();
     String oldMsg = "";
     Thread read;
     String serverName;
     int PORT;
     String name;
    BufferedReader input;
    PrintWriter output;
    Socket server;

    public ClientGUI() {
        this.serverName = "localhost";
        this.PORT = 50000;
        this.name = "Username";

        String fontfamily = "Helvetica";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        final JFrame windowFr = new JFrame("Chat Server");
        windowFr.getContentPane().setLayout(null);
        windowFr.setSize(700, 500);
        windowFr.setResizable(false);
        windowFr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main chat gui
        mainchat.setBounds(25, 25, 490, 320);
        mainchat.setFont(font);
        mainchat.setMargin(new Insets(6, 6, 6, 6));
        mainchat.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(mainchat);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);

        mainchat.setContentType("text/html");
        mainchat.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // User list GUI
        userList.setBounds(520, 25, 156, 320);
        userList.setEditable(true);
        userList.setFont(font);
        userList.setMargin(new Insets(6, 6, 6, 6));
        userList.setEditable(false);
        JScrollPane jsplitsuser = new JScrollPane(userList);
        jsplitsuser.setBounds(520, 25, 156, 320);

        userList.setContentType("text/html");
        userList.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // Field box for user input to send message.
        msgBox.setBounds(0, 350, 400, 50);
        msgBox.setFont(font);
        msgBox.setMargin(new Insets(6, 6, 6, 6));
         JScrollPane jtextInputChatSP = new JScrollPane(msgBox);
        jtextInputChatSP.setBounds(25, 350, 650, 50);

        // Send Button
         JButton sendBtn = new JButton("Send");
        sendBtn.setFont(font);
        sendBtn.setBounds(575, 410, 100, 35);

        // Disconnect button
        JButton disBtn = new JButton("Disconnect");
        disBtn.setFont(font);
        disBtn.setBounds(25, 410, 130, 35);
        msgBox.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
                //Disconnect using command CTRL+C
                if (e.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar(17)+KeyEvent.getExtendedKeyCodeForChar(67)) {
                    disBtn.doClick();
                }

                // Get last message typed
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = msgBox.getText().trim();
                    msgBox.setText(oldMsg);
                    oldMsg = currentMessage;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = msgBox.getText().trim();
                    msgBox.setText(oldMsg);
                    oldMsg = currentMessage;
                }
            }
        });

        // Click on send button
        sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        // GUI to connect to server with all the fields.
         JTextField nameField = new JTextField(this.name);
         JTextField portField = new JTextField(Integer.toString(this.PORT));
         JTextField ipField = new JTextField(this.serverName);
         JButton connBtn = new JButton("Connect");

        // check if the fields are not empty
        nameField.getDocument().addDocumentListener(new TextListener(nameField, portField, ipField, connBtn));
        portField.getDocument().addDocumentListener(new TextListener(nameField, portField, ipField, connBtn));
        ipField.getDocument().addDocumentListener(new TextListener(nameField, portField, ipField, connBtn));

        // Position for the text fields, we could add some labels to it.
        connBtn.setFont(font);
        ipField.setBounds(25, 380, 135, 40);
        nameField.setBounds(375, 380, 135, 40);
        portField.setBounds(200, 380, 135, 40);
        connBtn.setBounds(575, 380, 100, 40);

        // Change colours of background
        mainchat.setBackground(Color.LIGHT_GRAY);
        userList.setBackground(Color.LIGHT_GRAY);

        //
        windowFr.add(connBtn);
        windowFr.add(jtextFilDiscuSP);
        windowFr.add(jsplitsuser);
        windowFr.add(nameField);
        windowFr.add(portField);
        windowFr.add(ipField);
        windowFr.setVisible(true);
        mainchat.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar(17)+KeyEvent.getExtendedKeyCodeForChar(67)) {
                    disBtn.doClick();
                }
            }
        });
        userList.addKeyListener(new KeyAdapter() {
            // Send messages using enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.getExtendedKeyCodeForChar(17)+KeyEvent.getExtendedKeyCodeForChar(67)) {
                    disBtn.doClick();
                }
            }
        });

        // Actions performed when connecting to server.
        connBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    name = nameField.getText();
                    String port = portField.getText();
                    serverName = ipField.getText();
                    PORT = Integer.parseInt(port);

                    appendToPane(mainchat, "<span>Connecting to " + serverName + " on port " + PORT + "...</span>");
                    server = new Socket(serverName, PORT);

                    appendToPane(mainchat, "<span>Connected to " +
                            server.getRemoteSocketAddress()+"</span>");

                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);

                    // send username to server
                    output.println(name);

                    // Change of GUI when pressing Connect button

                    read = new Read();
                    read.start();
                    windowFr.remove(nameField);
                    windowFr.remove(portField);
                    windowFr.remove(ipField);
                    windowFr.remove(connBtn);
                    windowFr.add(sendBtn);
                    windowFr.add(jtextInputChatSP);
                    windowFr.add(disBtn);
                    windowFr.revalidate();
                    windowFr.repaint();
                    mainchat.setBackground(Color.WHITE);
                    userList.setBackground(Color.WHITE);
                } catch (Exception ex) {
                    appendToPane(mainchat, "<span>Could not connect to Server</span>");
                    JOptionPane.showMessageDialog(windowFr, ex.getMessage());
                }

            }
        });

        // Actions performed when pressing disconnect button
        disBtn.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                windowFr.add(nameField);
                windowFr.add(portField);
                windowFr.add(ipField);
                windowFr.add(connBtn);
                windowFr.remove(sendBtn);
                windowFr.remove(jtextInputChatSP);
                windowFr.remove(disBtn);
                windowFr.revalidate();
                windowFr.repaint();
                read.interrupt();
                userList.setText(null);
                mainchat.setBackground(Color.LIGHT_GRAY);
                userList.setBackground(Color.LIGHT_GRAY);
                appendToPane(mainchat, "<span>Connection closed.</span>");
                output.close();
            }
        });

    }


    // Checks if all fields are not empty
    public class TextListener implements DocumentListener{
        JTextField field1;
        JTextField field2;
        JTextField field3;
        JButton connBtn;

        public TextListener(JTextField field1, JTextField field2, JTextField field3, JButton connBtn){
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.connBtn = connBtn;
        }

        public void changedUpdate(DocumentEvent e) {}

        public void removeUpdate(DocumentEvent e) {
            if(field1.getText().trim().equals("") ||
                    field2.getText().trim().equals("") ||
                    field3.getText().trim().equals("")
            ){
                connBtn.setEnabled(false);
            }else{
                connBtn.setEnabled(true);
            }
        }
        public void insertUpdate(DocumentEvent e) {
            if(field1.getText().trim().equals("") ||
                    field2.getText().trim().equals("") ||
                    field3.getText().trim().equals("")
            ){
                connBtn.setEnabled(false);
            }else{
                connBtn.setEnabled(true);
            }
        }

    }

    // Function to send messages
    public void sendMessage() {
        try {
            String message = msgBox.getText().trim();
            if (message.equals("")) {
                return;
            }
            this.oldMsg = message;
            output.println(message);
            msgBox.requestFocus();
            msgBox.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        ClientGUI client = new ClientGUI();
    }

    // Function to read incoming messages
    class Read extends Thread {
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null){

                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(message.split(", "))
                            );
                            userList.setText(null);
                            for (String user : ListUser) {
                                appendToPane(userList, "@" + user);
                            }
                        }else{
                            appendToPane(mainchat, message);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Failed to parse incoming message");
                }
            }
        }
    }

    // Sends text to main panel
    private void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
