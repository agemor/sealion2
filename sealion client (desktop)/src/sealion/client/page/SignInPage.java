package sealion.client.page;

import sealion.client.Display;
import sealion.client.Main;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.Packet;
import sealion.client.net.SigninEvent;
import sealion.client.ui.CommonUI;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SignInPage extends ChangeListener implements Page, SigninEvent {

	/**
	 * �ý��� ����
	 */
	public GL20 gl = Gdx.graphics.getGL20();

	/**
	 * UI
	 */
	public Stage stage;
	public Image background;
	public TextField id_input_textfield;
	public TextField password_input_textfield;
	public Button signin_button;
	public Button signup_button;
	public Camera camera;

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

		// �ؽ�Ʈ �ʵ�
		id_input_textfield = new TextField("", Style.inputTextFieldStyle);
		password_input_textfield = new TextField("", Style.inputTextFieldStyle);

		password_input_textfield.setPasswordCharacter('��');
		password_input_textfield.setPasswordMode(true);

		id_input_textfield.setWidth(250);
		password_input_textfield.setWidth(250);

		// ��ư
		signin_button = new Button(Style.getButtonStyle("LOGIN_BUTTON_UP", "LOGIN_BUTTON_DOWN"));
		signup_button = new Button(Style.getButtonStyle("REGISTER_BUTTON_UP", "REGISTER_BUTTON_DOWN"));

		// ��ġ ����
		id_input_textfield.setPosition(320, 480 - 38 - 46);
		password_input_textfield.setPosition(320, 480 - 95 - 46);
		signin_button.setPosition(590, 480 - 110 - 35);
		signup_button.setPosition(590, 480 - 110 - 180);

		// ���������� �߰�
		stage.addActor(background);
		stage.addActor(id_input_textfield);
		stage.addActor(password_input_textfield);
		stage.addActor(signin_button);
		stage.addActor(signup_button);

		// �̺�Ʈ ������ �߰�
		Connection.addEventListener(this);

		signin_button.addListener(this);
		signup_button.addListener(this);
	}

	@Override
	public void backKeyPressed() {

		if (CommonUI.isBusy() && !CommonUI.areYouSureToExit) {
			CommonUI.closeAll();
			return;
		}

		if (CommonUI.areYouSureToExit)
			Gdx.app.exit();

		// �ѹ� �� ������ �����մϴ�.
		else
			CommonUI.areYouSureToExit();
	}

	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (CommonUI.isBusy())
			return;

		// �α����� ���
		if (actor == signin_button) {

			// ������ �α��� ��û�� �Ѵ�.
			String userID = id_input_textfield.getText().trim();
			String userPassword = password_input_textfield.getText().trim();

			if (userID.length() < 1 || userPassword.length() < 1) {
				CommonUI.alert("���̵�� ��й�ȣ�� ��� �Է��� �ּ���.");
				return;
			}

			// UI�� �󸰴�.
			CommonUI.freeze();

			Connection.signin(userID, userPassword);
		}

		// ȸ�� ������ ���
		else if (actor == signup_button) {
			Main.self.show(Main.Page.SIGNUP);
		}

	}

	@Override
	public void onSignin(boolean succeed) {

		// UI�� ���δ�.
		CommonUI.melt();

		if (succeed) {
			Gdx.app.log("login", "success");

			// ����� ����.
			Main.self.show(Main.Page.GAME);

			// ���� �ڷḦ ��û�Ѵ�.
			Connection.send(new Packet(Packet.Header.CHARACTER, ""));
			Connection.send(new Packet(Packet.Header.USER_LIST, ""));

		} else {
			CommonUI.alert("�α��ο� �����Ͽ����ϴ�.");
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
		//stage.setViewport(camera.viewportWidth, camera.viewportHeight, false);
	}

	@Override
	public void resume() {
	}

}
