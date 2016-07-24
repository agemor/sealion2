package sealion.client;

import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.page.GamePage;
import sealion.client.page.SignInPage;
import sealion.client.ui.CommonUI;
import sealion.client.ui.InputManager;
import sealion.client.ui.Style;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {

	public static enum Page {
		SIGNIN, GAME
	}

	public static Main self;

	public GamePage gamePage;
	public SignInPage signInPage;

	@Override
	public void create() {

		Gdx.input.setCatchBackKey(true);

		// �ڿ��� �ε��Ѵ�.
		Graphics.sheets.putAll(Graphics.Loader.loadSheet("ui.xml"));
		Graphics.sheets.putAll(Graphics.Loader.loadSheet("background.xml"));
		Graphics.animations.putAll(Graphics.Loader.loadAnimation("game.xml"));
		Graphics.animations.putAll(Graphics.Loader.loadAnimation("character.xml"));
		Fonts.fonts.putAll(Fonts.Loader.loadFont("font.xml"));

		Display.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// ��Ÿ�� �ε�
		Style.load();

		// ���� UI �ʱ�ȭ
		CommonUI.initialize();

		InputManager.self = new InputManager();

		// ������ ������ �õ��Ѵ�.
		Connection.initialize();
		Connection.connect("182.216.161.175", 17343);

		gamePage = new GamePage();
		signInPage = new SignInPage();

		show(Page.SIGNIN);
	}

	/**
	 * �������� �����ش�.
	 * 
	 * @param page
	 */
	public void show(Page page) {
		switch (page) {
		case SIGNIN:
			setScreen(signInPage);

			break;
		case GAME:
			setScreen(gamePage);
			break;
		default:
			break;

		}
	}

}
