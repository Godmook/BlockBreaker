import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
abstract class Object_set{
    Object_set(){

    }
    abstract void draw(Graphics g);
    abstract void update(float var1);
    abstract void collisionDetect(Object_set var1);
    abstract  boolean isDie();
}
class Block_Obj extends Object_set{
    int x;
    int y;
    int w;
    int h;
    int vx;
    int time;
    Color color;
    static int Dist=2;
    boolean Move=false;
    boolean die=false;
    Block_Obj(int _x,int _y,int _w,int _h,boolean Case){
        this.x=_x;
        this.y=_y;
        this.w=_w;
        this.h=_h;
        this.Move=Case;
        if(this.Move){
            int r=(int)(Math.random()*2);
            if(r==1){
                color= Color.RED;
            }
            else{
                color=Color.YELLOW;
            }
        }
        else
            color=Color.gray;
        this.die=false;
    }
    @Override
    void draw(Graphics g) {
    	Graphics2D g2=(Graphics2D)g;
        g.setColor(this.color);
        g.fillRoundRect(this.x+Dist,this.y+Dist,this.w-2*Dist,this.h-2*Dist,10,10);
        if(this.color==Color.gray) {
        	GradientPaint GradientPanel = new GradientPaint(x, y, color.brighter(), x, y+h, color.darker());
        	g2.setPaint(GradientPanel);
        	g2.setStroke(new BasicStroke(3));
        	g2.drawRoundRect(this.x+Dist,this.y+Dist,this.w-2*Dist,this.h-2*Dist,10,10);
        }
    }

    @Override
    void update(float var1) {
        this.x=this.x+(int)((float)this.vx*var1);
        if(this.x<0)x=0;
        if(this.x+w>790)this.x=790-w;
    }

    @Override
    void collisionDetect(Object_set var1) {

    }

