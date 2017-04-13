package ndg;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.JRadioButton;

import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JRadioButton rdbtnDefaultRec;
	private JRadioButton rdbtnHdRec;

	private String path;
	private Sound s;
	private int sampleRate = 44000;
	private int sampleSize = 16;
	private int channels = 1;
	private boolean flag = false;
	private JTextField samplerate;
	private JTextField samplesize;
	private JTextField channel;

	public Panel() {
		setPreferredSize(new Dimension(438, 191));
		setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(42, 23, 351, 145);
		add(panel);
		panel.setLayout(null);

		JButton btnRec = new JButton("Rec");
		btnRec.setBounds(288, 37, 59, 25);
		panel.add(btnRec);

		JButton btnPathtoSave = new JButton("Path to Save");
		btnPathtoSave.setBounds(12, 70, 124, 25);
		panel.add(btnPathtoSave);

		JButton btnParar = new JButton("Stop Recording");
		btnParar.setBounds(205, 70, 142, 25);
		panel.add(btnParar);

		rdbtnDefaultRec = new JRadioButton("Default Recording");
		rdbtnDefaultRec.setBounds(8, 1, 152, 23);
		panel.add(rdbtnDefaultRec);
		rdbtnDefaultRec.setSelected(true);

		rdbtnHdRec = new JRadioButton("HD Recording");
		rdbtnHdRec.setBounds(8, 24, 120, 23);
		panel.add(rdbtnHdRec);

		JButton btnDefinirManualmente = new JButton("Define Manually");
		btnDefinirManualmente.setBounds(199, 0, 148, 25);
		panel.add(btnDefinirManualmente);
		btnDefinirManualmente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ret = JOptionPane.showConfirmDialog(null,
						getPanel(),
						"Define the Values: ",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if(ret == 0){
					sampleRate = Integer.parseInt(samplerate.getText());
					sampleSize = Integer.parseInt(samplesize.getText());
					channels = Integer.parseInt(channel.getText());
				}
			}
		});
		rdbtnHdRec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnDefaultRec.isSelected()){
					rdbtnDefaultRec.setSelected(false);
					rdbtnHdRec.setSelected(true);

					sampleRate = 44000;
					sampleSize = 16;
					channels = 1;
				}
			}
		});
		rdbtnDefaultRec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnHdRec.isSelected()){
					rdbtnDefaultRec.setSelected(true);
					rdbtnHdRec.setSelected(false);

					sampleRate = 44000;
					sampleSize = 32;
					channels = 2;
				}
			}
		});
		btnParar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(s!=null) s.finish();
			}
		});
		btnPathtoSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(); 
				String choosertitle = "Path to Save";
				
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle(choosertitle);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(Panel.this) == JFileChooser.APPROVE_OPTION) { 
					path = chooser.getSelectedFile().toString();
					flag = true;
				}
			}
		});
		btnRec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!flag){
						JOptionPane.showMessageDialog(null,
								"Choose the saving path first",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					Properties props = new Properties();
					FileInputStream file = new FileInputStream("config.properties");
					props.load(file);

					Socket client;
					client = new Socket(props.getProperty("serverIP"), Integer.parseInt(props.getProperty("serverPort")));
					client.setTcpNoDelay(true);

					ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());

					oos.writeObject("ready");

					oin.readObject();
					client.close();
					try{
						s = new Sound(path, sampleRate, sampleSize, channels);
						s.start();
					}catch(Exception ex1){
						JOptionPane.showMessageDialog(null,
								"Error on Recording Line",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

	}

	private JPanel getPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 2));

		JLabel lblSampleRate = new JLabel("Sample Rate:");
		lblSampleRate.setBounds(42, 316, 94, 15);
		panel.add(lblSampleRate);

		JLabel lblSampleSize = new JLabel("Sample Size:");
		lblSampleSize.setBounds(42, 341, 94, 15);
		panel.add(lblSampleSize);

		JLabel lblChannels = new JLabel("Channels:");
		lblChannels.setBounds(65, 368, 71, 15);
		panel.add(lblChannels);

		samplerate = new JTextField();
		samplerate.setText("44000");
		samplerate.setBounds(143, 339, 114, 19);
		panel.add(samplerate);
		samplerate.setColumns(10);

		samplesize = new JTextField();
		samplesize.setText("16");
		samplesize.setBounds(143, 366, 114, 19);
		panel.add(samplesize);
		samplesize.setColumns(10);

		channel = new JTextField();
		channel.setText("1");
		channel.setBounds(143, 314, 114, 19);
		panel.add(channel);
		channel.setColumns(10);

		panel.setBounds(267, 273, 243, 120);
		return panel;
	}

	public static void main(String s[]) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame("");
					JPanel panel = new Panel();
					frame = new JFrame();
					frame.setSize(panel.getPreferredSize());
					frame.setContentPane(panel);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					frame.setResizable(false);

					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
