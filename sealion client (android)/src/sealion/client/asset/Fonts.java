package sealion.client.asset;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * ��Ʈ
 * 
 * @author ����
 * 
 */
public class Fonts {

	public static Map<String, BitmapFont> fonts;

	static {
		fonts = new HashMap<String, BitmapFont>();
	}

	/**
	 * ��Ʈ�� ����Ѵ�.
	 * 
	 * @param font
	 * @return
	 */
	public static BitmapFont get(Font font) {
		return fonts.get(font.toString());
	}

	public static enum Font {
		CLEAR_GOTHIC, NANUM_GOTHIC;
	}

	/**
	 * ��Ʈ�� ��Ʈ �δ�
	 * 
	 * @author ����
	 * 
	 */
	public static class Loader {

		/**
		 * ��Ʈ�� ��Ʈ�� �ε��Ѵ�.
		 * 
		 * @param assetFilePath
		 * @return
		 */
		public static Map<String, BitmapFont> loadFont(String assetFilePath) {
			try {

				// XML �����͸� �д´�.
				XmlReader xmlReader = new XmlReader();
				Element entry = xmlReader.parse(Gdx.files.internal(assetFilePath));

				Map<String, BitmapFont> assets = new HashMap<String, BitmapFont>();

				// ��� �Ӽ����� �д´�.
				for (int i = 0; i < entry.getChildCount(); i++) {
					Element element = entry.getChild(i);

					// ��Ʈ �̸�
					String name = element.getAttribute("name");

					// ��Ʈ ���
					String fontPath = element.getAttribute("fontPath");

					assets.put(name, new BitmapFont(Gdx.files.internal(fontPath), false));

				}

				return assets;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
