package sealion.client.page;

import sealion.client.Display;
import sealion.client.Main;
import sealion.client.asset.Graphics;
import sealion.client.net.Connection;
import sealion.client.net.SignupEvent;
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

public class SignUpPage extends ChangeListener implements Page, SignupEvent {

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
	public TextField character_input_textfield;
	public TextField email_input_textfield;
	public Button signup_button;
	public Button cancel_button;
	public Camera camera;

	public SignUpPage() {

		super();

		// ������ ���� ���� �ʱ�ȭ
		camera = new OrthographicCamera();

		initializeUI();
	}

	/**
	 * UI �ʱ�ȭ
	 */
	private void initializeUI() {
		stage = new Stage();

		// ��� �̹���
		background = new Image(Graphics.sheets.get("REGISTER"));

		id_input_textfield = new TextField("", Style.inputTextFieldStyle);
		password_input_textfield = new TextField("", Style.inputTextFieldStyle);
		email_input_textfield = new TextField("", Style.inputTextFieldStyle);
		character_input_textfield = new TextField("", Style.inputTextFieldStyle);
		cancel_button = new Button(Style.getButtonStyle("CANCEL_BUTTON_UP", "CANCEL_BUTTON_DOWN"));
		signup_button = new Button(Style.getButtonStyle("REGISTER_BUTTON_UP", "REGISTER_BUTTON_DOWN"));

		id_input_textfield.setWidth(250);
		password_input_textfield.setWidth(250);
		email_input_textfield.setWidth(250);
		character_input_textfield.setWidth(250);

		id_input_textfield.setPosition(320, 480 - 46 - 40);
		password_input_textfield.setPosition(320, 480 - 46 - 105);
		email_input_textfield.setPosition(320, 480 - 46 - 170);
		character_input_textfield.setPosition(320, 480 - 46 - 234);

		signup_button.setPosition(590, 480 - 110 - 170);
		cancel_button.setPosition(590, 480 - 110 - 350);

		stage.addActor(background);
		stage.addActor(id_input_textfield);
		stage.addActor(password_input_textfield);
		stage.addActor(email_input_textfield);
		stage.addActor(character_input_textfield);
		stage.addActor(signup_button);
		stage.addActor(cancel_button);

		cancel_button.addListener(this);
		signup_button.addListener(this);

	}

	@Override
	public void backKeyPressed() {

		if (CommonUI.isBusy()) {
			CommonUI.closeAll();
			return;
		}

		changed(null, cancel_button);
	}

	/**
	 * Ŭ���Ǿ��� �� �����
	 */
	@Override
	public void changed(ChangeEvent event, Actor actor) {

		if (CommonUI.isBusy())
			return;

		// ȸ������ ��ư�� ������ ���
		if (actor == signup_button) {

			String userID = id_input_textfield.getText().trim();
			String userPassword = password_input_textfield.getText().trim();
			String userCharacter = character_input_textfield.getText().trim();
			String userEmail = email_input_textfield.getText().trim();

			if (userID.length() < 1 || userPassword.length() < 1 || userCharacter.length() < 1
					|| userEmail.length() < 6) {
				CommonUI.alert("��� �׸��� �ùٸ��� �Է��� �ּ���.");
				return;
			}

			CommonUI.freeze();

			Connection.signup(userID, userPassword, userCharacter, userEmail);
		}

		// ��� ��ư�� ������ ���
		else if (actor == cancel_button) {

			// ��� �׸��� �����.
			id_input_textfield.setText("");
			password_input_textfield.setText("");
			character_input_textfield.setText("");
			email_input_textfield.setText("");

			// �α��� �������� ���ư���.
			Main.self.show(Main.Page.SIGNIN);

		}

	}

	@Override
	public void onSignup(boolean succeed) {

		CommonUI.melt();

		// �����ߴٸ�
		if (succeed) {
			CommonUI.notice("���ο� ������ ��������ϴ�.");
		}

		// ����
		else {
			CommonUI.alert("ȸ�� ���Կ� �����߽��ϴ�.");
		}

	}

	@Override
	public void show() {

		Gdx.input.setInputProcessor(new InputMultiplexer(stage, InputManager.self));
		InputManager.targetPage = this;

		// �̺�Ʈ ������ �߰�
		Connection.addEventListener(this);
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
	public void hide() {
		Connection.removeEventListener(this);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
