import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AccountFrame extends JFrame {
    DataOutputStream out;
    DataInputStream in;
    JLabel userLabel;
    JLabel balanceLabel;
    JButton transferButton;
    JTextField recipientInput;
    JButton withdrawalButton;
    JTextField withdrawalInput;
    JPanel panel;
    JLabel amountLabel;
    JLabel recipentLabel;

    public AccountFrame(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        userLabel = new JLabel();
        userLabel.setText(Client.username);

        balanceLabel = new JLabel();
        balanceLabel.setText(Client.userBalance);

        amountLabel= new JLabel("Kwota:");
        recipentLabel = new JLabel("Odbiorca:");

        transferButton = new JButton("Przelew");
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float amount = Float.parseFloat(withdrawalInput.getText());
                if(amount <= 0){
                    JOptionPane.showMessageDialog(null, "Błędna kwota", "Błędna kwota", JOptionPane.WARNING_MESSAGE);
                }else{
                    if(recipientInput.getText().equals("")){
                        JOptionPane.showMessageDialog(null, "Brak odbiorcy", "Przelew", JOptionPane.WARNING_MESSAGE);
                    }else{
                        String outmsg = "2:" + Client.username + ":"  + recipientInput.getText() + ":"+ withdrawalInput.getText();
                        try {
                            out.writeUTF(outmsg);
                            out.flush();
                            String inmsg = in.readUTF();
                            System.out.println("IN: " + inmsg);
                            if(inmsg .equals("ERR")){
                                JOptionPane.showMessageDialog(null,"Coś poszło nie tak", "Błąd", JOptionPane.ERROR_MESSAGE);
                            }
                            if(inmsg.equals("TOOMUCH")){
                                JOptionPane.showMessageDialog(null, "Za mało środków na tą operacje", "Błąd", JOptionPane.WARNING_MESSAGE);
                            }
                            if(!inmsg.equals("ERR") && !inmsg.equals("TOOMUCH") && !inmsg.equals("NOTFOUND")){
                                System.out.println("Odebrano: " + inmsg);
                                JOptionPane.showMessageDialog(null, "Operacja udana", "Przlew", JOptionPane.INFORMATION_MESSAGE);
                                balanceLabel.setText(inmsg);
                            }
                            if(inmsg.equals("NOTFOUND")){
                                JOptionPane.showMessageDialog(null, "Nie znaleziono odbiorcy", "Błąd", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        recipientInput = new JTextField();


        withdrawalInput = new JTextField();
        withdrawalButton = new JButton("Wypłata");
        withdrawalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float amount = Float.parseFloat(withdrawalInput.getText());
                if(amount <= 0){
                    JOptionPane.showMessageDialog(null, "Błędna kwota", "Błędna kwota", JOptionPane.WARNING_MESSAGE);
                }else{
                    String outmsg = "3:" + Client.username + ":" + withdrawalInput.getText();
                    try {
                        out.writeUTF(outmsg);
                        out.flush();
                        String inmsg = in.readUTF();
                        if(inmsg .equals("ERR")){
                            JOptionPane.showMessageDialog(null,"Coś poszło nie tak", "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                        if(inmsg.equals("TOOMUCH")){
                            JOptionPane.showMessageDialog(null, "Za mało środków na tą operacje", "Błąd", JOptionPane.WARNING_MESSAGE);
                        }
                        if(!inmsg.equals("ERR") && !inmsg.equals("TOOMUCH")){
                            JOptionPane.showMessageDialog(null, "Operacja udana", "Wypłata", JOptionPane.INFORMATION_MESSAGE);
                            balanceLabel.setText(inmsg);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        panel = new JPanel(new GridLayout(4, 1));

        panel.add(userLabel);
        panel.add(balanceLabel);
        panel.add(amountLabel);
        panel.add(withdrawalInput);
        panel.add(recipentLabel);
        panel.add(recipientInput);
        panel.add(transferButton);
        panel.add(withdrawalButton);

        add(panel, BorderLayout.CENTER);
        setSize(300, 200);
        pack();
    }
}
