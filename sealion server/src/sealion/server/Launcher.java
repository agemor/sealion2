package sealion.server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.UIManager;
import javax.swing.JScrollPane;

import sealion.server.util.Logger;

import java.awt.Toolkit;

public class Launcher {

	private static JTextField commandLine;
	private static JTextArea output;
	private static JLabel connectionCount;
	private static NewUser newUserWindow;

	public static void show(String message) {
		output.append(message);
	}

	public static void updateConnectionCount(int num) {
		connectionCount.setText("Users: " + num);
	}

	public Server server;

	private JFrame frmLionseServer;
	private JTextField commandLineTextField;
	private JButton serverStartButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Launcher window = new Launcher();
					window.frmLionseServer.setVisible(true);

					Logger.initialize();
					window.server = new Server("server.config");
					window.newUserWindow.server = window.server;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Launcher() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		frmLionseServer = new JFrame();
		frmLionseServer.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Launcher.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		frmLionseServer.setResizable(false);
		frmLionseServer.setTitle("Sealion 2.0");
		frmLionseServer.setBounds(100, 100, 600, 500);
		frmLionseServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLionseServer.getContentPane().setLayout(null);

		newUserWindow = new NewUser();

		serverStartButton = new JButton("���� ����");
		serverStartButton.setFont(UIManager.getFont("Label.font"));
		serverStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// ��� ������ ���
				if (server.status == Server.Status.PENDING) {

					server.start();

					serverStartButton.setText("���� ��...");
					serverStartButton.setEnabled(false);

					JOptionPane.showMessageDialog(frmLionseServer, "������ " + server.config.port + "�� ��Ʈ���� ����Ǿ����ϴ�.",
							"���� ����", JOptionPane.INFORMATION_MESSAGE);

				}
			}
		});

		serverStartButton.setBounds(10, 10, 97, 28);
		frmLionseServer.getContentPane().add(serverStartButton);

		JLabel totalConnectionLabel = new JLabel("������ ��: 312");
		totalConnectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalConnectionLabel.setBounds(485, 17, 97, 15);
		frmLionseServer.getContentPane().add(totalConnectionLabel);

		connectionCount = totalConnectionLabel;

		commandLineTextField = new JTextField();
		commandLineTextField.setBounds(10, 440, 459, 21);
		frmLionseServer.getContentPane().add(commandLineTextField);
		commandLineTextField.setColumns(10);

		JButton executeButton = new JButton("��� ����");
		executeButton.setFont(UIManager.getFont("Label.font"));
		executeButton.setBounds(485, 439, 97, 23);
		frmLionseServer.getContentPane().add(executeButton);

		JButton dataUploadButton = new JButton("������ ���ε�");
		dataUploadButton.setFont(UIManager.getFont("Label.font"));
		dataUploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (server.status != Server.Status.RUNNING) {
					JOptionPane.showMessageDialog(frmLionseServer, "������ ���� �����ϼ���.", "���� ����", JOptionPane.ERROR_MESSAGE);
					return;
				}

				server.update();

				JOptionPane.showMessageDialog(frmLionseServer, "�������� ��� ���� ������ �����ͺ��̽��� ���ε��߽��ϴ�.", "������ ���ε�",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		dataUploadButton.setBounds(119, 10, 119, 28);
		frmLionseServer.getContentPane().add(dataUploadButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 48, 572, 382);
		frmLionseServer.getContentPane().add(scrollPane);

		JTextArea outputTextArea = new JTextArea();
		scrollPane.setViewportView(outputTextArea);
		outputTextArea.setFont(UIManager.getFont("Label.font"));
		outputTextArea.setEditable(false);

		frmLionseServer.setTitle("Sealion 2.0 - " + Server.getIP());

		output = outputTextArea;

		JButton addNewUserButton = new JButton("�� ���� �߰�");
		addNewUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (server.status != Server.Status.RUNNING) {
					JOptionPane.showMessageDialog(frmLionseServer, "������ ���� �����ϼ���.", "���� ����", JOptionPane.ERROR_MESSAGE);
					return;
				}

				newUserWindow.setVisible(true);
			}
		});
		addNewUserButton.setBounds(250, 10, 113, 28);
		frmLionseServer.getContentPane().add(addNewUserButton);
	}
}
