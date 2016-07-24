package sealion.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sealion.server.user.User;
import sealion.server.util.Display;
import sealion.server.world.Character;
import sealion.server.world.Location;

import com.mysql.jdbc.Driver;

@SuppressWarnings("unused")
public class Database {

	public static enum Status {
		PENDING, CONNECTED, CLOSED, ERROR
	}

	// ȣ��Ʈ
	public String host;

	// ���� ����
	public Connection connection;

	// ����
	public Statement query;

	// �����ͺ��̽� ����
	public Status status = Status.PENDING;

	// ����� ����
	public Account account;

	/**
	 * �����ͺ��̽� ����
	 */
	public Database(String host) {
		this.host = host;
	}

	/**
	 * �����ͺ��̽� ������ �����Ѵ�.
	 * 
	 * @param account
	 * @return
	 */
	public boolean connect(Account account) {

		try {
			// ���� ���
			connection = DriverManager.getConnection(host, account.user, account.password);
			
			// ���� ���� ��� ���
			query = connection.createStatement();

			status = Status.CONNECTED;

		} catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot connect to DB server.");
			Display.raw(e.getMessage());

			status = Status.ERROR;

			return false;
		}

		return true;
	}

	public boolean connect() {

		if (account == null)
			return false;

		return connect(account);
	}

	/**
	 * ������ �α��� ������ ��ȿ���� Ȯ���Ѵ�.
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean signin(String user, String password) {
		if (status != Status.CONNECTED)
			return false;

		try {

			// ���̵�� ��й�ȣ�� ��ġ�ϴ��� Ȯ���Ѵ�.
			ResultSet users = query.executeQuery(Query.get(Query.Type.SELECT_USER, user));

			while (users.next()) {
				if (users.getString("password").equals(password)) {
					// �α��� ������ ��ġ�Ѵٸ� ���� �α��� �ð��� ������Ʈ�Ѵ�.
					updateLastSigninTime(user);
					
					// �α��� ����
					return true;
				}
			}

			return false;
		}

		// ������ �߻��� ��� �α��� ����
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Signin failed.");
			Display.raw(e.getMessage());
		}
		return false;
	}

	/**
	 * ���ο� ���� ������ �����Ѵ�.
	 * 
	 * @param user
	 * @param password
	 * @param character
	 * @param email
	 * @return
	 */
	public boolean signup(String user, String password, String character, String email) {
		if (status != Status.CONNECTED)
			return false;

		// ���� ���Ӱ� ĳ���� ������ ��� ���������� üũ�Ѵ�.
		if (getUserEntry(user) != null || getCharacter(character) != null)
			return false;

		try {

			// �����ͺ��̽��� ���� ������ ĳ���� ������ �����Ѵ�.
			query.execute(Query.get(Query.Type.INSERT_USER, user, password, character, email));
			query.execute(Query.get(Query.Type.INSERT_CHARACTER, character));

			return true;
		}

		// ������ �߻��� ��� �α��� ����
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Signup failed.");
			Display.raw(e.getMessage());
		}
		return false;
	}

	/**
	 * ���� ��Ʈ���� ����Ѵ�.
	 * 
	 * @param user
	 * @return
	 */
	public User.Entry getUserEntry(String user) {
		if (status != Status.CONNECTED)
			return null;

		try {

			// ���� ������ ��� �´�.
			ResultSet users = query.executeQuery(Query.get(Query.Type.SELECT_USER, user));

			while (users.next()) {
				User.Entry entry = new User.Entry(user);
				entry.character = users.getString("character").trim();
				entry.level = users.getInt("level");
				entry.banned = users.getInt("banned") == 1 ? true : false;

				return entry;
			}
		}

		// ������ �߻��� ���
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot get user entry.");
			Display.raw(e.getMessage());
		}
		return null;
	}

	/**
	 * ĳ���͸� ����Ѵ�.
	 * 
	 * @param name
	 * @return
	 */
	public Character getCharacter(String name) {
		if (status != Status.CONNECTED)
			return null;

		try {

			// ���� ������ ��� �´�.
			ResultSet data = query.executeQuery(Query.get(Query.Type.SELECT_CHARACTER, name));

			while (data.next()) {

				// ���� ������ �ν��Ͻ��� �ִ´�.
				Character character = new Character(name);
				character.level = data.getInt("level");
				character.experience = data.getInt("exp");
				character.maxHealth = data.getInt("maxhealth");
				character.health = data.getInt("health");
				character.money = data.getInt("money");

				// ��ġ ���� ����
				character.location = new Location(data.getString("location"));

				// ��� ���� ����
				character.equipment.hat = data.getInt("hat");
				character.equipment.clothes = data.getInt("clothes");
				character.equipment.weapon = data.getInt("weapon");

				return character;
			}
		}

		// ������ �߻��� ���
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot get character info.");
			Display.raw(e.getMessage());
		}
		return null;
	}

	/**
	 * ĳ���� ������ ������Ʈ�Ѵ�.
	 * 
	 * @param character
	 * @return
	 */
	public boolean updateCharacter(Character character) {
		if (status != Status.CONNECTED)
			return false;

		try {

			// ������Ʈ�Ѵ�.
			query.executeUpdate(Query.get(Query.Type.UPDATE_CHARACTER, character.name, String.valueOf(character.level),
					String.valueOf(character.experience), String.valueOf(character.maxHealth),
					String.valueOf(character.health), String.valueOf(character.money),
					String.valueOf(character.equipment.hat), String.valueOf(character.equipment.clothes),
					String.valueOf(character.equipment.weapon), character.location.getPacket()));

			return true;
		}

		// ������ ���
		catch (SQLException e) {
			e.printStackTrace();

			Display.error("Cannot update character info");
			Display.raw(e.getMessage());
		}

		return false;
	}

	/**
	 * ������ ���� �α��� �ð��� ������Ʈ�Ѵ�.
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 */

	private boolean updateLastSigninTime(String user) throws SQLException {
		if (status != Status.CONNECTED)
			return false;

		// ���� ����
		query.executeUpdate(Query.get(Query.Type.UPDATE_LAST_LOGIN_TIME, user));

		return true;
	}

	/**
	 * �������� ������ ���´�.
	 * 
	 * @return
	 */
	public boolean close() {
		try {

			// ������ �����Ѵ�.
			connection.close();

			status = Status.CLOSED;

		} catch (SQLException e) {
			e.printStackTrace();

			status = Status.ERROR;

			return false;
		}

		return true;
	}

	/**
	 * �����ͺ��̽� ���� ���
	 * 
	 * @param host
	 * @param account
	 * @return
	 */
	public static Database getConnection(String host, Account account) {

		Database connection = new Database(host);
		connection.account = account;

		return connection;
	}

	/**
	 * �����ͺ��̽� ����
	 * 
	 * @author ����
	 * 
	 */
	public static class Account {

		// �����ͺ��̽� ���̵�
		public String user;

		// �����ͺ��̽� ��й�ȣ
		public String password;

		/**
		 * ���� ����
		 * 
		 * @param user
		 * @param password
		 */
		public Account(String user, String password) {
			this.user = user;
			this.password = password;
		}
	}
}
