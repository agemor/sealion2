package sealion.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import sealion.server.util.Display;

public class Configuration {

	/**
	 * ���� ����
	 */
	public String version = "1,0";

	/**
	 * ���� �̸� ����
	 */
	public String name = "lionse";

	/**
	 * ���� ��Ʈ
	 */
	public int port = 17343;

	/**
	 * ���ڵ� ����
	 */
	public String encoding = "utf-8";

	/**
	 * �ִ� ���� ���� ���� ��
	 */
	public int maximumConnection = 1000;

	/**
	 * ���� IP���� �ߺ� ���� ��� ����
	 */
	public boolean allowDuplicateConnection = false;

	/**
	 * �����ͺ��̽� ���� ����
	 */
	public String dbHost = "jdbc:mysql://localhost:3306/lionse";
	public String dbUser = "root";
	public String dbPassword = "asd123";

	/**
	 * ���� ����
	 */
	public Configuration() {

	}

	/**
	 * ���� ������ �ε��Ѵ�.
	 * 
	 * @param path
	 * @return
	 */
	public static Configuration load(String path) {

		// ���� ���� ���� (server.config)
		File configFile = new File(path);

		if (!configFile.exists()) {
			Display.error("���� ������ �������� �����Ƿ� �⺻ ������ ����մϴ�.");
			return new Configuration();
		}
		try {

			// ������Ƽ ��ü ����
			Properties property = new Properties();

			// ������Ƽ�� �ε��Ѵ�.
			property.load(new FileInputStream(configFile));

			// Ŀ���� ���� ����
			Configuration config = new Configuration();

			config.version = property.getProperty("version", "1.0");
			config.name = property.getProperty("name", "lionse");

			config.encoding = property.getProperty("encoding", "utf-8");
			config.allowDuplicateConnection = Boolean.parseBoolean(property.getProperty("allow-duplicate-connection",
					"false"));
			config.maximumConnection = Integer.parseInt(property.getProperty("maximum-connection", "1000"));

			config.dbHost = property.getProperty("db-host", "jdbc:mysql://localhost:3306/lionse");
			config.dbUser = property.getProperty("db-user", "root");
			config.dbPassword = property.getProperty("db-password", "asd123");

			return config;

		}

		// ������ �߻����� ���
		catch (Exception e) {
			e.printStackTrace();
		}

		Display.error("���� ������ �ҷ����� ���� ������ �߻��Ͽ� �⺻ ������ ����մϴ�.");
		return new Configuration();

	}

	public String toString() {
		String info = new String();

		info = "version: " + version//
				+ "\nname: " + name//
				+ "\nport: " + port//
				+ "\nencoding: " + encoding//
				+ "\nmaximum connection: " + maximumConnection//
				+ "\nallow duplicate connection: " + String.valueOf(allowDuplicateConnection)//
				+ "\ndb host: " + dbHost//
				+ "\ndb user:" + dbUser + "\n";

		return info;
	}
}
