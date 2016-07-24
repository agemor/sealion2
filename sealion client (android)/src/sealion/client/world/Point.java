package sealion.client.world;

import sealion.client.net.Packet;

public class Point {

	/**
	 * ��������
	 */
	public int stage;

	/**
	 * �������� ���� ��ǥ
	 */
	public float x;
	public float y;
	public float z;

	/**
	 * ��ǥ ����
	 * 
	 * @param x
	 * @param y
	 */
	public Point(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point(int stage, float x, float y, float z) {
		this.stage = stage;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * �Ľ� ���ڿ��� ��ǥ ����
	 * 
	 * @param packet
	 */
	public Point(String packet) {
		String[] chunk = packet.split(":");

		stage = Integer.parseInt(chunk[0]);
		x = Float.parseFloat(chunk[1]);
		y = Float.parseFloat(chunk[2]);
		z = Float.parseFloat(chunk[3]);
	}

	/**
	 * �� �� ���� �Ÿ� ���ϱ�
	 * 
	 * @param location
	 * @return
	 */
	public double getDistance(Point location) {
		return Math.sqrt(Math.pow(x - location.x, 2) + Math.pow(y - location.y, 2));

	}

	public String getPacket() {
		return String.valueOf((int) x) + Packet.SPLITTER + String.valueOf((int) y) + Packet.SPLITTER
				+ String.valueOf((int) z);
	}

}
