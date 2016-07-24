package sealion.client;

import sealion.client.asset.Fonts;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.page.GamePage;
import sealion.client.page.SignInPage;
import sealion.client.page.SignUpPage;
import sealion.client.ui.CommonUI;
import sealion.client.ui.InputManager;
import sealion.client.ui.Style;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {

	public static enum Page {
		SIGNIN, SIGNUP, GAME
	}

	public static Main self;

	public GamePage gamePage;
	public SignInPage signInPage;
	public SignUpPage signUpPage;

	@Override
	public void create() {

		Gdx.input.setCatchBackKey(true);

		// �ڿ��� �ε��Ѵ�.
		Graphics.sheets.putAll(Graphics.Loader.loadSheet("ui.xml"));
		Graphics.sheets.putAll(Graphics.Loader.loadSheet("background.xml"));
		Graphics.animations.putAll(Graphics.Loader.loadAnimation("game.xml"));
		Graphics.animations.putAll(Graphics.Loader.loadAnimation("character.xml"));
		Fonts.fonts.putAll(Fonts.Loader.loadFont("font.xml"));

		// ��Ÿ�� �ε�
		Style.load();

		// ���� UI �ʱ�ȭ
		CommonUI.initialize();

		InputManager.self = new InputManager();

		// ������ ������ �õ��Ѵ�.
		Connection.initialize();
		Connection.connect("127.0.0.1", 17343);

		gamePage = new GamePage();
		signInPage = new SignInPage();
		signUpPage = new SignUpPage();

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
		case SIGNUP:
			setScreen(signUpPage);
			break;
		case GAME:
			setScreen(gamePage);
			break;
		default:
			break;

		}
	}

}
