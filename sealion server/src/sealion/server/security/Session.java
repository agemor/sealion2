package sealion.server.security;

public class Session {

	/**
	 * ������ ��� �ּ�
	 */
	public String address;

	/**
	 * ������ ��ȿ��
	 */
	public boolean valid = false;

	public Session(String address) {
		this.address = address;
	}
}
