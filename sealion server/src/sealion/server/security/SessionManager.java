package sealion.server.security;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

	public static Map<String, Session> sessions = new HashMap<String, Session>();

	/**
	 * IP�ּҷ� ������ �˻��Ͽ� ��ȯ�Ѵ�.
	 * 
	 * @param IPAndPort
	 * @return
	 */
	public static Session getByAddress(String address) {
		return sessions.get(address);
	}

	/**
	 * �� Ȱ�� ���� ���� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public static int total() {
		return sessions.size();
	}

	/**
	 * ������ �ı��Ѵ�.
	 * 
	 * @param session
	 */
	public static void expire(Session session) {
		sessions.remove(session.address);
	}

	/**
	 * ������ ����Ѵ�.
	 * 
	 * @param address
	 * @return
	 */
	public static Session register(String address) {

		Session session = new Session(address);
		sessions.put(address, session);

		return session;
	}
}
