package sealion.client.world;

import java.util.LinkedList;
import java.util.List;

import sealion.client.Display;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.ui.Style;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Character extends Unit {

	/**
	 * ĳ���� �׷����� ���� ��� ���� ����� ��ġ�� ��Ÿ��
	 */
	public static final float RESOLUTION = 1.5f;
	public static final float C_HEAD = 0 * RESOLUTION;
	public static final float C_FACE = 10 * RESOLUTION;
	public static final float C_BODY = 24 * RESOLUTION;
	public static final float C_HAND = 29 * RESOLUTION;
	public static final float C_LEG = 30 * RESOLUTION;

	/**
	 * �ε巯�� ����ȿ���� �ֱ� ���� ���� ��
	 */
	public static final float[] SINE_WAVE = { 0, 0.27f, 0.52f, 0.74f, 0.89f, 0.98f, 0.99f, 0.93f, 0.79f, 0.59f, 0.35f,
			0.08f, -0.19f, -0.45f, -0.67f, -0.85f, -0.96f, -0.99f, -0.95f, -0.84f, -0.66f, -0.43f, -0.17f };

	public static enum Status {
		MOVE, STOP
	}

	/**
	 * ĳ���� �̸�
	 */
	public String name;
	public boolean isMe = false;

	/**
	 * ���� ����
	 */
	public Status status = Status.STOP;

	/**
	 * ĳ���� �Ӽ�
	 */
	public int level = 0;
	public int experience = 0;

	public int speed = 100;

	public int maxHealth = 0;
	public int health = 0;

	public int money = 0;

	/**
	 * �ٴ� ��ǥ
	 */
	public Point bottom;

	/**
	 * ��� ����
	 */
	public int hat = 0;
	public int clothes = 0;
	public int weapon = 0;

	/**
	 * �̵� ���
	 */
	public Path currentPath;
	public List<Path> path;
	public int pathIndex = 0;

	// ------------------------ �׷��� ���� ����

	/**
	 * ĳ���Ϳ� ���� �̵�
	 */
	public TalkBalloon talkBalloon;
	public Label nameTag;

	// �׷���
	private TextureRegion[] texture_head;
	private TextureRegion[] texture_face;
	private TextureRegion[] texture_body;
	private TextureRegion[] texture_hand;
	private TextureRegion[] texture_leg_stop;
	private TextureRegion[] texture_leg_step1;
	private TextureRegion[] texture_leg_step2;
	private TextureRegion[] texture_leg_step3;
	private TextureRegion[] texture_leg_step4;

	// ���� ���� �ִϸ��̼��� ������
	private int step = 0;
	private float stepTime = 0;

	// ����
	private int waveIndex = 0;
	private float p_head;
	private float p_face;
	private float p_body;
	private float p_hand;

	/**
	 * ĳ���� ����
	 */
	public Character() {

		// ĳ���� ��ġ �ʱ�ȭ
		position = new Point(0, 0, 0);
		path = new LinkedList<Path>();

		talkBalloon = new TalkBalloon(this);
		nameTag = new Label("", Style.nameTagLabelStyle);

		loadGraphics();

		bottom = new Point(0, 0, 0);
	}

	public void setName(String name) {

		this.name = name;
		nameTag = new Label(name, Style.nameTagLabelStyle);
	}

	/**
	 * ĳ���� �׷��� �ε�
	 */
	public void loadGraphics() {
		texture_head = Graphics.animations.get("HEAD");
		texture_face = Graphics.animations.get("FACE");
		texture_body = Graphics.animations.get("BODY");
		texture_hand = Graphics.animations.get("HAND");
		texture_leg_stop = Graphics.animations.get("LEG_STAND");
		texture_leg_step1 = Graphics.animations.get("LEG_STEP1");
		texture_leg_step2 = Graphics.animations.get("LEG_STEP2");
		texture_leg_step3 = Graphics.animations.get("LEG_STEP3");
		texture_leg_step4 = Graphics.animations.get("LEG_STEP4");
	}

	/**
	 * ĳ������ ���� ��ǳ���� ����.
	 * 
	 * @param message
	 */
	public void say(String message) {
		talkBalloon.show(message);
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

		if (status == Status.MOVE) {
			switch (step) {
			case 0:
				spriteBatch.draw(texture_leg_step1[direction], position.x, Display.height - (position.y + C_LEG));
				break;
			case 1:
				spriteBatch.draw(texture_leg_step2[direction], position.x, Display.height - (position.y + C_LEG));
				break;
			case 2:
				spriteBatch.draw(texture_leg_stop[direction], position.x, Display.height - (position.y + C_LEG));
				break;
			case 3:
				spriteBatch.draw(texture_leg_step3[direction], position.x, Display.height - (position.y + C_LEG));
				break;
			case 4:
				spriteBatch.draw(texture_leg_step4[direction], position.x, Display.height - (position.y + C_LEG));
				break;
			case 5:
				spriteBatch.draw(texture_leg_step3[direction], position.x, Display.height - (position.y + C_LEG));
				break;
			}
		} else {
			spriteBatch.draw(texture_leg_stop[direction], position.x, Display.height - (position.y + C_LEG));
		}

		// draw body
		spriteBatch.draw(texture_body[direction], position.x, Display.height - (position.y + p_body));

		// draw hands
		spriteBatch.draw(texture_hand[direction], position.x, Display.height - (position.y + p_hand));

		// draw face
		spriteBatch.draw(texture_face[direction], position.x, Display.height - (position.y + p_face));

		// draw head
		spriteBatch.draw(texture_head[direction], position.x, Display.height - (position.y + p_head));

		// draw name tag
		nameTag.draw(spriteBatch, 1);

		// draw talk balloon
		if (talkBalloon.talking)
			talkBalloon.draw(spriteBatch, delta);

	}

	@Override
	public void update(float delta) {

		stepTime += delta;

		// ���� �ε����� �����Ѵ�.
		if (++waveIndex >= SINE_WAVE.length)
			waveIndex = 0;

		// ������ �ð��� ������ ������ �ٲ۴�.
		if (stepTime > 0.05f && status == Status.MOVE) {
			stepTime = 0;
			if (++step > 5)
				step = 0;
		}

		if (isMe && status == Status.MOVE) {
			updatePosition(delta);
		}

		// �̵��� ��ΰ� �ִٸ�
		else if (this.path.size() > 0 && !isMe && this.path.size() > pathIndex) {

			Path path = this.path.get(pathIndex);

			if (path.canceled) {
				pathIndex++;
			} else {

				// ��ġ�� ������Ʈ�Ѵ�.
				this.direction = path.direction;
				if (path.destination == null) {
					updatePosition(delta);
				} else {
					updatePosition(delta);
				}

				// �������� �����ߴ��� üũ�Ѵ�.
				if (path.reached(position)) {

					// ���� ���� �������� �����ߴٸ�
					if (this.path.size() - 1 <= pathIndex) {
						this.path.clear();
						pathIndex = 0;
					}

					// �߰� �������� �����ߴٸ�
					else {
						pathIndex++;
					}
				}

				// �ʹ� �������� ���̰� ���� ���ٸ� �׳� ���� ����
				else if (path.previousDistance > 2000 && path.destination != null) {
					position.x = path.destination.x;
					position.y = path.destination.y;
				}
			}

		}

		// �����±� ��ġ ������Ʈ
		nameTag.setPosition(position.x - (nameTag.getWidth() / 2) + 27, Display.height - position.y + 27);

		// ��ǳ�� ������Ʈ
		if (talkBalloon.talking)
			talkBalloon.update(delta);

		// ��ġ ����
		p_head = C_HEAD + SINE_WAVE[waveIndex] * 2;
		p_face = C_FACE + SINE_WAVE[waveIndex] * 2;
		p_body = C_BODY + SINE_WAVE[waveIndex] * 2;
		p_hand = C_HAND + SINE_WAVE[waveIndex] * 2;

	}

	private void updatePosition(float delta) {
		switch (direction) {
		case 0:
			position.x -= speed * delta;
			position.y += speed * delta / 2;
			break;
		case 1:
			position.x -= speed * delta;
			break;
		case 2:
			position.x -= speed * delta;
			position.y -= speed * delta / 2;
			break;
		case 3:
			position.y -= speed * delta / 2;
			break;
		case 4:
			position.x += speed * delta;
			position.y -= speed * delta / 2;
			break;
		case 5:
			position.x += speed * delta;
			break;
		case 6:
			position.x += speed * delta;
			position.y += speed * delta / 2;
			break;
		case 7:
			position.y += speed * delta / 2;
			break;
		}
	}

	/**
	 * ĳ���͸� �̵��Ѵ�.
	 * 
	 * @param targetDirection
	 */
	public void move(int targetDirection) {
		this.direction = targetDirection;
		Connection.move(targetDirection, position);
	}

	/**
	 * ĳ���͸� �����Ѵ�.
	 */
	public void stop() {
		Connection.stop(position);
	}

	@Override
	public Point getPosition() {
		bottom.x = position.x;
		bottom.y = position.y + 150;
		bottom.z = position.z;
		return bottom;
	}

	/**
	 * ���� ĳ������ ��ġ���� Ư�� ���� �ٶ󺸴� ������ �����Ѵ�.
	 * 
	 * @param point
	 * @return
	 */
	public int getDirection(Point destination) {

		int xdet = 0; // -1-����, 0-���� ����, 1-������
		int ydet = 0; // -1-�Ʒ���, 0-���� ����, 1-����

		if (destination.x - position.x > 0) // �����ʿ� �ִ�.
			xdet = 1;
		else if (destination.x - position.x < 0)
			xdet = -1;// ���ʿ� �ִ�.

		if (destination.y - position.y > 0) // ���ʿ� �ִ�.
			ydet = -1;
		else if (destination.y - position.y < 0) // �Ʒ��ʿ� �ִ�.
			ydet = 1;

		if (xdet > 0) { // ������
			if (ydet > 0) // ������ ��
				return 4;
			else if (ydet == 0) // �׳� ������
				return 5;
			else
				return 6; // ������ �Ʒ�

		} else if (xdet == 0) { // y�� ������
			if (ydet > 0) // ��
				return 3;
			else if (ydet == 0) // �� ������ (����Ʈ ��ũ!)
				return -1;
			else
				return 7;// �Ʒ�

		} else { // ����
			if (ydet > 0) // ���� ��
				return 2;
			else if (ydet == 0) // �׳� ����
				return 1;
			else
				return 0;// ���� �Ʒ�
		}
	}

}
