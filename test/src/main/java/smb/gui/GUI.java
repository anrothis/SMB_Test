package smb.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import smb.client.SambaClient;

public class GUI extends JFrame {

    private SambaClient sambaClient;
    private int width, height;

    public GUI() {
        super("Netzwerk Ordner abfrage");
        // sambaClient = new SambaClient(this);
        int frameSize = 15;
        int space = 5;
        int textFieldHeight = 30;
        int labelHeight = 20;
        width = 600;
        height = 24 * textFieldHeight;
        Point startingPosition = new Point();
        startingPosition.setLocation(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - width / 2,
                Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - height / 2);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.darkGray);
        panel.setPreferredSize(new Dimension(width, height));

        int pos = 1;
        JLabel hostLabel = new JLabel("Host IP:");
        hostLabel.setForeground(Color.LIGHT_GRAY);
        hostLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(hostLabel);

        JTextField hostIPAdressTextArea = new JTextField("");
        hostIPAdressTextArea.setBorder(null);
        hostIPAdressTextArea.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, textFieldHeight);
        panel.add(hostIPAdressTextArea);

        pos += 1;
        JLabel userLabel = new JLabel("User:");
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(userLabel);

        JTextField userTextField = new JTextField("");
        userTextField.setBorder(null);
        userTextField.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, textFieldHeight);
        panel.add(userTextField);

        pos += 1;
        JLabel passwordLabel = new JLabel("Passwort:");
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        passwordLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(passwordLabel);

        JPasswordField passwordTextArea = new JPasswordField();
        passwordTextArea.setBorder(null);
        passwordTextArea.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, textFieldHeight);
        panel.add(passwordTextArea);

        pos += 1;
        JLabel mountNameLabel = new JLabel("Share Ordnername:");
        mountNameLabel.setForeground(Color.LIGHT_GRAY);
        mountNameLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(mountNameLabel);

        JTextField mountNameTextField = new JTextField("");
        mountNameTextField.setBorder(null);
        mountNameTextField.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, textFieldHeight);
        panel.add(mountNameTextField);

        pos += 1;
        JLabel subFolderLabel = new JLabel("Unterordner:");
        subFolderLabel.setForeground(Color.LIGHT_GRAY);
        subFolderLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(subFolderLabel);

        JTextField subFolderTextField = new JTextField("");
        subFolderTextField.setBorder(null);
        subFolderTextField.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, textFieldHeight);
        panel.add(subFolderTextField);

        pos += 1;
        JLabel fileFilterLabel = new JLabel("Filtersyntax:");
        fileFilterLabel.setForeground(Color.LIGHT_GRAY);
        fileFilterLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(fileFilterLabel);

        JTextField fileFilterTextField = new JTextField("*");
        fileFilterTextField.setBorder(null);
        fileFilterTextField.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, textFieldHeight);
        panel.add(fileFilterTextField);

        pos += 1;
        JLabel treeStructLabel = new JLabel("Dateibaum:");
        treeStructLabel.setForeground(Color.LIGHT_GRAY);
        treeStructLabel.setBounds(frameSize, getYLabel(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, labelHeight);
        panel.add(treeStructLabel);

        JTextArea treeStructure = new JTextArea();
        treeStructure.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, 7 * textFieldHeight);
        treeStructure.setAutoscrolls(true);

        JScrollPane scrollTree = new JScrollPane(treeStructure);
        scrollTree.setBounds(frameSize, getYTextField(pos, frameSize, space, textFieldHeight, labelHeight),
                width - 2 * frameSize, 9 * textFieldHeight);
        panel.add(scrollTree);

        JButton refreshList = new JButton("Request file list...");
        refreshList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                treeStructure.setText("Fetching Data from " + mountNameTextField.getText() + "/"
                        + subFolderTextField.getText() + "... \n");
                sambaClient = new SambaClient(null, userTextField.getText(), passwordTextArea.getPassword(),
                        mountNameTextField.getText(), hostIPAdressTextArea.getText(), fileFilterTextField.getText(),
                        subFolderTextField.getText());

                Thread smbList = new Thread(new Runnable() {
                    public void run() {
                        sambaClient.retrieveFolderContentList();
                    }
                });

                Thread updateTextArea = new Thread(new Runnable() {
                    public void run() {
                        while (smbList.isAlive()) {
                            try {
                                Thread.sleep(200);
                                treeStructure.append("-");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        treeStructure.append("\n");
                        String[] tabel = new String[sambaClient.getFolderList().size()];
                        for (int i = 0; i < sambaClient.getFolderList().size(); i++) {
                            tabel[i] = sambaClient.getFolderList().get(i);
                        }
                        for (String[] string : sambaClient.getFilePathList()) {
                            treeStructure.append(string[1] + string[2] + "\n");
                        }
                    }
                });
                smbList.start();
                updateTextArea.start();

            }
        });
        refreshList.setBounds(frameSize, height - frameSize - textFieldHeight, width - 2 * frameSize, textFieldHeight);
        panel.add(refreshList);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel);
        setLocation(startingPosition);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public void setTextArea() {

    }

    private int getYLabel(int pos, int frameSize, int space, int textFieldHeight, int labelHeight) {
        return frameSize + (pos - 1) * (2 * space + textFieldHeight + labelHeight);

    }

    private int getYTextField(int pos, int frameSize, int space, int textFieldHeight, int labelHeight) {

        return frameSize + space + labelHeight + (pos - 1) * (2 * space + textFieldHeight + labelHeight);
    }
}