    @Override
    boolean isDie() {
        return this.die;
    }
}
class Ball_Obj extends Object_set{
    int prev_x;
    int prev_y;
    int x;
    int y;
    int vx;
    int vy;
    int r=5;
    boolean Fall_down;
    GamePhase p;
    Ball_Obj(int _x,int _y,int _vx,int _vy,GamePhase panel){
        this.prev_x=_x;
        this.prev_y=_y;
        this.x=this.prev_x;
        this.y=this.prev_y;
        this.vx=_vx;
        this.vy=_vy;
        p=panel;
        Fall_down=false;
    }
    @Override
    void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(this.x-r,this.y-r,2*r,2*r);
    }

    @Override
    void update(float var1) {
        prev_y=y;
        prev_x=x;
        this.x=this.x+(int)((float)this.vx*var1);
        this.y=this.y+(int)((float)this.vy*var1);
        Cur();
    }

    @Override
    void collisionDetect(Object_set var1) {
        if(!var1.isDie()){
            if(var1 instanceof Block_Obj){
                Block_Obj a=(Block_Obj) var1;
                if(this.x>=a.x-this.r && this.x<=a.x+a.w+this.r &&this.y+this.r>=a.y&&this.y-this.r<=a.y+a.h){
                    if(prev_y + r < a.y ) {y = a.y-r; vy = -vy;};
                    if(prev_y - r > a.y + a.h) {y = a.y+ a.h + r; vy = -vy;};
                    if(prev_x + r <a.x) { x = a.x - r; vx = -vx;}
                    if(prev_x -r >a.x + a.w) { x= a.x +a.w +r; vx=-vx;}
                    if(a.Move){
                        a.die=true;
                    }
                    if(a.color==Color.red) {
                    	p.Red_box_sound.setFramePosition(0);
                    	p.Red_box_sound.start();
                    }
                    if(a.color==Color.YELLOW){
                    	p.Yellow_box_sound.setFramePosition(0);
                    	p.Yellow_box_sound.start();
                        for(int i=0;i<2;i++) {
                            float angle = (float)(Math.random()*360) * 3.141592f / 180.0f;
                            float speed=20*20+30*30;
                            speed=(float)(Math.sqrt(speed));
                            float vx = (float) (speed*Math.cos(angle));
                            float vy = (float) (speed*Math.sin(angle));
                            p.obj.add(new Ball_Obj(x, y, (int) vx, (int) vy, p));
                        }
                    }
                    if(a.color==Color.gray) {
                    	p.Gray_box_sound.setFramePosition(0);
                    	p.Gray_box_sound.start();
                    }
                }
            }
        }
    }
    void Cur(){
        int w=p.getWidth();
        if(x-r<0){x=r;vx=-vx;}
        if(x+r>w){x=w-r;vx=-vx;}
        if(y-r<0){y=r;vy=-vy;}
    }
    void CheckFallDown(){
        if(y-r>p.getHeight())Fall_down=true;
        else
            Fall_down=false;
    }
    @Override
    boolean isDie() {
        CheckFallDown();
        return Fall_down;
    }
}
class StartPage extends JPanel implements Runnable,KeyListener{
    Thread t;
    private Clip sound;
    int time=0;
    HW5 inn;
    StartPage(HW5 in){
        inn=in;
        setFocusable(true);
        addKeyListener(this);
        t= new Thread(this);
        t.start();
        try {
        	sound = AudioSystem.getClip();
            URL url = this.getClass().getClassLoader().getResource("start_music.wav");
            AudioInputStream fp = AudioSystem.getAudioInputStream(url);
            sound.open(fp);
            sound.loop(-1);
        }catch (LineUnavailableException var4) {
            var4.printStackTrace();
        } catch (UnsupportedAudioFileException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }
    void WriteFont(Graphics2D g2,String tmp,Dimension d,Font newFont,float Y){
        FontMetrics fontMetrics = getFontMetrics(newFont);
        Rectangle stringBounds = fontMetrics.getStringBounds(tmp, g2).getBounds();
        int textX = (d.width - stringBounds.width) / 2;
        int textY = (int)((double)(d.height - stringBounds.height) / Y) + fontMetrics.getAscent();
        g2.setFont(newFont);
        g2.drawString(tmp,textX,textY);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d=this.getSize();
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint GradientPanel = new GradientPaint(0,0,new Color( 102, 103, 171),0,d.height,new Color(224, 225, 255));
        g2.setPaint(GradientPanel);
        g2.fillRect(0,0,d.width,d.height);
        Font newFont=new Font("Showcard Gothic",1,(int)(d.height*0.06));
        g2.setPaint(Color.WHITE);
        FontMetrics fontMetrics = getFontMetrics(newFont);
        String tmp="Java Programming";
        WriteFont(g2,tmp,d,newFont,6);
        tmp="Homework #5";
        WriteFont(g2,tmp,d,newFont,4.5F);
        Font pressFont=new Font("Showcard Gothic",1,(int)(d.height*0.1));
        tmp="Block Breaker";
        WriteFont(g2,tmp,d,pressFont,2.3F);
        if(time%2==0){
            g2.setPaint(Color.decode("#ff533d"));
            tmp="Press Spacebar to play!";
            Font EnterFont=new Font("Segoe Print",1,(int)(d.height*0.04));
            WriteFont(g2,tmp,d,EnterFont,1.3F);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.time++;
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            Thread.currentThread().interrupt();
            sound.stop();
            sound.setFramePosition(0);
            inn.changePanel();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
class GamePhase extends JPanel implements KeyListener,Runnable {
    ArrayList<Object_set> obj = new ArrayList<>();
    int Stage = 0;
    HW5 inn;
    int ball_num = 0;
    int block_num = 0;
    int time=0;
    Thread t;
    private Clip game_over=null;
    Clip Yellow_box_sound=null;
    Clip Red_box_sound=null;
    Clip Gray_box_sound=null;
    private Clip Level_up=null;
    void StartStage() {
        ball_num = 0;
        block_num = 0;
        obj.clear();
        obj.add(new Ball_Obj(400, 600, 20, -30, this));
        obj.add(new Block_Obj(340, 700, 150, 40, false));
        int dx = (Stage + 1) * 3;
        int dy = (Stage + 1) * 3;
        int w = getWidth() / dx;
        int h = (int) ((float) getHeight() * 0.4F / (float) dy);
        for (int i = 0; i < dx; i++) {
            for (int j = 0; j < dy; j++) {
            	Block_Obj a=new Block_Obj(i * w, j * h, w, h, true);
            	a.time=this.time;
                obj.add(a);
            }
        }
        Iterator it = obj.iterator();
        while (it.hasNext()) {
            Object_set c = (Object_set) it.next();
            if (c instanceof Ball_Obj) ball_num++;
            if (c instanceof Block_Obj) block_num++;
        }
    }
    GamePhase(HW5 in) {
        inn = in;
        inn.cur_score=0;
        obj.clear();
        setFocusable(true);
        addKeyListener(this);
        t = new Thread(this);
        t.start();
        try {
        	game_over = AudioSystem.getClip();
            URL url = this.getClass().getClassLoader().getResource("game_over.wav");
            AudioInputStream fp = AudioSystem.getAudioInputStream(url);
            game_over.open(fp);
            Gray_box_sound = AudioSystem.getClip();
            URL url1 = this.getClass().getClassLoader().getResource("bar_pop.wav");
            AudioInputStream fp1 = AudioSystem.getAudioInputStream(url1);
            Gray_box_sound.open(fp1);
            Yellow_box_sound = AudioSystem.getClip();
            URL url2 = this.getClass().getClassLoader().getResource("ping2.wav");
            AudioInputStream fp2 = AudioSystem.getAudioInputStream(url2);
            Yellow_box_sound.open(fp2);
            Red_box_sound = AudioSystem.getClip();
            URL url3 = this.getClass().getClassLoader().getResource("ping.wav");
            AudioInputStream fp3 = AudioSystem.getAudioInputStream(url3);
            Red_box_sound.open(fp3);
            Level_up = AudioSystem.getClip();
            URL url4 = this.getClass().getClassLoader().getResource("level-up.wav");
            AudioInputStream fp4 = AudioSystem.getAudioInputStream(url4);
            Level_up.open(fp4);
        }catch (LineUnavailableException var4) {
            var4.printStackTrace();
        } catch (UnsupportedAudioFileException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d = this.getSize();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint GradientPanel = new GradientPaint(0, 0, new Color(102, 103, 171), 0, d.height, new Color(224, 225, 255));
        g2.setPaint(GradientPanel);
        g2.fillRect(0, 0, d.width, d.height);
        Iterator it = obj.iterator();
        while (it.hasNext()) {
            Object_set c = (Object_set) it.next();
            c.draw(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_LEFT) {
            Iterator it = obj.iterator();
            while (it.hasNext()) {
                Object_set c = (Object_set) it.next();
                if (c instanceof Block_Obj) {
                    if (((Block_Obj) c).Move == false) {
                        ((Block_Obj) c).vx = -60;
                    }
                }
            }
        }
        if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
            Iterator it = obj.iterator();
            while (it.hasNext()) {
                Object_set c = (Object_set) it.next();
                if (c instanceof Block_Obj) {
                    if (((Block_Obj) c).Move == false) {
                        ((Block_Obj) c).vx = 60;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Iterator it = obj.iterator();
        while (it.hasNext()) {
            Object_set c = (Object_set) it.next();
            if (c instanceof Block_Obj) {
                if (((Block_Obj) c).Move == false) {
                    ((Block_Obj) c).vx = 0;
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(400);
            StartStage();
                while (true) {
                    try {
                    	time++;
                        for (Object_set o : obj)
                            o.update(0.33F);
                        repaint();
                        for (Object_set o1 : obj) {
                            if (o1 instanceof Ball_Obj) {
                                for (Object_set o2 : obj) {
                                    if (o1 == o2) continue;
                                    if (o2 instanceof Block_Obj) {
                                        o1.collisionDetect(o2);
                                        ball_num=0;
                                        block_num=0;
                                        Iterator it = obj.iterator();
                                        while (it.hasNext()) {
                                            Object_set c = (Object_set) it.next();
                                            if (c instanceof Ball_Obj) ball_num++;
                                            if (c instanceof Block_Obj) block_num++;
                                        }
                                        repaint();
                                    }
                                }
                            }
                        }
                        
                        Iterator it = obj.iterator();
                        while (it.hasNext()) {
                            Object_set c=(Object_set) it.next();
                            if (c.isDie()) {
                                if (c instanceof Block_Obj) {
                                    inn.cur_score += 10;
                                    block_num--;
                                }
                                if (c instanceof Ball_Obj) {
                                  
                                    ball_num--;
                                }
                                it.remove();
                            }
                        }
                        repaint();
                    }
                    catch(Exception e){

                    }
                    if (block_num == 1) {
                        Stage++;
                        Level_up.setFramePosition(0);
                        Level_up.start();
                        Thread.currentThread().sleep(300);
                        StartStage();
                    }
                    if (ball_num == 0) {
                        if(inn.cur_score>inn.Max_score){
                            inn.Max_score=inn.cur_score;
                        }
                        game_over.setFramePosition(0);
                        game_over.start();
                        while(game_over.getMicrosecondLength() != game_over.getMicrosecondPosition())
                        {
                        }
                        Thread.currentThread().interrupt();
                        inn.changePanel();
                    }
                    repaint();
                    Thread.sleep((30));
                }
        } catch (InterruptedException e) {

        }
    }
}
class EndingPage extends JPanel implements Runnable,KeyListener{
    int time;
    HW5 inn;
    Thread t;
    Clip Ending=null;
    EndingPage(HW5 in){
        inn=in;
        setFocusable(true);
        addKeyListener(this);
        t=new Thread(this);
        t.start();
        try {
            Ending = AudioSystem.getClip();
            URL url = this.getClass().getClassLoader().getResource("start_music.wav");
            AudioInputStream fp = AudioSystem.getAudioInputStream(url);
            Ending.open(fp);
            Ending.loop(-1);
        }catch (LineUnavailableException var4) {
            var4.printStackTrace();
        } catch (UnsupportedAudioFileException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }
    void WriteFont(Graphics2D g2,String tmp,Dimension d,Font newFont,float Y){
        FontMetrics fontMetrics = getFontMetrics(newFont);
        Rectangle stringBounds = fontMetrics.getStringBounds(tmp, g2).getBounds();
        int textX = (d.width - stringBounds.width) / 2;
        int textY = (int)((double)(d.height - stringBounds.height) / Y) + fontMetrics.getAscent();
        g2.setFont(newFont);
        g2.drawString(tmp,textX,textY);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d=this.getSize();
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint GradientPanel = new GradientPaint(0,0,new Color( 102, 103, 171),0,d.height,new Color(224, 225, 255));
        g2.setPaint(GradientPanel);
        g2.fillRect(0,0,d.width,d.height);
        Font newFont=new Font("Showcard Gothic",1,(int)(d.height*0.1));
        g2.setPaint(Color.decode("#ff533d"));
        FontMetrics fontMetrics = getFontMetrics(newFont);
        String tmp="Game Over";
        WriteFont(g2,tmp,d,newFont,6);
        g2.setPaint(Color.white);
        Font pressFont=new Font("Showcard Gothic",1,(int)(d.height*0.05));
        tmp="High Score : "+inn.Max_score;
        WriteFont(g2,tmp,d,pressFont,2.5F);
        tmp="Your Score : "+inn.cur_score;
        WriteFont(g2,tmp,d,pressFont,1.7F);
        if(time%2==0){
            g2.setPaint(Color.decode("#ff533d"));
            tmp="Press Spacebar to play!";
            Font EnterFont=new Font("Segoe Print",1,(int)(d.height*0.04));
            WriteFont(g2,tmp,d,EnterFont,1.3F);
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            Thread.currentThread().interrupt();
            Ending.stop();
            Ending.setFramePosition(0);
            inn.changePanel();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.time++;
            repaint();
        }
    }
}
public class HW5 extends JFrame{
    int Phase=0;
    JPanel cur_panel=null;
    int Max_score=0;
    int cur_score=0;
    HW5() {
        setTitle("Java HomeWork5");
        setSize(800, 800);
        setDefaultCloseOperation(3);
        cur_panel = new StartPage(this);
        add(cur_panel);
        setResizable(false);
        setVisible(true);
    }
    void changePanel() {
        this.remove(this.cur_panel);
        ++this.Phase;
        this.Phase %= 3;
        switch(this.Phase) {
            case 0:
                this.cur_panel = new StartPage(this);
                break;
            case 1:
                this.cur_panel = new GamePhase(this);
                break;
            case 2:
                this.cur_panel = new EndingPage(this);
        }

        this.add(this.cur_panel);
        this.cur_panel.requestFocus();
        this.revalidate();
        this.repaint();
    }
    public static void main(String[] args) {
        new HW5();
    }
}
