import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** Headless-login preview launcher for capturing the principal dashboard views. */
public class CaptureGUIScreenshot {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                MainAppFrame frame = new MainAppFrame("admin", "ADMIN");
                frame.setVisible(true);
                String[] views={"Fleet Inventory","Rent a Vehicle","Business Analytics"};
                String[] files={"fleethub_inventory.png","fleethub_rent.png","fleethub_analytics.png"};
                Timer timer=new Timer(900,null); final int[] index={0};
                timer.addActionListener(e->{
                    if(index[0]>=views.length){timer.stop();frame.dispose();System.out.println("Screenshots captured.");return;}
                    frame.showView(views[index[0]]);frame.repaint();
                    Timer capture=new Timer(250,ev->{((Timer)ev.getSource()).stop();saveFrame(frame,files[index[0]]);index[0]++;timer.restart();});capture.setRepeats(false);capture.start();timer.stop();
                });timer.setRepeats(false);timer.start();
            } catch(Exception ex){ex.printStackTrace();}
        });
    }
    private static void saveFrame(JFrame frame,String filename){
        try{BufferedImage image=new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);Graphics2D g=image.createGraphics();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);frame.paint(g);g.dispose();ImageIO.write(image,"png",new File(filename));System.out.println("Saved "+filename);}catch(Exception e){e.printStackTrace();}
    }
}
