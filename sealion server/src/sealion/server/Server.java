package sealion.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import sealion.server.db.Database;
import sealion.server.security.SessionManager;
import sealion.server.user.User;
import sealion.server.util.Display;
import sealion.server.util.Logger;
import sealion.server.world.Stage;

public class Server {

	public static enum Status {
		PENDING, RUNNING, CLOSED
	}

	public Server self = this;

	// ���� ����
	public ServerSocket server;
	public Receiver receiver;

	// ���� ����
	public Configuration config;

	// ���� ����
	public Status status = Status.PENDING;

	// �����ͺ��̽� ����
	public Database database;

	// ��������
	public Stage[] stages;

	/**
	 * ���� ����
	 * 
	 * @param name
	 * @param port
	 */
	public Server(String configPath) {

		Launcher.updateConnectionCount(SessionManager.total());

		// ���� �ε�
		config = Configuration.load(configPath);

		Display.raw(config.toString());

		// �������� �ε�
		stages = new Stage[1];

		for (int i = 0; i < stages.length; i++)
			stages[i] = new Stage();
	}

	/**
	 * ���� ����
	 */
	public void start() {

		// �̹� ������ ���� ���� ��� ��� ����
		if (status == Status.RUNNING)
			return;

		try {

			// �����ͺ��̽� ��� �G ����
			database = Database.getConnection(config.dbHost, new Database.Account(config.dbUser, config.dbPassword));
			if (database.connect())
				Display.show("�����ͺ��̽� ������ ����Ǿ����ϴ�.");
			else
				return;

			// ���� �ʱ�ȭ
			server = new ServerSocket(config.port);
			receiver = new Receiver();

			// ���� ����
			receiver.start();

			status = Status.RUNNING;

			Display.show("��� �غ� ���ƽ��ϴ�.");
			Logger.info("��� �غ� ���ƽ��ϴ�.");

		} catch (IOException e) {
			e.printStackTrace();

			Display.error("������ ������ �� �����ϴ�.");
			Display.raw(e.getMessage());

			Logger.error("cannot start server.");
			Logger.error("������ ������ �� �����ϴ�.");
		}
	}

	/**
	 * ���� �ߴ�
	 */
	public void stop() {

	}

	/**
	 * ��� Ȱ�� ���� ���� ����
	 */
	public void update() {
		for (Stage stage : stages)
			stage.users.update();

		Display.show("�����ͺ��̽��� ���� ������ ������Ʈ�߽��ϴ�.");
		Logger.info("�����ͺ��̽��� ���� ������ ������Ʈ�߽��ϴ�.");
	}

	/**
	 * IP�ּ� ���
	 * 
	 * @return
	 */
	public static String getIP() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Ŭ���̾�Ʈ ������ �޴� ������
	 * 
	 * @author ����
	 * 
	 */
	public class Receiver extends Thread {

		@Override
		public void run() {

			// ������ ����ִ� ������ ��� �޴´�.
			while (!server.isClosed()) {

				try {

					// Ŭ���̾�Ʈ ������ �޴´�.
					Socket client = server.accept();

					// �������� ����� �����Ѵ�.
					User user = new User(client, self);
					user.start();

					Display.show("���ο� ������ ����������ϴ�. (" + user.getIPAddressAndPort() + ")");
					Logger.info("���ο� ������ ����������ϴ�. (" + user.getIPAddressAndPort() + ")");
				}

				// Ŭ���̾�Ʈ ���ӿ� ������ ���� ���
				catch (IOException e) {
					e.printStackTrace();

					Display.error("Ŭ���̾�Ʈ ������ ���� �� �����ϴ�.");
					Display.raw(e.getMessage());

					Logger.error("Ŭ���̾�Ʈ ������ ���� �� �����ϴ�.");
					Logger.error(e.getMessage());
				}

			}
		}
	}
}
