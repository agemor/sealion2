package sealion.server.world;

import sealion.server.user.Packet;

public class Character {

	// ĳ���� �̸�
	public String name;

	/**
	 * ĳ���� ����
	 */
	public int direction = 0;
	public int level = 0;
	public int experience = 0;
	public int maxHealth = 0;
	public int health = 0;
	public int money = 0;

	// ������ ������ �ʴ� ������
	public String creature;
	public String item;
	public String quest;

	/**
	 * ĳ���� ��ġ
	 */
	public Location location;

	/**
	 * ĳ���� ���
	 */
	public Equipment equipment;

	public Character(String name) {
		this.name = name;

		// ��� �ʱ�ȭ
		equipment = new Equipment();
	}

	/**
	 * ĳ���� ���
	 * 
	 * @author ����
	 * 
	 */
	public static class Equipment {

		public int hat;
		public int clothes;
		public int weapon;

		public Equipment() {

		}
	}

	/**
	 * ĳ������ ��Ŷ ������ ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public String getPacketData() {
		String packet = name //
				+ Packet.SPLITTER + level//
				+ Packet.SPLITTER + experience//
				+ Packet.SPLITTER + maxHealth//
				+ Packet.SPLITTER + health//
				+ Packet.SPLITTER + money//
				+ Packet.SPLITTER + equipment.hat//
				+ Packet.SPLITTER + equipment.clothes//
				+ Packet.SPLITTER + equipment.weapon//
				+ Packet.SPLITTER + location.stage//
				+ Packet.SPLITTER + location.x //
				+ Packet.SPLITTER + location.y//
				+ Packet.SPLITTER + location.z;//

		return packet;
	}
}
