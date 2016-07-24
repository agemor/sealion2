package sealion.client.android;

import sealion.client.ui.DialogEvent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * �ȵ���̵� ����Ƽ�� API�� ����ϰ� �� �ش�.
 * 
 * @author ����
 * 
 */
public class ActionResolver {

	private static Handler uiThread;
	private static Context context;

	public static void initialize(Context appContext) {
		uiThread = new Handler();
		context = appContext;
	}

	/**
	 * �佺Ʈ �޽��� ���� (��)
	 * 
	 * @param toastMessage
	 */
	public static void showShortToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * �佺Ʈ �޽��� ���� (ª��)
	 * 
	 * @param toastMessage
	 */
	public static void showLongToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * ���â ����
	 * 
	 * @param alertBoxTitle
	 * @param alertBoxMessage
	 * @param alertBoxButtonText
	 */
	public static void showAlertBox(final String alertBoxTitle, final String alertBoxMessage,
			final String alertBoxButtonText) {

		uiThread.post(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(context).setTitle(alertBoxTitle).setMessage(alertBoxMessage)
						.setNeutralButton(alertBoxButtonText, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						}).create().show();
			}
		});
	}

	public static void showSigninBox(final String signinBoxTitle, final String signinButtonText,
			final String cancelButtonText, final DialogEvent event) {

		uiThread.post(new Runnable() {

			@Override
			public void run() {

				// ���̾ƿ� �Ķ���� ����
				LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				LinearLayout.LayoutParams editorParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				// �Է� �ʵ尡 �ִ� ���̾ƿ�
				LinearLayout layout = new LinearLayout(context);

				layout.setLayoutParams(layoutParms);
				layout.setOrientation(LinearLayout.VERTICAL);

				// ���̵�/��й�ȣ �Է�
				final EditText id_input = new EditText(context);
				final EditText password_input = new EditText(context);

				id_input.setLayoutParams(editorParms);
				id_input.setHint("���̵�");

				password_input.setLayoutParams(editorParms);
				password_input.setHint("��й�ȣ");
				password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

				// ui �߰�
				layout.addView(id_input);
				layout.addView(password_input);

				// �α��� ��ȭâ ����
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(signinBoxTitle);
				builder.setView(layout);

				// ��� ��ư
				builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				// �α��� ��ư
				builder.setPositiveButton(signinButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						event.onDialogFinished(id_input.getText().toString(), password_input.getText().toString());
					}
				});

				builder.create().show();

			}
		});

	}

	public static void showSignupBox(final String signupBoxTitle, final String signupButtonText,
			final String cancelButtonText, final DialogEvent event) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {

				// ���̾ƿ� �Ķ���� ����
				LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				LinearLayout.LayoutParams editorParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				// �Է� �ʵ尡 �ִ� ���̾ƿ�
				LinearLayout layout = new LinearLayout(context);

				layout.setLayoutParams(layoutParms);
				layout.setOrientation(LinearLayout.VERTICAL);

				// ���̵�/��й�ȣ �Է�
				final EditText id_input = new EditText(context);
				final EditText password_input = new EditText(context);
				final EditText character_input = new EditText(context);
				final EditText email_input = new EditText(context);

				id_input.setLayoutParams(editorParms);
				id_input.setHint("���̵�");

				password_input.setLayoutParams(editorParms);
				password_input.setHint("��й�ȣ");

				character_input.setLayoutParams(editorParms);
				character_input.setHint("ĳ���� �̸�");

				email_input.setLayoutParams(editorParms);
				email_input.setHint("�̸��� �ּ�");

				// ui �߰�
				layout.addView(id_input);
				layout.addView(password_input);
				layout.addView(character_input);
				layout.addView(email_input);

				// �α��� ��ȭâ ����
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(signupBoxTitle);
				builder.setView(layout);

				// ��� ��ư
				builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				// �α��� ��ư
				builder.setPositiveButton(signupButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						String userID = id_input.getText().toString();
						String password = password_input.getText().toString();
						String characterName = character_input.getText().toString();
						String email = email_input.getText().toString();

						event.onDialogFinished(userID, password, characterName, email);
					}
				});

				builder.create().show();
			}
		});

	}

	public static void showChatBox(final String chatBoxTitle, final String sendButtonText,
			final String cancelButtonText, final DialogEvent event) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {

				// ���̾ƿ� �Ķ���� ����
				LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				LinearLayout.LayoutParams editorParms = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F);

				// �Է� �ʵ尡 �ִ� ���̾ƿ�
				LinearLayout layout = new LinearLayout(context);

				layout.setLayoutParams(layoutParms);
				layout.setOrientation(LinearLayout.VERTICAL);

				// �޽��� �Է�ĭ
				final EditText message_input = new EditText(context);

				message_input.setLayoutParams(editorParms);
				message_input.setHint("�޽���");

				// ui �߰�
				layout.addView(message_input);

				// �α��� ��ȭâ ����
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(chatBoxTitle);
				builder.setView(layout);

				// ��� ��ư
				builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				// ������ ��ư
				builder.setPositiveButton(sendButtonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						event.onDialogFinished(message_input.getText().toString());
					}
				});

				builder.create().show();

			}
		});
	}

	public static void openUri(String uri) {
		Uri myUri = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
		context.startActivity(intent);
	}
}
