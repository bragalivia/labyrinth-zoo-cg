package org.yourorghere;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.TextureData;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class OpenglCG extends JFrame implements GLEventListener, MouseMotionListener,KeyListener {
    
    public List<Vertice> vertices = new ArrayList<Vertice>();
    private GLCanvas canvas;
    private GL gl;
    private GLU glu;
    private Animator animator;
    
    private Random random = new Random();
    Direcao direcaoMaca;

    private int larguraTela = 1000;
    private int alturaTela = 680;
    private BufferedImage image;
    private TextureData td;
    private ByteBuffer buffer;
    boolean cameraFora = false;
    boolean atualiza;

    private static int iMatriz = 16;
    private static int jMatriz = 15;
    int iMatrizElefanteInicial  = 1;
    int jMatrizElefanteInicial  = 1;
  
    static String[][]  matriz = new String[][]{{"P","P","P","P","P","P","P","P","P","P","P","P","P","P","P"},
                                                {"P","E","P","P"," "," ","P"," "," "," "," "," "," "," ","P"},
                                                {"P"," ","P","P"," "," ","P"," "," "," "," "," "," "," ","P"},
                                                {"P"," ","P","P"," "," ","P"," ","P"," "," "," ","M"," ","P"},
                                                {"P"," ","P","P"," "," ","P"," ","P"," "," "," "," "," ","P"},
                                                {"P"," ","P","P"," "," "," "," ","P"," "," "," "," "," ","P"},
                                                {"P"," ","P","P"," "," "," "," ","P","P","P","P","P","P","P"},
                                                {"P"," ","P","P"," "," ","P"," ","P","P","P","P","P","P","P"},
                                                {"P"," ","P","P"," "," ","P"," "," "," "," "," "," "," ","P"},
                                                {"P"," ","P","P"," "," ","P"," "," "," "," "," "," "," ","P"},
                                                {"P"," ","P","P"," "," ","P"," ","P","P","P","P","P","P","P"},
                                                {"P"," "," "," "," "," "," "," "," "," "," "," "," "," ","P"},
                                                {"P"," "," "," "," "," "," "," "," "," "," "," "," "," ","P"},
                                                {"P"," "," "," "," "," "," "," "," "," "," "," "," "," ","P"},
                                                {"P"," "," "," "," "," "," "," "," "," "," "," "," "," ","P"},
                                                {"P","P","P","P","P","P","P","P","P","P","P","P","P","P","P"}
                                                };
   

  
    TextRenderer renderer = new TextRenderer(new Font("TimesRoman", Font.BOLD, 30), false, false);

    long tempoInicial = System.currentTimeMillis();
    long tempoFinal = tempoInicial + 90000;
    long tempoAtual;
    String segundos = "90";

    float xTabuleiro = 1300;
    float zTabuleiro = -1500;
    float yTabuleiro = 30;

    boolean naoAndar = false;
    final int deslocamentoElefante = 80;
    Direcao direcaoElefante = Direcao.TRAS;
    int anguloElefante = 0;

    double xLocalElefante = xTabuleiro-100;
    double yLocalElefante = yTabuleiro+1;
    double zLocalElefante = zTabuleiro+100;
    
    double olhoXcameraInterna = xTabuleiro/2;
    double olhoZcameraInterna = zTabuleiro/2;
    double centroXcameraInterna = xTabuleiro/2;
    double centroZcameraInterna = 200;

    int glShadeModel = GL.GL_SMOOTH;

 public OpenglCG() {

	super("Zoo");

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addKeyListener(this);
        add(canvas, BorderLayout.CENTER);

        glu = new GLU();

        animator = new Animator(canvas);
 	this.addKeyListener(this);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        this.setSize(1000, 680);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        animator.start();

    }

    public static void main(String[] args) {
        new OpenglCG();
    }

    public void init(GLAutoDrawable drawable) {
        this.gl = drawable.getGL();
        MP3 mp3 = new MP3(new File("src/mario.mp3"));
        mp3.play();

        //cor do fundo da tela
        gl.glClearColor(0.0f, 0.0f, 0.0f, .0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glShadeModel(GL.GL_SMOOTH);

        //luz
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_LIGHTING);

        FloatBuffer light_pos = FloatBuffer.wrap(new float[]{xTabuleiro / 2, yTabuleiro * 2, zTabuleiro / 2, 1f});
        FloatBuffer light_Ka = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1f});
        FloatBuffer light_Kd = FloatBuffer.wrap(new float[]{0.6f, 0.6f, 0.5f, 1f});
        FloatBuffer light_Ks = FloatBuffer.wrap(new float[]{0.6f, 0.6f, 0.5f, 1f});

        gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, light_pos);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, light_Ka);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, light_Kd);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, light_Ks);
    }


    public void reshape(GLAutoDrawable drawable, int x, int y, int larguraTela, int alturaTela) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(80, (float) larguraTela / (float) alturaTela, 1, 100000);
    }

    public void display(GLAutoDrawable drawable) {

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        if(cameraFora){
            glu.gluLookAt(300, yTabuleiro+1200, 500,
                      xTabuleiro, -100, zTabuleiro,
                      0, 1, 0);
        }else{
            glu.gluLookAt(xLocalElefante, yLocalElefante+250, zLocalElefante,
                            centroXcameraInterna-20, 0, centroZcameraInterna-50, 0, 1, 0);
            cameraFora = false;
        }

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

	criarParedes(gl);

        //tabuleiro
        vertices.add(new Vertice(0f, 0f, 0, true));
        vertices.add(new Vertice(0f, 0f, zTabuleiro));
        vertices.add(new Vertice(xTabuleiro, 0f, zTabuleiro));
        vertices.add(new Vertice(xTabuleiro, 0f, 0));

        vertices.add(new Vertice(0f, yTabuleiro, 0, true, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, yTabuleiro, 0, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, yTabuleiro, zTabuleiro, 0, 0, 0));
        vertices.add(new Vertice(0f, yTabuleiro, zTabuleiro, 0, 0, 0));

        vertices.add(new Vertice(0f, 0, 0, true, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, 0, 0, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, yTabuleiro, 0, 0, 0, 0));
        vertices.add(new Vertice(0f, yTabuleiro, 0, 0, 0, 0));

        vertices.add(new Vertice(0f, 0, zTabuleiro, true, 0, 0, 0));
        vertices.add(new Vertice(0f, yTabuleiro, zTabuleiro, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, yTabuleiro, zTabuleiro, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, 0, zTabuleiro, 0, 0, 0));

        vertices.add(new Vertice(xTabuleiro, 0, 0, true, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, 0, zTabuleiro, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, yTabuleiro, zTabuleiro, 0, 0, 0));
        vertices.add(new Vertice(xTabuleiro, yTabuleiro, 0, 0, 0, 0));

        vertices.add(new Vertice(0, 0, 0, true, 0, 0, 0));
        vertices.add(new Vertice(0, yTabuleiro, 0, 0, 0, 0));
        vertices.add(new Vertice(0, yTabuleiro, zTabuleiro, 0, 0, 0));
        vertices.add(new Vertice(0, 0, zTabuleiro, 0, 0, 0));

        int qtdeVertices = 0;
        for (Vertice vertice : vertices) {
            if(vertice.isOrigem()) {
                gl.glTranslated(0, 0, 0);
            }

            if(qtdeVertices++ == 0) {
                gl.glBegin(GL.GL_QUADS);
            }
            
            gl.glVertex3f(vertice.getX(), vertice.getY(), vertice.getZ());

            if(qtdeVertices == 4) {
                gl.glEnd();
                qtdeVertices = 0;
            }
        }

        gl.glFlush();


        //Material pro elefante
        FloatBuffer material_KaElefante = FloatBuffer.wrap(new float[]{0.6f, 0.0f, 0.0f, 1.00f});
        FloatBuffer material_KdElefante = FloatBuffer.wrap(new float[]{0.221f, 0.19f, 0.123f, 1.00f});//cor
        FloatBuffer material_KsElefante = FloatBuffer.wrap(new float[]{0.33f, 0.33f, 0.52f, 1.00f});
        FloatBuffer material_KeElefante = FloatBuffer.wrap(new float[]{0.00f, 0.00f, 0.00f, 0.00f});
        FloatBuffer material_SeElefante = FloatBuffer.wrap(new float[]{126f});

        gl.glMaterialfv(gl.GL_FRONT, gl.GL_AMBIENT, material_KaElefante);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_DIFFUSE, material_KdElefante);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SPECULAR, material_KsElefante);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_EMISSION, material_KeElefante);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SHININESS, material_SeElefante);

        if(cameraFora){
            CarregaOBJ objModel = new CarregaOBJ("src/elephal.obj");
            gl.glPushMatrix();

            gl.glTranslated(0, 0, 0);
            gl.glTranslated(xLocalElefante, yLocalElefante , zLocalElefante);
            gl.glRotated(anguloElefante, 0, 1, 0);
            gl.glScalef(0.2f, 0.2f, 0.2f);
            objModel.DrawModel(gl);

            gl.glPopMatrix();
        }

        //Material Maca
        FloatBuffer material_KaCabeca = FloatBuffer.wrap(new float[]{ 1.0f, 0.0f, 0.0f, 1.00f });//cor
        FloatBuffer material_KdCabeca = FloatBuffer.wrap(new float[]{ 0.43f, 0.47f, 0.54f, 1.00f });
        FloatBuffer material_KsCabeca = FloatBuffer.wrap(new float[]{ 0.33f, 0.33f, 0.52f, 1.00f });
        FloatBuffer material_KeCabeca = FloatBuffer.wrap(new float[]{ 0.00f, 0.00f, 0.00f, 0.00f });
        FloatBuffer material_SeCabeca = FloatBuffer.wrap(new float[]{ 126f });

        gl.glMaterialfv(gl.GL_FRONT, gl.GL_AMBIENT, material_KaCabeca);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_DIFFUSE, material_KdCabeca);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SPECULAR, material_KsCabeca);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_EMISSION, material_KeCabeca);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SHININESS, material_SeCabeca);

        CarregaOBJ objModel2 = new CarregaOBJ("src/maca.obj");

        gl.glPushMatrix();
        gl.glTranslated(0, 0, 0);

        int direcaoRadomica = random.nextInt(4);

        double xMaca = xTabuleiro;
        double zMaca = zTabuleiro;
        int iMatrizMacaInicial = 3;
        int jMatrizMacaInicial = 12;

        //maca randomico
        switch(direcaoRadomica) {
            case 0:
                zMaca = zTabuleiro - deslocamentoElefante;
                direcaoMaca = Direcao.FRENTE;
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial+1 ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial-1 ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial+1 ] = " ";
                matriz[iMatrizMacaInicial-1 ][jMatrizMacaInicial ] = "M";
                break;

            case 1:
                zMaca = zTabuleiro + deslocamentoElefante;
                direcaoMaca = Direcao.TRAS;
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial-1 ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial-1 ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial+1 ] = " ";
                matriz[iMatrizMacaInicial+1 ][jMatrizMacaInicial ] = "M";
                break;

            case 2:
                xMaca = xTabuleiro - deslocamentoElefante;
                direcaoMaca = Direcao.ESQUERDA;
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial-1 ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial+1 ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial+1 ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial-1 ] = "M";
                break;

            case 3:
                xMaca = xTabuleiro + deslocamentoElefante;
                direcaoMaca = Direcao.DIREITA;
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial-1 ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial-1 ] = " ";
                matriz[iMatrizMacaInicial+1 ][jMatrizMacaInicial ] = " ";
                matriz[iMatrizMacaInicial ][jMatrizMacaInicial+1 ] = "M";
                break;
           }

        gl.glTranslated(xMaca-1000, yTabuleiro+1, zMaca+450);

        objModel2.DrawModel(gl);
        gl.glPopMatrix();

        renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        iniciaCronometro();

        renderer.setColor(1.0f, 0.0f, 0.0f, 0.8f);
        if(segundos.length()==5){
        renderer.draw("Tempo: "+segundos.substring(0, 2), 50, 590);
        renderer.endRendering();
        }
        if(segundos.length()==4){
           renderer.draw("Tempo: "+segundos.substring(0, 1), 50, 590);
            renderer.endRendering();
        } if(segundos.length()==3){
             JOptionPane.showMessageDialog(this, "Seu tempo acabou!!! Você Perdeu!!!!");
             System.exit(0);
        }
    }

    public String iniciaCronometro(){
       
        String auxAnterior = null;
            while(tempoInicial<tempoFinal) {
            int i=0;
            tempoAtual = System.currentTimeMillis();
            tempoInicial = tempoAtual;
            tempoAtual = tempoFinal - tempoInicial;
            if((tempoAtual+"").length()==6) {
            i=3;
            }
            if((tempoAtual+"").length()==5) {
            i=2;
            }
            if((tempoAtual+"").length()==4) {
            i=1;
            }
            String aux = tempoAtual+"";
            if(auxAnterior!=null) {
            if(!aux.substring(0,i).equalsIgnoreCase(auxAnterior.substring(0,i))) {
            auxAnterior= aux;
//            System.out.println(aux.substring(0,i));
            }

            } else {
//            System.out.println(aux.substring(0,i));
            }
            auxAnterior = aux;
            segundos = aux;
            return segundos;
        }
        segundos = "0";
        return segundos;
        
        
    }
  
   

    private void seguirFrente() {
        atualiza = true;
         if(atualiza){
             atualizaElefanteMatriz();
             atualiza = false;
             for (int i = 0; i < iMatriz; i++) {
                System.out.println();
                for (int j = 0; j < jMatriz; j++) {

                System.out.print(matriz[i][j]);
                }
            }
        }
        if(!naoAndar){
            switch(direcaoElefante) {

                case FRENTE:
                    if(zLocalElefante-deslocamentoElefante > zTabuleiro+100)
                    zLocalElefante -= deslocamentoElefante;
                    olhoZcameraInterna -= deslocamentoElefante;
                    centroZcameraInterna -= deslocamentoElefante;

                    break;

                case TRAS:
                    if(zLocalElefante+deslocamentoElefante < 100)
                    zLocalElefante += deslocamentoElefante;
                    olhoZcameraInterna += deslocamentoElefante;
                    centroZcameraInterna += deslocamentoElefante;
                    break;

                case DIREITA:
                    if(xLocalElefante+deslocamentoElefante < xTabuleiro+100)
                    xLocalElefante += deslocamentoElefante;
                    olhoXcameraInterna += deslocamentoElefante;
                    centroXcameraInterna += deslocamentoElefante;
                    break;

                case ESQUERDA:
                    if(xLocalElefante-deslocamentoElefante > 100)
                    xLocalElefante -= deslocamentoElefante;
                    olhoXcameraInterna -= deslocamentoElefante;
                    centroXcameraInterna -= deslocamentoElefante;
                    break;
            }

        }
    }

    private void seguirAtras() {
        atualiza = true;
        switch(direcaoElefante) {
            case FRENTE:
                anguloElefante = 0;
                direcaoElefante = Direcao.TRAS;
                centroZcameraInterna = 300;
                break;

            case TRAS:
                anguloElefante = 180;
                direcaoElefante = Direcao.FRENTE;
                centroZcameraInterna = zTabuleiro - 300;
                break;

            case DIREITA:
                anguloElefante = 270;
                direcaoElefante = Direcao.ESQUERDA;
                centroXcameraInterna = -300;
                break;

            case ESQUERDA:
                anguloElefante = 90;
                direcaoElefante = Direcao.DIREITA;
                centroXcameraInterna = xTabuleiro + 300;
                break;
        }
    }

       private void seguirDireita() {
        atualiza = true;
        switch(direcaoElefante) {
            case FRENTE:
                direcaoElefante = Direcao.DIREITA;
		centroXcameraInterna = xTabuleiro + 300;
                centroZcameraInterna = zLocalElefante;
                break;

            case DIREITA:
                direcaoElefante = Direcao.TRAS;
		centroXcameraInterna = xLocalElefante;
                centroZcameraInterna = 300;
                break;

            case TRAS:
                direcaoElefante = Direcao.ESQUERDA;
		centroXcameraInterna = -300;
                centroZcameraInterna = zLocalElefante;
                break;

            case ESQUERDA:
                direcaoElefante = Direcao.FRENTE;
 		centroXcameraInterna = xLocalElefante;
                centroZcameraInterna = zTabuleiro-300;
                break;
        }

        anguloElefante += 270;

    }

    private void seguirEsquerda() {
        atualiza = true;
        switch(direcaoElefante) {
            case FRENTE:
                direcaoElefante = Direcao.ESQUERDA;
		centroXcameraInterna = -300;
                centroZcameraInterna = zLocalElefante;
                break;

            case ESQUERDA:
                direcaoElefante = Direcao.TRAS;
 		centroXcameraInterna = xLocalElefante;
                centroZcameraInterna = 300;
                break;

            case TRAS:
                direcaoElefante = Direcao.DIREITA;
                centroXcameraInterna = xTabuleiro+300;
                centroZcameraInterna = zLocalElefante;
                break;

            case DIREITA:
                direcaoElefante = Direcao.FRENTE;
 		centroXcameraInterna = xLocalElefante;
                centroZcameraInterna = zTabuleiro -300;
                break;
        }

        anguloElefante += 90;

    }
    
    private void atualizaElefanteMatriz(){
       if(direcaoElefante.toString().equals("FRENTE")){
           if(matriz[iMatrizElefanteInicial -1][jMatrizElefanteInicial ].equals(" ")){
                matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = " ";
                iMatrizElefanteInicial --;
                matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = "E";
                naoAndar = false;
           }else{
               if(matriz[iMatrizElefanteInicial -1][jMatrizElefanteInicial ].equals("M")){
                   MP3 mp3 = new MP3(new File("src/elefante.mp3"));
                   mp3.play();
                   JOptionPane.showMessageDialog(this, "Você Ganhou!!!!");
                   System.exit(0);
                   naoAndar = true;
               }else{
                   naoAndar = true;
               }
               
           }
       }
       if(direcaoElefante.toString().equals("TRAS")){
            if(matriz[iMatrizElefanteInicial +1][jMatrizElefanteInicial ].equals(" ")){
                matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = " ";
                iMatrizElefanteInicial ++;
                matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = "E";
                naoAndar = false;
            }else{
                if(matriz[iMatrizElefanteInicial +1][jMatrizElefanteInicial ].equals("M")){
                   File mp3File = new File("src/elefante.mp3");
                    MP3 mp3 = new MP3(mp3File);
                    mp3.play();
                   JOptionPane.showMessageDialog(this, "Você Ganhou!!!!");
                   System.exit(0);
                   naoAndar = true;
               }else{
                   naoAndar = true;
               }

            }
       }
       if(direcaoElefante.toString().equals("DIREITA")){
            if(matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial -1].equals(" ")){
                matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = " ";
                jMatrizElefanteInicial --;
                matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = "E";
                naoAndar = false;
            }else{
                 if(matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial -1].equals("M")){
                   MP3 mp3 = new MP3(new File("src/elefante.mp3"));
                   mp3.play();
                   JOptionPane.showMessageDialog(this, "Você Ganhou!!!!");
                   System.exit(0);
                   naoAndar = true;
               }else{
                   naoAndar = true;
               }

            }

       }
        if(direcaoElefante.toString().equals("ESQUERDA")){
             if(matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial +1].equals(" ")){
          matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = " ";
          jMatrizElefanteInicial ++;
          matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial ] = "E";
            naoAndar = false;
             }else{
                 if(matriz[iMatrizElefanteInicial ][jMatrizElefanteInicial +1].equals("M")){
                   MP3 mp3 = new MP3(new File("src/elefante.mp3"));
                   mp3.play();
                   JOptionPane.showMessageDialog(this, "Você Ganhou!!!!");
                   System.exit(0);
                   naoAndar = true;
               }else{
                   naoAndar = true;
               }
            }
       }
    }


 

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 38) {
            seguirFrente();
        }

        if(e.getKeyCode() == 40) {
            seguirAtras();
        }

        if(e.getKeyCode() == 37) {
            seguirEsquerda();
        }

        if(e.getKeyCode() == 39) {
            seguirDireita();
        }

        if(e.getKeyCode() == 70) { //letra F (fora)
            cameraFora = true;
        }

       if(e.getKeyCode() == 68) {//letra D (dentro)
            cameraFora = false;
        }

    }

    public void criarParedes(GL gl) {
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DOUBLEBUFFER);
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        
        FloatBuffer material_Ka = FloatBuffer.wrap(new float[]{1, 1, 1, 1});
        FloatBuffer material_Kd = FloatBuffer.wrap(new float[]{1, 1, 1, 1});
        FloatBuffer material_Ks = FloatBuffer.wrap(new float[]{1, 1, 1, 1});
        FloatBuffer material_Ke = FloatBuffer.wrap(new float[]{5, 1, 1, 0});
        FloatBuffer material_Se = FloatBuffer.wrap(new float[]{126f});
        
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_AMBIENT, material_Ka);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_DIFFUSE, material_Kd);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SPECULAR, material_Ks);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_EMISSION, material_Ke);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SHININESS, material_Se);

	loadImage("src/grade.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);
 
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
//        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
//        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
//        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);

        
        //parede 1
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0, 0.0f);
        gl.glTexCoord2f(0.0f, 10.0f);
        gl.glVertex3f(xTabuleiro, 0, 0.0f);
        gl.glTexCoord2f(10.0f, 10.0f);
        gl.glVertex3f(xTabuleiro, yTabuleiro+500, 0);
        gl.glTexCoord2f(10.0f, 0.0f);
        gl.glVertex3f(0.0f, yTabuleiro+500, 0);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);


        //parede 2
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, 0, zTabuleiro);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(0, yTabuleiro+500, zTabuleiro);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro, yTabuleiro+500, zTabuleiro);
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(xTabuleiro, 0,zTabuleiro );

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //parede 3
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro, 0, 0);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro, 0, zTabuleiro);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro, yTabuleiro+500, zTabuleiro);
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(xTabuleiro, yTabuleiro+500, 0 );

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //parede 4
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0, 0);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(0, yTabuleiro+500, 0);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(0, yTabuleiro+500, zTabuleiro);
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(0, 0,  zTabuleiro);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //parede 5
        loadImage("src/palha.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro - 220, 0, zTabuleiro+850);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro- 220, yTabuleiro+500, zTabuleiro+850);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro- 220, yTabuleiro+500,zTabuleiro );
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro- 220, 0,  zTabuleiro);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //parede 6
        loadImage("src/palha.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro - 500, 0, zTabuleiro+400);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro- 500, yTabuleiro+500, zTabuleiro+400);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro- 500, yTabuleiro+500,zTabuleiro );
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(xTabuleiro- 500, 0,  zTabuleiro);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //parede 7
        loadImage("src/palha.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro - 500, 0, zTabuleiro+850);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro- 500, yTabuleiro+500, zTabuleiro+850);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro- 500, yTabuleiro+500,zTabuleiro+600 );
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(xTabuleiro- 500, 0,  zTabuleiro + 600);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //parede 8
        loadImage("src/palha.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro - 750, 0, zTabuleiro+600);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro- 750, yTabuleiro+500, zTabuleiro+600);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro- 750, yTabuleiro+500,zTabuleiro+300 );
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(xTabuleiro- 750, 0,  zTabuleiro + 300);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);


         //parede 9
        loadImage("src/palha.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro - 750, 0, zTabuleiro+600);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro- 750, yTabuleiro+500, zTabuleiro+600);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(0 , yTabuleiro+500,zTabuleiro+600 );
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(0, 0,  zTabuleiro+600);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

         //parede 10
        loadImage("src/palha.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);

        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(xTabuleiro - 750, 0, zTabuleiro+800);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro- 750, yTabuleiro+500, zTabuleiro+800);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(0 , yTabuleiro+500,zTabuleiro+800 );
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(0, 0,  zTabuleiro+800);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);

        //piso
        loadImage("src/grama_sintetica.jpg");
        gl.glEnable(GL.GL_TEXTURE_2D);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);

        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, yTabuleiro, 0.0f);
        gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro, yTabuleiro, 0.0f);
        gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro, yTabuleiro, zTabuleiro);
        gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(0.0f, yTabuleiro, zTabuleiro);

        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_TEXTURE_2D);


        //ceu
