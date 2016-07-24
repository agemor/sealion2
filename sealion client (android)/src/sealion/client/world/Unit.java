package sealion.client.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Unit implements Renderable {

	public static enum Type {
		NPC, MOB, OBJECT
	}

	/**
	 * ��ġ, ����
	 */
	public Point position;
	public int direction = 0;

	/**
	 * Ÿ��
	 */
	public Type type = Type.OBJECT;

	/**
	 * �ؽ���
	 */
	public TextureRegion[] texture;

	public Unit(Type type, TextureRegion[] graphic) {
		this.type = type;
		this.texture = graphic;
		position = new Point(0, 0, 0);
	}

	public Unit() {
		position = new Point(0, 0, 0);
	}

	/**
	 * ������ ��ġ�� �����Ѵ�.
	 * 
	 * @return
	 */
	public Point getPosition() {
		return position;
	}

}
