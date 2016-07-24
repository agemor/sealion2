package sealion.client.ui;

import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Style {

	public static TextFieldStyle inputTextFieldStyle;
	public static LabelStyle talkBalloonLabelStyle;
	public static LabelStyle nameTagLabelStyle;
	public static TouchpadStyle touchpadStyle;
	public static ButtonStyle openChatButtonStyle;
	public static ButtonStyle openMenuButtonStyle;

	public static void load() {

		// �Է� �ؽ�Ʈ ��Ÿ�� ����
		inputTextFieldStyle = new TextFieldStyle();
		inputTextFieldStyle.font = Fonts.get(Fonts.Font.CLEAR_GOTHIC);
		inputTextFieldStyle.fontColor = Color.BLACK;
		inputTextFieldStyle.disabledFontColor = Color.GRAY;
		inputTextFieldStyle.cursor = new TextureRegionDrawable(Graphics.sheets.get("TEXT_FIELD_POINTER"));
		inputTextFieldStyle.background = new TextureRegionDrawable(Graphics.sheets.get("TEXTFIELD_BACKGROUND"));
		
		// ��ǳ�� ��
		talkBalloonLabelStyle = new LabelStyle();
		talkBalloonLabelStyle.font = Fonts.get(Fonts.Font.NANUM_GOTHIC);
		talkBalloonLabelStyle.fontColor = Color.WHITE;
		
		// ���� �±�
		nameTagLabelStyle = new LabelStyle();
		nameTagLabelStyle.font = Fonts.get(Fonts.Font.NANUM_GOTHIC);
		nameTagLabelStyle.fontColor = Color.BLACK;
		
		// ��ġ�е�
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = new TextureRegionDrawable(Graphics.sheets.get("TOUCHPAD_BACKGROUND"));
		touchpadStyle.knob = new TextureRegionDrawable(Graphics.sheets.get("TOUCHPAD_KNOB"));

		// ä��â ���� ��ư
		openChatButtonStyle = new ButtonStyle();
		openChatButtonStyle.up = new TextureRegionDrawable(Graphics.sheets.get("OPEN_CHAT_BUTTON_UP"));
		openChatButtonStyle.down = new TextureRegionDrawable(Graphics.sheets.get("OPEN_CHAT_BUTTON_DOWN"));
		openChatButtonStyle.checked = openChatButtonStyle.down;

		// �޴� ���� ��ư
		openMenuButtonStyle = new ButtonStyle();
		openMenuButtonStyle.up = new TextureRegionDrawable(Graphics.sheets.get("OPEN_MENU_BUTTON_UP"));
		openMenuButtonStyle.down = new TextureRegionDrawable(Graphics.sheets.get("OPEN_MENU_BUTTON_DOWN"));
		openMenuButtonStyle.checked = openMenuButtonStyle.down;

	}

	/**
	 * ��ư ��Ÿ���� ����� �ش�.
	 * 
	 * @param up
	 * @param down
	 * @return
	 */
	public static ButtonStyle getButtonStyle(String up, String down) {

		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(Graphics.sheets.get(up));
		style.down = new TextureRegionDrawable(Graphics.sheets.get(down));

		return style;
	}

}