//        loadImage("src/ceu.jpg");
//        gl.glEnable(GL.GL_TEXTURE_2D);
//
//        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, larguraTela, alturaTela, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);
//        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
//        gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
//
//         gl.glEnable(GL.GL_TEXTURE_2D);
//        gl.glPushMatrix();
//            gl.glBegin(GL.GL_QUADS);
//                gl.glNormal3f(0.0f, 1.0f, 0.0f);
//                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.0f, yTabuleiro+500, 0.0f);
//                gl.glTexCoord2f(0.0f, 10.0f); gl.glVertex3f(xTabuleiro, yTabuleiro+500, 0.0f);
//                gl.glTexCoord2f(10.0f, 10.0f); gl.glVertex3f(xTabuleiro, yTabuleiro+500, -zTabuleiro);
//                gl.glTexCoord2f(10.0f, 0.0f); gl.glVertex3f(0.0f, yTabuleiro+500, -zTabuleiro);
//
//            gl.glEnd();
//        gl.glPopMatrix();
//        gl.glDisable(GL.GL_TEXTURE_2D);

    }

    public void loadImage(String fileName){
        image = null;
        try {
                image = ImageIO.read(new File(fileName));
        }
        catch (IOException e) {
                e.printStackTrace();
        }

        larguraTela  = image.getWidth();
        alturaTela = image.getHeight();
        td = new TextureData(0,0,false,image);
        buffer = (ByteBuffer) td.getBuffer();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

}