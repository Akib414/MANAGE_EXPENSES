package gui;
import javax.swing.*;
import java.awt.*;
class BackgroundImagePanel extends JPanel {
    private Image backgroundImage;

    public BackgroundImagePanel(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        backgroundImage = icon.getImage();
        if (backgroundImage == null) {
            System.out.println("Image not loaded!");
        }
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
