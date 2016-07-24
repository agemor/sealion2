package sealion.client.world;

import sealion.client.Display;
import sealion.client.asset.Graphics;
import sealion.client.ui.Style;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TalkBalloon implements Renderable {

	public static final int DEFAULT_REMAINING_TIME = 7;

	public static Sprite balloon;
	public static TextureRegion balloon_knob;

	/**
	 * ��ǳ���� ��� ĳ����
	 */
	public Character character;

	/**
	 * �޽���
	 */
	public String message;

	/**
	 * ��ǳ���� ����������� ���� �ð�
	 */
	public float remainingTime;

	public Label label;
	public boolean talking = false;
	public String[] lines = new String[9];
	public int lineCount = 0;

	public float width, height;

	public TalkBalloon(Character character) {
		this.character = character;

		label = new Label("", Style.talkBalloonLabelStyle);

		if (balloon == null) {
			balloon = new Sprite(Graphics.sheets.get("TALK_BALLOON"));
			balloon_knob = Graphics.sheets.get("TALK_BALLOON_KNOB");
		}
	}

	public void show(String text) {

		// ���� ����
		talking = true;
		remainingTime = DEFAULT_REMAINING_TIME;

		// ���� �ʱ�ȭ
		for (int j = 0; j < 9; j++)
			lines[j] = "";

		// �޽����� �ʹ� ��� �ڸ���.
		if (text.length() >= 9 * 10)
			message = text.substring(0, 9 * 10 - 1);
		else
			message = text;

		// #1 �������� ������.
		int i = 0;
		int k = 0;

		// �� ���ο� 10���ھ� ä�� �ִ´�.
		while (message.length() > i) {
			lines[k] += message.charAt(i++);
			if (i > 10 * (k + 1))
				k++;
		}
		i = 0;

		// #2 �ڸ� ���ο� �°� �󺧿� ���δ�.
		StringBuffer buffer = new StringBuffer();
		for (int j = 0; j < 9; j++) {
			if (lines[j].equals(""))
				break;
			i++;
			buffer.append(lines[j] + "\n");
		}

		for (int j = 0; j < 9 - i; j++) {
			buffer.insert(0, '\n');
		}

		lineCount = i;

		label.setText(buffer.toString());

		height = i * 20 + 10;
		width = label.getPrefWidth() + 10;

	}

	public void draw(SpriteBatch spriteBatch, float delta) {
		balloon.setPosition(character.position.x - label.getPrefWidth() / 2 + 27 - 5, Display.height
				- character.position.y + 45);
		balloon.setSize(width, height);
		spriteBatch.draw(balloon_knob, character.position.x - 7.5f + 27, Display.height - character.position.y + 16);

		balloon.draw(spriteBatch);

		label.draw(spriteBatch, 1);
	}

	public void update(float delta) {

		remainingTime -= delta;

		if (remainingTime < 0) {
			talking = false;
		}

		label.setPosition(character.position.x - (label.getPrefWidth() / 2) + 27, Display.height - character.position.y
				+ 137);
	}

}
