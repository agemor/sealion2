package sealion.client.asset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Graphics {

	public static Map<String, TextureRegion> sheets;
	public static Map<String, TextureRegion[]> animations;

	static {
		sheets = new HashMap<String, TextureRegion>();
		animations = new HashMap<String, TextureRegion[]>();
	}

	/**
	 * �ڿ� ���� ����(.asset) �δ�
	 * 
	 * @author ����
	 * 
	 */
	public static class Loader {

		/**
		 * XML �ڿ� ���Ͽ��� �ִϸ��̼��� �����Ѵ�.
		 * 
		 * @param assetFilePath
		 * @return
		 */
		public static Map<String, TextureRegion[]> loadAnimation(String assetFilePath) {

			// ���� ��Ʈ ���·� �ڿ��� ����Ѵ�.
			Map<String, TextureRegion> sheets = loadSheet(assetFilePath);
			Map<String, TextureRegion[]> assets = new HashMap<String, TextureRegion[]>();

			for (Map.Entry<String, TextureRegion> entry : sheets.entrySet())
				assets.put(entry.getKey(), splitTexture(entry.getValue()));

			return assets;
		}

		/**
		 * XML �ڿ� ���Ͽ��� ��Ʈ�� �����Ѵ�.
		 * 
		 * @param assetFilePath
		 * @return
		 */
		public static Map<String, TextureRegion> loadSheet(String assetFilePath) {
			try {

				// XML �����͸� �д´�.
				XmlReader xmlReader = new XmlReader();
				Element entry = xmlReader.parse(Gdx.files.internal(assetFilePath));

				// �ؽ�ó�� ����Ѵ�.
				String imagePath = entry.getAttribute("imagePath");
				Texture texture = new Texture(imagePath);

				Map<String, TextureRegion> assets = new HashMap<String, TextureRegion>();

				// ��� �Ӽ����� �д´�.
				for (int i = 0; i < entry.getChildCount(); i++) {
					Element element = entry.getChild(i);

					// �ڿ� �̸�
					String name = element.getAttribute("name");

					// ����
					int width = Integer.parseInt(element.getAttribute("width"));
					int height = Integer.parseInt(element.getAttribute("height"));
					int x = Integer.parseInt(element.getAttribute("x"));
					int y = Integer.parseInt(element.getAttribute("y"));

					assets.put(name, new TextureRegion(texture, x, y, width, height));
				}

				return assets;

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * �ؽ�ó�� ���η� 8����Ѵ�.
		 * 
		 * @param texture
		 * @return
		 */
		public static TextureRegion[] splitTexture(TextureRegion texture) {

			// ���� ���̸� ���Ѵ�.
			int width = texture.getRegionWidth() / 8;
			TextureRegion[] result = new TextureRegion[8];

			// ���� �������� �ڸ���.
			for (int i = 0; i < 8; i++)
				result[i] = new TextureRegion(texture, i * width, 0, width, texture.getRegionHeight());

			return result;
		}
	}

}
