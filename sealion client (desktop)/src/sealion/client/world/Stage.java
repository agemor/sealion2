package sealion.client.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sealion.client.Display;
import sealion.client.asset.Graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Stage implements Renderable {

	/**
	 * ������ ���� ��
	 */
	public static final float SCALE_Z = (float) Math.sqrt(2);
	public Camera camera;
	public Point smoothCamera;

	/**
	 * �������� ũ�� (�׻� ���簢�� ��)
	 */
	public int size;

	/**
	 * �������� ���� ���
	 */
	public List<Terrain> terrain;
	public List<Character> characters;
	public List<Unit> units;

	/**
	 * ������ ĳ����
	 */
	public Character me;

	/**
	 * Z��ǥ ����
	 */
	private Comparator<Unit> unitComparator;

	/**
	 * �������� ����
	 */
	public Stage() {

		// ī�޶� �ʱ�ȭ
		camera = new OrthographicCamera(Display.width, Display.height);
		camera.position.set(Display.width / 2, Display.height / 2, 0.4f);
		smoothCamera = new Point(Display.width / 2, Display.height / 2, 0);

		// ���̾� �ʱ�ȭ
		terrain = new ArrayList<Terrain>();
		characters = new ArrayList<Character>();
		units = new ArrayList<Unit>();

		// ������
		unitComparator = new UnitComparator();
	}

	/**
	 * ���������� �ε��Ѵ�.
	 * 
	 * @param entry
	 */
	public void load(Entry entry) {

		// ������ ���
		int startingX = Terrain.WIDTH / 2 * entry.getSize();
		int startingY = Terrain.HEIGHT / 2;

		for (int i = 0; i < entry.getSize(); i++) {

			// ���� ������ ������Ʈ
			startingX -= Terrain.WIDTH / 2;
			startingY += Terrain.HEIGHT / 2;

			for (int j = 0; j < entry.getSize(); j++) {

				// ��ǥ ����
				int x = startingX + (Terrain.WIDTH / 2 * j);
				int y = startingY + (Terrain.HEIGHT / 2 * j);

				// ���� ����
				Terrain grid = new Terrain(x, y, entry.terrainAltitude[i][j], new Terrain.Index(j, i), null);

				// ���� �׷��� ���
				if (entry.terrainType[i][j] == 0) {
					grid.setTexture(Graphics.animations.get("TERRAIN")[0]);
				}

				// ���� �߰�
				terrain.add(grid);
			}
		}
	}

	public void load() {
		Entry entry = new Entry();
		entry.generateRandom(20);

		load(entry);
	}

	/**
	 * ��ǥ�� ���� Ÿ�� ���ϱ�
	 * 
	 * @param point
	 * @return
	 */
	public Terrain getGrid(Point point) {
		float dx = -(terrain.get(0).x - (Terrain.WIDTH / 2));
		float dy = -terrain.get(0).y;

		float px = Terrain.RADIAN_45 * (point.x + (2 * point.y) + dx + (2 * dy));
		float py = Terrain.RADIAN_45 * (-point.x + (2 * point.y) + (-dx) + (2 * dy)) + (90 * 1.5f);

		float gridSize = Terrain.HEIGHT / Terrain.RADIAN_45;
		int tx = (int) Math.floor(px / gridSize);
		int ty = (int) Math.floor(py / gridSize);

		return terrain.get((int) (ty * Math.sqrt(terrain.size()) + tx));
	}

	/**
	 * �������� ������
	 */
	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

		// ī�޶� ������Ʈ
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		// ���� �׸���
		for (int i = 0; i < terrain.size(); i++) {
			Terrain piece = terrain.get(i);
			spriteBatch.draw(piece.getTexture(), piece.x, Display.height - piece.y + piece.z * SCALE_Z);
		}

		// ��� ������ ������ ��ü�� Z�� �����Ѵ�.
		Collections.sort(units, unitComparator);

		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(spriteBatch, delta);
		}

	}

	@Override
	public void update(float delta) {

		// ������ ����Ű���� ��ǥ�� �����Ѵ�.
		smoothCamera.x += (0.05f * (me.position.x - smoothCamera.x));
		smoothCamera.y += (0.05f * ((Display.height - me.position.y) - smoothCamera.y));

		camera.position.x = Math.round(smoothCamera.x);
		camera.position.y = Math.round(smoothCamera.y);

		// ��� ĳ���͸� ������Ʈ�Ѵ�.
		for (int i = 0; i < characters.size(); i++) {
			characters.get(i).update(delta);
		}
/*
		// ��� ������ ������Ʈ�Ѵ�.
		for (int i = 0; i < units.size(); i++) {
			units.get(i).update(delta);
		}*/
	}

	public class UnitComparator implements Comparator<Unit> {		
		@Override
		public int compare(Unit r1, Unit r2) {
			return (int) (r1.getPosition().y - r2.getPosition().y);
		}

	}

	/**
	 * ���������� ���� ������ ����
	 * 
	 * @author ����
	 * 
	 */
	public static class Entry {

		// ����
		public int[][] terrainType;
		public int[][] terrainAltitude;

		// ����
		public int[] unit;

		public Entry() {

		}

		public Entry(int size) {
			generateRandom(size);
		}

		public int getSize() {
			return terrainType.length;
		}

		/**
		 * �������� ���������� �����.
		 * 
		 * @param size
		 */
		public void generateRandom(int size) {

			terrainType = new int[size][size];
			terrainAltitude = new int[size][size];
			unit = new int[0];

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					terrainType[i][j] = 0;
					terrainAltitude[i][j] = 0;
				}
			}
		}
	}

}
