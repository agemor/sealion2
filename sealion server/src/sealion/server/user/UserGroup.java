package sealion.server.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserGroup {

	public List<User> users;

	public UserGroup() {
		users = new ArrayList<User>();
	}

	/**
	 * �׷� ���� ��� �������� ��Ŷ�� �߼��Ѵ�.
	 * 
	 * @param packet
	 * @throws IOException
	 */
	public void broadcast(Packet packet, User exception) throws IOException {
		for (User user : users)
			if (user != exception)
				user.send(packet);
	}

	public void broadcast(String rawPacket, User exception) throws IOException {
		for (User user : users)
			if (user != exception)
				user.send(rawPacket);
	}

	/**
	 * �׷� ���� ��� �������� Ư�� ������ �������� �˸���.
	 * 
	 * @param user
	 * @throws IOException
	 */
	public void farewell(User user) throws IOException {
		broadcast(new Packet(Packet.Header.FAREWELL, user.character.name), user);
	}

	/**
	 * �׷� ���� ��� �������� �ڽ��� ������ �Ű��Ѵ�.
	 * 
	 * @param user
	 * @throws IOException
	 */
	public void hello(User user) throws IOException {
		broadcast(new Packet(Packet.Header.HELLO, user.character.getPacketData()), user);
	}

	public void update() {
		for (User user : users)
			user.update();
	}

	/**
	 * �׷쿡�� Ư�� ������ �����Ѵ�.
	 * 
	 * @param user
	 */
	public void kick(User user) {
		users.remove(user);
	}

	/**
	 * �׷쿡 ������ �߰��Ѵ�.
	 * 
	 * @param user
	 */
	public void invite(User user) {
		users.add(user);
	}

	/**
	 * �׷��� ���� ����� ��Ŷ���� �����Ͽ� �����Ѵ�.
	 * 
	 * @return
	 */
	public String getPacketData() {

		if(users.size() < 1)
			return "";
		
		StringBuffer packet = new StringBuffer();

		for (User user : users) {
			packet.append(user.character.getPacketData());
			packet.append(Packet.PROPERTY);
		}

		// �� �������� ������Ƽ ���ڸ� �����Ѵ�.
		packet.deleteCharAt(packet.length() - 1);

		return packet.toString();
	}
}
