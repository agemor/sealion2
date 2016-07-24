package sealion.server.world;

public class Location {

	/**
	 * ��������
	 */
	public int stage;

	/**
	 * �������� ���� ��ǥ
	 */
	public int x;
	public int y;
	public int z;

	/**
	 * ��ǥ ����
	 * 
	 * @param x
	 * @param y
	 */
	public Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(int stage, int x, int y, int z) {
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
	public Location(String packet) {
		String[] chunk = packet.split(":");

		stage = Integer.parseInt(chunk[0]);
		x = Integer.parseInt(chunk[1]);
		y = Integer.parseInt(chunk[2]);
		z = Integer.parseInt(chunk[3]);
	}

	public String getPacket() {
		return stage + ":" + x + ":" + y + ":" + z;
	}
}