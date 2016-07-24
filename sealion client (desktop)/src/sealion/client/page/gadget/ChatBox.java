package sealion.client.page.gadget;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sealion.client.Display;
import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
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
	public TextFieldStyle textFieldStyle;

	/**
	 * UI ������Ʈ
	 */
	public Image chatbox_background;
	public Image inputbox_background;
	public Label chatbox_label;
	public TextField inputbox_textfield;

	/**
	 * ����
	 */
	public boolean opened = false;

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

		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = Fonts.get(Fonts.Font.CLEAR_GOTHIC);
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.disabledFontColor = Color.WHITE;
	}

	/**
	 * UI �ʱ�ȭ
	 */
	private void initializeUI() {

		// ��� ���ϱ�
		chatbox_background = new Image(Graphics.sheets.get("CHAT_BOX"));
		inputbox_background = new Image(Graphics.sheets.get("CHAT_INPUT"));

		// UI �ʱ�ȭ
		chatbox_label = new Label("", labelStyle);
		inputbox_textfield = new TextField("", textFieldStyle);

		// UI ����
		chatbox_label.setSize(300, 200);
		inputbox_textfield.setSize(600 - 20, 40);

		chatbox_background.setPosition(0, Display.height * Display.scale - 200);
		inputbox_background
				.setPosition((Display.width * Display.scale - 600) / 2, Display.height * Display.scale - 140);

		chatbox_label.setPosition(chatbox_background.getX() + 20, chatbox_background.getY());
		inputbox_textfield.setPosition(inputbox_background.getX() + 10, inputbox_background.getY() + 3);

		addActor(chatbox_background);
		addActor(inputbox_background);
		addActor(chatbox_label);
		addActor(inputbox_textfield);

		inputbox_textfield.addListener(new InputListener() {

			@Override
			public boolean keyTyped(InputEvent event, char character) {

				if (character == 13) {

					String message = inputbox_textfield.getText().trim();

					// �ʹ� ª�� ��� ������ �ʴ´�.
					if (message.equals(""))
						return true;

					// �ʹ� ��� �ڸ���.
					if (message.length() > 70)
						message = message.substring(0, 70);

					// ������ �޽����� ������.
					Connection.chat(message);

					// �� ĳ���� ���� �޽����� ����.
					addMessage(Connection.me.character.name + ":" + message);
					Connection.me.character.say(Connection.me.character.name + ":" + message);

					// �Է� â�� ����.
					inputbox_textfield.setText("");
				}

				return true;
			}
		});

		// close();
	}

	/**
	 * ä�� �޽��� �Է� â�� ����.
	 */
	public void open() {

		opened = true;

		inputbox_textfield.setVisible(true);
		inputbox_background.setVisible(true);
		inputbox_textfield.setText("");
	}

	/**
	 * ä�� �޽��� �Է� â�� �ݴ´�.
	 */
	public void close() {

		opened = false;

		inputbox_textfield.setVisible(false);
		inputbox_background.setVisible(false);
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
