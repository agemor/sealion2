package sealion.client.world;

/**
 * 
 * ĳ������ �̵� ������ ��Ÿ��. �� �������� ĳ������ ���ÿ� ����
 * 
 * @author ������
 * 
 */
public class Path {

	/**
	 * �������� ������ ������ ���ͷ��̴�.
	 */
	public int direction;
	public Point destination;
	public boolean canceled = false;

	public float velocityX;
	public float velocityY;

	public double previousDistance = 100000000;

	/**
	 * ��θ� �����Ѵ�.
	 * 
	 * @param direction
	 */
	public Path(int direction) {
		this.direction = direction;
		this.destination = null;
	}

	public Path(int direction, Point destination) {
		this.direction = direction;
		this.destination = destination;
	}

	/**
	 * ���� ������ �����ߴ��� Ȯ���Ѵ�.
	 * 
	 * @param location
	 * @return
	 */
	public boolean reached(Point location) {
		if (destination == null)
			return false;

		double distance = destination.getDistance(location);

		// ���� ��������ų� ��� �ּ� ������ ���� ��쿡 �ش��Ѵ�.
		if (distance < 4 || distance > previousDistance) {
			return true;
		}

		previousDistance = distance;

		return false;

	}

	public void cancel() {
		canceled = true;
	}
}