import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginFrame extends JFrame implements ActionListener{
    JLabel loginLabel;
    JTextField loginInput;
    JLabel passLabel;
    JPasswordField passInput;
    JButton submitButton;
    JPanel panel;;
    DataInputStream in;
    DataOutputStream out;

    public LoginFrame(DataInputStream in, DataOutputStream out){
        this.in = in;
        this.out = out;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Login
        loginLabel = new JLabel();
        loginLabel.setText("User Name :");
        loginInput = new JTextField();

        // Password
        passLabel = new JLabel();
        passLabel.setText("Password :");
        passInput = new JPasswordField();

        // Submit

        submitButton = new JButton("Wyślij");
        submitButton.addActionListener(this);

        panel = new JPanel(new GridLayout(3, 1));

        panel.add(loginLabel);
        panel.add(loginInput);
        panel.add(passLabel);
        panel.add(passInput);

        panel.add(submitButton);
        add(panel, BorderLayout.CENTER);
        setSize(300,200);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = loginInput.getText();
        String password = passInput.getText();
        String outmsg = "1:" + username + ":" + password;
        try {
            out.writeUTF(outmsg);
            String inmsg = in.readUTF();
            if(!inmsg.equals("NOK")){
                JOptionPane.showMessageDialog(this, "Logowanie powiodło się", "OK", JOptionPane.INFORMATION_MESSAGE);
                Client.isLogged = true;
                Client.username = username;
                Client.userBalance = inmsg;
            }else{
                JOptionPane.showMessageDialog(this, "Logowanie nie powiodło się", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
