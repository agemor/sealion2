package sealion.server.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import sealion.server.Launcher;
import sealion.server.Server;
import sealion.server.security.Session;
import sealion.server.security.SessionManager;
import sealion.server.util.Display;
import sealion.server.util.Logger;
import sealion.server.world.Character;
import sealion.server.world.Stage;

public class User extends Thread {

	/**
	 * ���� ����
	 */
	public Entry info;
	public Character character;

	/**
	 * ���� ���� ����
	 */
	public Socket connection;
	public Server server;

	// ����
	public Session session;
	private int signinFailedCount = 0;

	/**
	 * ������ ���� �ִ� ��������
	 */
	public Stage stage;

	/**
	 * ����� ���� ������
	 */
	private BufferedReader reader;
	private BufferedWriter writer;

	/**
	 * ���ο� ���� ������ �����Ѵ�.
	 * 
	 * @param connection
	 * @param config
	 */
	public User(Socket connection, Server server) {
		this.connection = connection;
		this.server = server;

		try {

			// �� ��� ������ �ʱ�ȭ
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), server.config.encoding));
			writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), server.config.encoding));
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ŭ���̾�Ʈ�� ��Ŷ�� �����Ѵ�.
	 * 
	 * @throws IOException
	 */
	public void send(Packet packet) throws IOException {
		writer.write(packet.toString());
		writer.newLine();
		writer.flush();
	}

	public void send(String rawPacket) throws IOException {
		writer.write(rawPacket);
		writer.newLine();
		writer.flush();
	}

	/**
	 * ������ ������ �������� �����ͺ��̽��� ������Ʈ�Ѵ�.
	 */
	public void update() {
		server.database.updateCharacter(character);
	}

	/**
	 * �������� ������ ���´�.
	 */
	public void goodbye() {

		Display.show("�������� ������ �������ϴ�. (" + getIPAddressAndPort() + ")");
		Logger.info("�������� ������ �������ϴ�. (" + getIPAddressAndPort() + ")");

		try {

			// ������ �����Ѵ�.
			SessionManager.expire(session);

			// ������ ������ ���
			if (session.valid) {

				// ĳ���� ������ �����Ѵ�.
				update();

				// ���� �׷� ���� ��� �������� �ۺ� �λ縦 �� �� ����.
				stage.users.farewell(this);
				stage.users.kick(this);

			}

			// ��� ������ �����Ѵ�.
			writer.close();
			reader.close();

			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ������ IP�ּҸ� ����Ѵ�.
	 * 
	 * @return
	 */
	public String getIPAddress() {
		return String.valueOf(connection.getInetAddress());
	}

	/**
	 * ������ IP�ּҿ� ��Ʈ ������ ����Ѵ�.
	 * 
	 * @return
	 */
	public String getIPAddressAndPort() {
		return String.valueOf(connection.getInetAddress()) + ":" + String.valueOf(connection.getPort());
	}

	/**
	 * Ȱ�� ���� ������
	 */
	@Override
	public void run() {

		try {

			// ���� �ִ� ���� ������ ���� �Ѿ�ٸ� ������ �����Ѵ�.
			if (SessionManager.total() >= server.config.maximumConnection) {

				// Ŭ���̾�Ʈ�� �ο��� �� á�ٰ� �˸���.
				send(Packet.get(Packet.Type.ERROR_MAXIMUM_CONNECTION_EXCEEDED));

				// Ŭ���̾�Ʈ���� �ۺ� �λ縦 �Ѵ�.
				goodbye();

				return;
			}

			// �ߺ� ���� ���θ� üũ�Ѵ�.
			if (!server.config.allowDuplicateConnection)
				if (SessionManager.getByAddress(getIPAddress()) != null) {

					// Ŭ���̾�Ʈ�� �ߺ� ������ �Ұ����ϴٰ� �˸���.
					send(Packet.get(Packet.Type.ERROR_DUPLICATE_CONNECTION));

					// Ŭ���̾�Ʈ���� �ۺ� �λ縦 �Ѵ�.
					goodbye();

					return;
				}

			// Ȱ�� ������ �ɴ´�. �ߺ� ������ ������ ��쿡�� ��Ʈ���� ����.
			if (server.config.allowDuplicateConnection)
				session = SessionManager.register(getIPAddressAndPort());
			else
				session = SessionManager.register(getIPAddress());

			Launcher.updateConnectionCount(SessionManager.total());

			// ȯ�� �λ縦 ������.
			send(Packet.get(Packet.Type.GREETINGS));

			// ������ Ȱ��ȭ�Ǿ� �ִ� ���� �ݺ��Ѵ�.
			while (!connection.isClosed()) {

				// Ŭ���̾�Ʈ�� �޽����� �޴´�.
				String rawPacket = reader.readLine();

				// ū ��Ŷ�� �� ���� ������ �׳� ������ ���������.
				if (rawPacket.length() > 1024 * 5)
					goodbye();

				// ��Ŷ�� �� ���ڷ� ������.
				String[] rawPackets = rawPacket.split(Packet.END);

				// ��Ƽ ��Ŷ ó��
				for (String packet : rawPackets)
					handlePacket(packet.trim());
			}
		}

		// ������ �߻����� ���
		catch (Exception e) {
			e.printStackTrace();

			// ������ �� ����
			if (session.valid)
				update();

			goodbye();

		}

		Launcher.updateConnectionCount(SessionManager.total());

	}

	/**
	 * ��Ŷ�� ó���Ѵ�.
	 * 
	 * @param rawPacket
	 * @throws IOException
	 */
	public void handlePacket(String rawPacket) throws IOException {

		String[] data = rawPacket.split(Packet.SPLITTER);

		Logger.info(rawPacket + "(" + getIPAddressAndPort() + ")");

		switch (rawPacket.charAt(0)) {

		// �α���: ���� ��ȿ���� �޴� ����
		case Packet.Header.SIGNIN:

			// �α��ο� �����ߴٸ� ���� �޽����� ������.
			if (!server.database.signin(data[1].trim(), data[2].trim())) {
				send(Packet.get(Packet.Type.SIGNIN_FAILED));
				signinFailedCount++;

				// 10ȸ �̻� Ʋ���� �¹���
				if (signinFailedCount > 10)
					goodbye();

			} else {
				// ������ ��ȿȭ�Ѵ�.
				send(Packet.get(Packet.Type.SIGNIN_SUCCESSFUL));
				session.valid = true;

				// ���� ������ DB���� ����Ѵ�.
				info = server.database.getUserEntry(data[1].trim());
				character = server.database.getCharacter(info.character);

				// ĳ������ �������� ������ �������� �������� ���������� ����Ѵ�.
				if (server.stages.length > character.location.stage)
					stage = server.stages[character.location.stage];
				else
					stage = server.stages[0];

			}

			break;

		// ȸ�� ����
		case Packet.Header.SIGNUP:
			
			// u, user_id, user_password, character_name, user_email

			String userName = data[1].trim();
			String userPassword = data[2].trim();
			String characterName = data[3].trim();
			String userEmail = data[4].trim();

			// ������ �߸��Ǿ��ٸ�
			if (userName.length() < 1 || userPassword.length() < 1 || characterName.length() < 1
					|| userEmail.length() < 6) {
				send(Packet.get(Packet.Type.SIGNUP_FAILED));
				break;
			}

			// ȸ�������� �õ��ϰ�, ������ ���ϵǾ��ٸ� ȸ������ ����
			if (server.database.signup(userName, userPassword, characterName, userEmail))
				send(Packet.get(Packet.Type.SIGNUP_SUCCESSFUL));
			else
				send(Packet.get(Packet.Type.SIGNUP_FAILED));

			break;

		// ���� ���� ���� ���
		case Packet.Header.USER_LIST:

			// ������ ��ȿ���� �ʴٸ� �����Ѵ�.
			if (!session.valid)
				break;

			send(new Packet(Packet.Header.USER_LIST, stage.users.getPacketData()));

			break;

		// ĳ���� ����
		case Packet.Header.CHARACTER:

			// ������ ��ȿ���� �ʴٸ� �����Ѵ�.
			if (!session.valid)
				break;

			send(new Packet(Packet.Header.CHARACTER, character.getPacketData()));

			break;

		// ���� �����鿡�� �λ縦 �ǳٴ�. (�Ű��)
		case Packet.Header.HELLO:

			stage.users.hello(this);
			stage.users.invite(this);

			break;

		// ���� �����鿡�� �ۺ� �λ縦 �Ѵ�.
		case Packet.Header.FAREWELL:

			stage.users.farewell(this);
			stage.users.kick(this);

			break;

		// �ټ����� �޽��� ���� (relay ����)
		case Packet.Header.BROADCAST:

			// ������ ��ȿ���� �ʴٸ� �����Ѵ�.
			if (!session.valid)
				break;

			// �״�� �����Ѵ�.
			stage.users.broadcast(rawPacket + Packet.SPLITTER + character.name + Packet.END, this);

			break;

		// �ټ����� �޽��� ���� (relay ����)
		case Packet.Header.RELAYED_BROADCAST:

			// ������ ��ȿ���� �ʴٸ� �����Ѵ�.
			if (!session.valid)
				break;

			if (rawPacket.length() < 3)
				break;

			// �� ��° �����ش�
			switch (rawPacket.charAt(1)) {

			// dm,direction,x,y,z
			case Packet.Header.MOVE:
				if (true) {
					String[] position = rawPacket.split(Packet.SPLITTER);

					// �ҷ� ��Ŷ�̸� ������.
					if (position.length != 5)
						break;

					character.direction = Integer.parseInt(position[1]);
					character.location.x = Integer.parseInt(position[2]);
					character.location.y = Integer.parseInt(position[3]);
					character.location.z = Integer.parseInt(position[4]);
				}

				// ��Ŷ ���� ����� �߰��Ͽ� ��� �������� �Ѹ���.
				stage.users.broadcast(rawPacket + Packet.SPLITTER + character.name + Packet.END, this);

				break;

			// ds,x,y,z
			case Packet.Header.STOP:

				if (true) {
					String[] position = rawPacket.split(Packet.END)[0].split(Packet.SPLITTER);

					// �ҷ� ��Ŷ�̸� ������.
					if (position.length != 4)
						break;

					character.location.x = Integer.parseInt(position[1]);
					character.location.y = Integer.parseInt(position[2]);
					character.location.z = Integer.parseInt(position[3]);
				}

				// ��Ŷ ���� ����� �߰��Ͽ� ��� �������� �Ѹ���.
				stage.users.broadcast(rawPacket + Packet.SPLITTER + character.name + Packet.END, this);

				break;
			}

			break;

		// �� �� ���� ���
		default:

		}
	}

	/**
	 * ���� ������ ��Ʈ��
	 * 
	 * @author ����
	 * 
	 */
	public static class Entry {

		// ���� �̸�
		public String name;

		// ĳ���� �̸�
		public String character;

		// ���� ����
		public int level = 0;

		// ���� ����
		public boolean banned = false;

		/**
		 * ������ ��Ʈ�� ����
		 * 
		 * @param name
		 * @param character
		 */
		public Entry(String name) {
			this.name = name;
		}

	}

}
