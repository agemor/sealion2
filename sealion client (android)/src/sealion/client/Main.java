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

		// 자원을 로드한다.
		Graphics.sheets.putAll(Graphics.Loader.loadSheet("ui.xml"));
		Graphics.sheets.putAll(Graphics.Loader.loadSheet("background.xml"));
		Graphics.animations.putAll(Graphics.Loader.loadAnimation("game.xml"));
		Graphics.animations.putAll(Graphics.Loader.loadAnimation("character.xml"));
		Fonts.fonts.putAll(Fonts.Loader.loadFont("font.xml"));

		Display.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// 스타일 로드
		Style.load();

		// 공통 UI 초기화
		CommonUI.initialize();

		InputManager.self = new InputManager();

		// 서버와 연결을 시도한다.
		Connection.initialize();
		Connection.connect("182.216.161.175", 17343);

		gamePage = new GamePage();
		signInPage = new SignInPage();

		show(Page.SIGNIN);
	}

	/**
	 * 페이지를 보여준다.
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
