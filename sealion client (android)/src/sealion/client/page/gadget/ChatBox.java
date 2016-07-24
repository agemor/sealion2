package sealion.client.page.gadget;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sealion.client.Display;
import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * 
 * ä�� �޽��� �Է� �ڽ�
 * 
 * @author ������
 * 
 */
public class ChatBox extends WidgetGroup implements Gadget {

	/**
	 * UI ��Ÿ��
	 */
	public LabelStyle labelStyle;

	/**
	 * UI ������Ʈ
	 */
	public Image chatbox_background;
	public Label chatbox_label;

	/**
	 * �޽��� ť
	 */
	public Queue<String> messageQueue;

	public ChatBox() {

		loadStyles();
		initializeUI();

		// �޽��� ť �ʱ�ȭ
		messageQueue = new LinkedList<String>();
		for (int i = 0; i < 9; i++) {
			messageQueue.add("");
		}

	}

	/**
	 * UI ��Ÿ�� �ε�
	 */
	private void loadStyles() {
		labelStyle = new LabelStyle();
		labelStyle.font = Fonts.get(Fonts.Font.NANUM_GOTHIC);
		labelStyle.fontColor = Color.BLACK;
	}

	/**
	 * UI �ʱ�ȭ
	 */
	private void initializeUI() {

		// ��� ���ϱ�
		chatbox_background = new Image(Graphics.sheets.get("CHAT_BOX"));

		// UI �ʱ�ȭ
		chatbox_label = new Label("", labelStyle);

		// UI ����
		chatbox_label.setSize(300, 200);

		chatbox_background.setPosition(0, Display.height * Display.scale - 200);

		chatbox_label.setPosition(chatbox_background.getX() + 20, chatbox_background.getY());

		addActor(chatbox_background);
		addActor(chatbox_label);

	}

	/**
	 * �޽����� ���� ����.
	 * 
	 * @param message
	 */
	public void addMessage(String message) {

		String trail = message;
		// �޽����� ��� ���� �ٷ� ������ ����Ѵ�.
		int count = (int) Math.floor(message.length() / 20);

		if (count > 0) {
			for (int i = 0; i < count; i++) {
				messageQueue.add(trail.substring(i * 20, (i + 1) * 20));
				if (messageQueue.size() > 9)
					messageQueue.poll();
			}
			trail = trail.substring(count * 20, trail.length());
		}
		messageQueue.add(trail);
		if (messageQueue.size() > 9)
			messageQueue.poll();

		StringBuffer buffer = new StringBuffer();
		Iterator<String> iterator = messageQueue.iterator();

		while (iterator.hasNext()) {
			buffer.append(iterator.next() + "\n");
		}

		chatbox_label.setText(buffer.toString());
	}

}
