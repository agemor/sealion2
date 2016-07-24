package sealion.client.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import sealion.client.net.crypto.SHA256;
import sealion.client.world.Path;
import sealion.client.world.Point;

import com.badlogic.gdx.Gdx;

public class Connection {

	public static final String ENCODING = "utf-8";

	/**
	 * ���� ���� ����
	 */
	public static String host;
	public static int port;
	public static boolean connected = false;

	/**
	 * ���� ����
	 */
	protected static Socket socket;
	protected static BufferedReader reader;
	protected static BufferedWriter writer;

	/**
	 * ������ ���ù�
	 */
	private static Thread receiver;

	/**
	 * �̺�Ʈ ������ ���
	 */
	private static List<SigninEvent> signinEventListeners;
	private static List<SignupEvent> signupEventListeners;
	private static List<ErrorEvent> errorEventListeners;
	private static List<UserEvent> userEventListeners;
	private static List<NoticeEvent> noticeEventListeners;

	/**
	 * ���� ����Ʈ
	 */
	private static List<User> users;

	/**
	 * �� ����
	 */
	public static User me;

	/**
	 * ������ �ʱ�ȭ�Ѵ�.
	 */
	public static void initialize() {
		users = new ArrayList<User>();
		me = new User();
		me.character.isMe = true;

		signinEventListeners = new ArrayList<SigninEvent>();
		signupEventListeners = new ArrayList<SignupEvent>();
		errorEventListeners = new ArrayList<ErrorEvent>();
		userEventListeners = new ArrayList<UserEvent>();
		noticeEventListeners = new ArrayList<NoticeEvent>();

	}

