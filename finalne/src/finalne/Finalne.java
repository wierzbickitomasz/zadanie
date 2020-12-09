

package finalne;

import java.awt.Image;
import java.io.IOException;
import static java.lang.Math.abs;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Finalne extends JFrame implements ActionListener  
{
String path = "";
JLabel label1,label2,label3,label4;
JButton bWczytaj,bRysuj;
    public Finalne()
    {
    setSize(1900,1000);               
    setLayout(null);
    bWczytaj = new JButton("Wczytaj");         
    bWczytaj.setBounds(10, 40, 100, 20);      
    add(bWczytaj);                             
    bWczytaj.addActionListener(this);          
    bRysuj = new JButton("Rysuj");         
    bRysuj.setBounds(120, 40, 100, 20);        
    add(bRysuj);                              
    bRysuj.addActionListener(this);
    
    label1 = new JLabel();
    label1.setBounds(10,100,800,350);
     add(label1);
    
    label2 = new JLabel();
    label2.setBounds(10,500,800,350);
    add(label2);
   
    label3 = new JLabel();
    label3.setBounds(850,100,800,350);
    add(label3);
      
     label4 = new JLabel();
    label4.setBounds(850,500,800,350);
    add(label4);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
    Object zrodlo = e.getSource(); 
    
    if(zrodlo == bWczytaj)
    {
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter the files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = file.getSelectedFile();
            path = selectedFile.getAbsolutePath();
            label1.setIcon(ResizeImage(path, 1));}
        else if(result == JFileChooser.CANCEL_OPTION){
            System.out.println("No File Select");}}
    if(zrodlo == bRysuj)
    {
        String teraz = System.getProperty("user.dir");
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        
        //BLUR
//****************************************************************************************************************
        String fileBlur = ((path).replace("\\", "/")).trim();
        System.out.println(path);
        Mat srcBlur = Imgcodecs.imread(fileBlur,0);
        Mat dst = new Mat(srcBlur.rows(), srcBlur.cols(), srcBlur.type());
        Imgproc.medianBlur(srcBlur, dst, 5);
        //Converting matrix to JavaFX writable image
        Imgcodecs.imwrite(teraz + "/blur.png", dst);
            
        //HOUGH
//****************************************************************************************************************
        String fileHough = teraz + "/blur.png";
        Mat srcfileHough = Imgcodecs.imread(fileHough,0);
            // Detecting edges of it
        Mat canny = new Mat();
        Imgproc.Canny(srcfileHough, canny, 50, 200, 3, false);
        // Changing the color of the canny
        Mat cannyColor = new Mat();
        Imgproc.cvtColor(canny, cannyColor, Imgproc.COLOR_GRAY2BGR);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(canny, lines, 1, Math.PI/50, 90, 5, 100);
        //operujemy na trzech liniach, dolnej srodkowej i gornej
        double max = 0;
        double min = 9999;
        Point prawy = new Point(0, 0);
        Point lewy = new Point(0, 0);
        //double posrednie = 0;
        for (int i = 0; i < lines.rows(); i++) {
            double[] data = lines.get(i, 0);
            //Drawing lines on the image
            Point pt1 = new Point(data[0], data[1]);
            Point pt2 = new Point(data[2], data[3]);
            if((data[1] > 200) && (data[2] > 300)){ //tylko prawy dół
                Imgproc.line(cannyColor, pt1, pt2, new Scalar(0, 0, 255), 3);
            if(pt2.y > max){
                max = pt2.y;}
            if(pt2.y < min){
                min = pt2.y;
                lewy = pt1;
                prawy = pt2;}
            }}
        Imgcodecs.imwrite(teraz + "/trzylinie.png", cannyColor);
        System.out.println(lewy);
        System.out.println(prawy);
        double odejmijx = abs((prawy.x - lewy.x)/2);
        double odejmijy = abs((prawy.y - lewy.y)/2);
        System.out.println(odejmijx);
        System.out.println(odejmijy);
        prawy.y = prawy.y +12; //odsuwanie od gornej linii
        lewy.x = lewy.x +odejmijx;
        lewy.y = lewy.y -odejmijy+12; //odsuwanie od gornej linii
        String file3linie = fileBlur;
        Mat src3linie = Imgcodecs.imread(file3linie);
        Imgproc.line(src3linie, lewy, prawy, new Scalar(181, 204, 1), 3);
        Imgcodecs.imwrite(teraz + "/wynik.png", src3linie);
        label2.setIcon(ResizeImage(teraz + "/blur.png", 2));
        label3.setIcon(ResizeImage(teraz + "/trzylinie.png", 3));
        label4.setIcon(ResizeImage(teraz + "/wynik.png", 4));}}
       
    
    public ImageIcon ResizeImage(String ImagePath, int a)
    {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label1.getWidth(), label1.getHeight(), Image.SCALE_SMOOTH);
        if(a == 1){
            newImg = img.getScaledInstance(label1.getWidth(), label1.getHeight(), Image.SCALE_SMOOTH);}
        else if(a == 2){
            newImg = img.getScaledInstance(label2.getWidth(), label2.getHeight(), Image.SCALE_SMOOTH);}
        else if(a == 3){
            newImg = img.getScaledInstance(label3.getWidth(), label3.getHeight(), Image.SCALE_SMOOTH);}
        else if(a == 4){
            newImg = img.getScaledInstance(label4.getWidth(), label4.getHeight(), Image.SCALE_SMOOTH);}
        ImageIcon image = new ImageIcon(newImg);
        return image;
        }
    
    
    
    
        public static void main(String[] args) throws IOException
        {
        Finalne Nowe = new Finalne();   
        Nowe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        Nowe.setVisible(true);    
        }
}
