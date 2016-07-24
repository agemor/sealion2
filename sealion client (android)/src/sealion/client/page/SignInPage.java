package sealion.client.page;

import sealion.client.Display;
import sealion.client.Main;
import sealion.client.android.ActionResolver;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.Packet;
import sealion.client.net.SigninEvent;
import sealion.client.net.SignupEvent;
import sealion.client.ui.CommonUI;
import sealion.client.ui.DialogEvent;
import sealion.client.ui.InputManager;
import sealion.client.ui.Style;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SignInPage extends ChangeListener implements Page, SigninEvent, SignupEvent, DialogEvent {

	/**
	 * �ý��� ����
	 */
	public GL20 gl = Gdx.graphics.getGL20();

	/**
	 * UI
	 */
	public Stage stage;
	public Image background;
	public Button signin_button;
	public Button signup_button;
	public Camera camera;

	/**
	 * Back Key ó�� ����
	 */
	private float elapsedTime = 0;
	private boolean areYouSureToExit = false;

	public SignInPage() {

		super();

		// ������ ���� ���� �ʱ�ȭ
		camera = new OrthographicCamera();
		initializeUI();
	}

	/**
	 * UI �ʱ�ȭ
	 */
	private void initializeUI() {

		// �������� ����
		stage = new Stage();

		// ��� �̹���
		background = new Image(Graphics.sheets.get("LOGIN"));

		// ��ư
		signin_button = new Button(Style.getButtonStyle("LOGIN_BUTTON_UP", "LOGIN_BUTTON_DOWN"));
		signup_button = new Button(Style.getButtonStyle("REGISTER_BUTTON_UP", "REGISTER_BUTTON_DOWN"));
		signin_button.setPosition(200, 480 - 110 - 140);
		signup_button.setPosition(440, 480 - 110 - 140);

		// ���������� �߰�
		stage.addActor(background);
		stage.addActor(signin_button);
		stage.addActor(signup_button);

		// �̺�Ʈ ������ �߰�
		Connection.signinEventListeners.add(this);
		Connection.signupEventListeners.add(this);

		signin_button.addListener(this);
		signup_button.addListener(this);
	}

	@Override
	public void backKeyPressed() {

		if (areYouSureToExit) {
			Connection.close();
			Gdx.app.exit();
		}

		else {
			areYouSureToExit = true;
			ActionResolver.showShortToast("�ڷΰ��� ��ư�� �� �� �� ������ �����մϴ�.");
		}
	}

	@Override
	public void onDialogFinished(String... args) {

		if (args.length == 2) {

			String userID = args[0].trim();
			String userPassword = args[1].trim();

			if (userID.length() < 1 || userPassword.length() < 1) {
				ActionResolver.showAlertBox("�α��� ����", "��� �׸��� �Է��� �ּ���", "Ȯ��");
				return;
			}

			CommonUI.freeze();

			Connection.signin(userID, userPassword);
		} else {
			String userID = args[0].trim();
			String userPassword = args[1].trim();
			String userCharacter = args[2].trim();
			String userEmail = args[3].trim();

			if (userID.length() < 1 || userPassword.length() < 1 || userCharacter.length() < 1
					|| userEmail.length() < 6) {
				ActionResolver.showAlertBox("ȸ�� ���� ����", "��� �׸��� �ùٸ��� �Է��� �ּ���.", "Ȯ��");
				return;
			}

			CommonUI.freeze();

			Connection.signup(userID, userPassword, userCharacter, userEmail);

		}
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {

		// �α����� ���
		if (actor == signin_button) {
			ActionResolver.showSigninBox("�α���", "Ȯ��", "���", this);
		}

		// ȸ�� ������ ���
		else if (actor == signup_button) {
			ActionResolver.showSignupBox("ȸ�� ����", "���� ����", "���", this);
		}

	}

	@Override
	public void onSignin(boolean succeed) {

		// UI�� ���δ�.
		CommonUI.melt();

		if (succeed) {
			ActionResolver.showShortToast("�α��ο� �����Ͽ����ϴ�.");
			// ����� ����.
			Main.self.show(Main.Page.GAME);

			// ���� �ڷḦ ��û�Ѵ�.
			Connection.send(new Packet(Packet.Header.CHARACTER, ""));
			Connection.send(new Packet(Packet.Header.USER_LIST, ""));

		} else {
			ActionResolver.showAlertBox("�α��� ����", "���̵� �Ǵ� ��й�ȣ�� �ùٸ��� �ʽ��ϴ�.", "Ȯ��");
		}
	}

	@Override
	public void onSignup(boolean succeed) {
		CommonUI.melt();

		// �����ߴٸ�
		if (succeed) {
			ActionResolver.showAlertBox("ȸ�� ���� �Ϸ�", "ȸ�� ������ �Ϸ��Ͽ����ϴ�.", "Ȯ��");
		}

		// ����
		else {
			ActionResolver.showAlertBox("ȸ�� ���� ����", "�̹� ��� ���� ���̵� ĳ���� �̸��Դϴ�.", "Ȯ��");
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, InputManager.self));
		InputManager.targetPage = this;
	}

	@Override
	public void render(float delta) {

		// �����ӹ��� ����
		Gdx.gl.glClearColor(01f, 1f, 1f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// ī�޶� ������Ʈ
		camera.update();

		stage.draw();

		CommonUI.draw(delta);

		if (areYouSureToExit) {
			elapsedTime += delta;

			if (elapsedTime > 3) {
				areYouSureToExit = false;
				elapsedTime = 0;
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// Connection.removeEventListener(this);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// ȭ�� �ػ󵵿� ���߾� ����
		camera.viewportWidth = (float) Math.ceil(Display.scale * width);
		camera.viewportHeight = (float) Math.ceil(Display.scale * height);

		camera.position.set(Display.width / 2, Display.height / 2, 0);
		stage.getViewport().setScreenWidth((int) camera.viewportWidth);
		stage.getViewport().setScreenHeight((int) camera.viewportHeight);
	}

	@Override
	public void resume() {
	}

}