	/**
	 * ������ �����Ѵ�.
	 * 
	 * @param host
	 * @param port
	 */
	public static void connect(String host, int port) {
		Connection.host = host;
		Connection.port = port;

		try {

			// ������ �����Ѵ�.
			socket = new Socket();

			// ������ ȣ��Ʈ�� �����Ѵ�.
			socket.connect(new InetSocketAddress(host, port));

			if (socket.isConnected())
				connected = true;

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), ENCODING));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), ENCODING));

			// ������ ���ù� ����
			receiver = new Receiver();
			receiver.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������ ��Ŷ�� �����Ѵ�.
	 * 
	 * @param packet
	 */
	public static void send(Packet packet) {
		if (!connected)
			return;
		try {

			writer.write(packet.header + Packet.SPLITTER + packet.data + Packet.END);
			writer.newLine();
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void send(String rawPacket) {
		if (!connected)
			return;
		try {

			writer.write(rawPacket + Packet.END);
			writer.newLine();
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �α���
	 * 
	 * @param id
	 * @param password
	 */
	public static void signin(String id, String password) {
		send(new Packet(Packet.Header.SIGNIN, id + Packet.SPLITTER + SHA256.digest(password)));

	}

	/**
	 * ȸ�� ����
	 * 
	 * @param id
	 * @param password
	 * @param character
	 * @param email
	 */
	public static void signup(String id, String password, String character, String email) {
		send(new Packet(Packet.Header.SIGNUP, id + Packet.SPLITTER + SHA256.digest(password) + Packet.SPLITTER
				+ character + Packet.SPLITTER + email));
	}

	/**
	 * ĳ���� �̵�
	 * 
	 * @param direction
	 * @param position
	 */
	public static void move(int direction, Point position) {
		send(String.valueOf(Packet.Header.RELAYED_BROADCAST) + String.valueOf(Packet.Header.MOVE) + Packet.SPLITTER
				+ direction + Packet.SPLITTER + position.getPacket());

	}

	/**
	 * ĳ���� ����
	 * 
	 * @param position
	 */
	public static void stop(Point position) {
		send(String.valueOf(Packet.Header.RELAYED_BROADCAST) + String.valueOf(Packet.Header.STOP) + Packet.SPLITTER
				+ position.getPacket());
	}

	public static void chat(String message) {
		Connection.send(String.valueOf(Packet.Header.BROADCAST) + String.valueOf(Packet.Header.CHAT) + Packet.SPLITTER
				+ message);
	}

	/**
	 * �̺�Ʈ �����ʸ� �߰��Ѵ�.
	 * 
	 * @param event
	 */
	public static void addEventListener(ConnectionEvent event) {

		// �̺�Ʈ�� ���� Ÿ�Կ� ���� �ٸ� �����̳ʿ� �߰��Ѵ�.
		if (event instanceof SigninEvent)
			signinEventListeners.add((SigninEvent) event);

		else if (event instanceof SignupEvent)
			signupEventListeners.add((SignupEvent) event);

		else if (event instanceof ErrorEvent)
			errorEventListeners.add((ErrorEvent) event);

		else if (event instanceof UserEvent)
			userEventListeners.add((UserEvent) event);

		else if (event instanceof NoticeEvent)
			noticeEventListeners.add((NoticeEvent) event);
	}

	/**
	 * �̺�Ʈ �����ʸ� �����Ѵ�.
	 * 
	 * @param event
	 */
	public static void removeEventListener(ConnectionEvent event) {
		if (event instanceof SigninEvent)
			signinEventListeners.remove((SigninEvent) event);

		else if (event instanceof SignupEvent)
			signupEventListeners.remove((SignupEvent) event);

		else if (event instanceof ErrorEvent)
			errorEventListeners.remove((ErrorEvent) event);

		else if (event instanceof UserEvent)
			userEventListeners.remove((UserEvent) event);

		else if (event instanceof NoticeEvent)
			noticeEventListeners.remove((NoticeEvent) event);
	}

	/**
	 * �������� �� �����͸� ����Ѵ�.
	 * 
	 * @author ����
	 * 
	 */
	private static class Receiver extends Thread {

		@Override
		public void run() {

			// ������ ����Ǿ� �ִ� ���� ����
			while (socket.isConnected()) {

				try {

					// ������ ���
					String rawData = reader.readLine();

					// ���� ��Ŷ���� ������
					String[] rawPackets = rawData.split(Packet.END);

					// ������ ��Ŷ�� ó���Ѵ�.
					for (String rawPacket : rawPackets)
						handlePacket(rawPacket.trim());
				}

				// ������ ó�� �� ������ �߻��ߴٸ�
				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}

		/**
		 * ���� ��Ŷ�� ó���Ѵ�.
		 * 
		 * @param rawPacket
		 */
		private void handlePacket(String rawPacket) {

			// �� ��Ŷ�� ������.
			if (rawPacket.length() < 1)
				return;

			// �и� ���ڷ� ������.
			String[] data = null;

			// ��Ŷ �ش��� �������� �ɰ���.
			switch (rawPacket.charAt(0)) {

			// �α��� ���
			case Packet.Header.SIGNIN:

				data = rawPacket.split(Packet.SPLITTER);

				// �α��� ���� ����
				boolean signinSucceed = Integer.parseInt(data[1]) == 1;

				for (SigninEvent listener : signinEventListeners)
					listener.onSignin(signinSucceed);

				break;

			// ȸ������ ���
			case Packet.Header.SIGNUP:

				data = rawPacket.split(Packet.SPLITTER);

				// ȸ������ ���� ����
				boolean signupSucceed = Integer.parseInt(data[1]) == 1;

				for (SignupEvent listener : signupEventListeners)
					listener.onSignup(signupSucceed);

				break;

			// ����
			case Packet.Header.ERROR:

				data = rawPacket.split(Packet.SPLITTER);

				// ���� �ڵ�
				int errorCode = Integer.parseInt(data[1]);

				for (ErrorEvent listener : errorEventListeners)
					listener.onError(errorCode);

				break;

			// ��ε�ĳ��Ʈ
			case Packet.Header.BROADCAST:
				
				String broadcastData = rawPacket.substring(2);
				
				if (rawPacket.length() < 2)
					for (UserEvent listener : userEventListeners)
						listener.onBroadcast(broadcastData);
				else {

					// ��ε�ĳ���� ������ �м��Ѵ�.
					switch (rawPacket.charAt(1)) {

					// ä�� �޽��� ����
					case Packet.Header.CHAT:

						if (true) {
							Gdx.app.log(rawPacket, rawPacket.charAt(1)+"");
							// ���� ���ڷ� ����
							String[] packet = rawPacket.substring(3).split(Packet.SPLITTER);

							if (packet.length < 2)
								break;

							// ������������ ������ ã�´�.
							User targetUser = null;
							for (User user : users) {
								if (user.character.name.equals(packet[1])) {
									targetUser = user;
									break;
								}
							}

							for (UserEvent listener : userEventListeners)
								listener.onChatMessageArrived(targetUser, packet[0]);

						}

						break;
					default:
					}

				}

				break;

			// ĳ���� ���� �ε�
			case Packet.Header.CHARACTER:

				data = rawPacket.substring(2).split(Packet.SPLITTER);

				// ĳ���� ������ ������Ʈ�Ѵ�
				me.loadCharacterPacket(data);

				for (UserEvent listener : userEventListeners)
					listener.onCharacterLoaded(me);

				break;

			// ���� �޽���
			case Packet.Header.NOTICE:

				data = rawPacket.split(Packet.SPLITTER);

				String noticeMessage = data[1].trim();

				for (NoticeEvent listener : noticeEventListeners)
					listener.onNotice(noticeMessage);

				break;

			// ���� ����
			case Packet.Header.FAREWELL:

				data = rawPacket.split(Packet.SPLITTER);

				// ������ ������ �̸��� ����Ѵ�.
				String leavingUserName = data[1].trim();
				User leavingUser = null;

				// ������ ��Ͽ��� ã�� ��ο��� �����Ѵ�.
				for (User user : users) {
					if (user.character.name.equals(leavingUserName)) {
						for (UserEvent listener : userEventListeners)
							listener.onLeave(user);
						leavingUser = user;
						break;
					}
				}

				users.remove(leavingUser);

				break;

			// ���� ����
			case Packet.Header.HELLO:

				data = rawPacket.substring(2).split(Packet.SPLITTER);

				// ������ ������ ���� �ν��Ͻ��� �����.
				User arrivingUser = new User();

				// ĳ���� ������ ������Ʈ�Ѵ�
				arrivingUser.loadCharacterPacket(data);

				// ���� ��Ͽ� �߰��Ѵ�.
				users.add(arrivingUser);

				// ����ġ
				for (UserEvent listener : userEventListeners)
					listener.onArrive(arrivingUser);

				break;

			// ���� ���
			case Packet.Header.USER_LIST:

				// �ش��� ������ �κ��� �Ӽ� �и� ���ڷ� ������.
				String[] userPackets = rawPacket.substring(2).split(Packet.PROPERTY);

				// ���� ��� �ʱ�ȭ
				users.clear();

				// ����Ʈ�� �߰�
				for (String userPacket : userPackets) {
					if (userPacket.trim().length() < 1)
						continue;

					System.out.println(userPacket);

					User user = new User();
					user.loadCharacterPacket(userPacket.split(Packet.SPLITTER));
					users.add(user);
				}

				// ����ġ
				for (UserEvent listener : userEventListeners)
					listener.onUserListLoaded(users);

				break;

			// ������ ��ģ ��ε�ĳ��Ʈ
			case Packet.Header.RELAYED_BROADCAST:

				data = rawPacket.substring(3).split(Packet.SPLITTER);

				switch (rawPacket.charAt(1)) {

				// ĳ���� �̵�
				case Packet.Header.MOVE:
					if (true) {

						int direction = Integer.parseInt(data[0]);
						int x = Integer.parseInt(data[1]);
						int y = Integer.parseInt(data[2]);
						int z = Integer.parseInt(data[3]);
						String userName = data[4].trim();

						Point newPoint = new Point(x, y, z);

						User user = null;

						for (User one : users)
							if (one.character.name.equals(userName)) {
								user = one;
								break;
							}

						// ���� ���̰ų� ���� ���� ���� �̵� ����� ��Ȱ��ȭ��Ų��.
						if (user.character.currentPath != null)
							user.character.currentPath.cancel();

						// �Ѱ谪 üũ (�̵� - ���� ������� Ȯ��)
						if (user.character.position.getDistance(newPoint) > 3) {

							// ���ο� �н� ����
							Path path = new Path(user.character.getDirection(newPoint), newPoint);
							user.character.path.add(path);
						}

						// ĳ������ ���ÿ� �̵� ��� �߰�
						Path move = new Path(direction);
						user.character.path.add(move);

						user.character.currentPath = move;

						// ����ġ
						for (UserEvent listener : userEventListeners)
							listener.onMove(user, direction);

					}
					break;

				// ĳ���� ����
				case Packet.Header.STOP:
					if (true) {

						int x = Integer.parseInt(data[0]);
						int y = Integer.parseInt(data[1]);
						int z = Integer.parseInt(data[2]);
						String userName = data[3].trim();

						Point newPoint = new Point(x, y, z);

						User user = null;

						for (User one : users)
							if (one.character.name.equals(userName)) {
								user = one;
								break;
							}

						// �������� �̵� ����� ��� ���
						user.character.currentPath.cancel();

						// ���� ��ġ�� �ʹ� ���̰� ���� ���� �ùٸ� ��ġ�� �̵��Ѵ�.
						if (user.character.position.getDistance(newPoint) > 3) {

							// ���ο� �н� ����
							Path path = new Path(user.character.getDirection(newPoint), newPoint);
							user.character.path.add(path);
						}

						// ����ġ
						for (UserEvent listener : userEventListeners)
							listener.onStop(user);

					}

					break;
				}

				break;

			}
		}
	}
}
