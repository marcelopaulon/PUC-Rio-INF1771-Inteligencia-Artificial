package gfx;

import java.awt.image.BufferedImage;

/**
 * TODO: Comentar. N�o entendi o que a classe faz. Comente seu c�digo, Marcelo.
 */
public class SpriteSheet {
	/**
	 * Arquivo de imagem contendo os tiles que ser�o usados
	 */
	private BufferedImage sheet;

	/**
	 * Construtor de SpriteSheet
	 * <p><b>SpriteSheet:</b> TODO: Comentar. N�o entendi o que a classe faz. Comente seu c�digo, Marcelo.</p>
	 * @param sheet sheet.png depois de ser lido pelo programa
	 */
	public SpriteSheet(BufferedImage sheet) {
		this.sheet = sheet;
	}

	/**
	 * Divide a imagem em partes menores (para especificar o tamanho de cada tile) 
	 * @param x Coordenada x onde a subimagem (tile) come�a
	 * @param y Coordenada y onde a subimagem (tile) come�a
	 * @param width Largura da subimagem (tile)
	 * @param height Altura da subimagem (tile)
	 * @return a subimagem carregada pelo programa em uma BufferedImage
	 */
	public BufferedImage crop(int x, int y, int width, int height) {
		return sheet.getSubimage(x, y, width, height);
	}
}
