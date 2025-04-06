package sistemasolar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class SistemaSolar extends JPanel implements ActionListener {

    // Constantes y escala
    private final double G = 6.67428e-11; // Constante gravitacional
    private final double dt = 3600 * 24; // Paso de tiempo (1 día en segundos)
    private final double unidadAstro = 1.496e11; // 1 Unidad Astronómica (UA)
    private final float escala = 200 / (float) unidadAstro; // Factor de escala
    private final int WIDTH = 1200, HEIGHT = 900;

    double distanciaTierra = unidadAstro; // 1 UA
    double distanciaMarte = 1.524 * unidadAstro; // 1.524 UA
    double distanciaMercurio = 0.387 * unidadAstro; // 0.387 UA
    double distanciaVenus = 0.723 * unidadAstro; // 0.723 UA

    // Listas para los cuerpos celestes
    private final ArrayList<CuerpoCeleste> planetas = new ArrayList<>();
    private final CuerpoCeleste sol;

    private final Timer timer;

    public SistemaSolar() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);

        sol = new CuerpoCeleste(WIDTH / 2, HEIGHT / 2, 1.9890e30, 30, 0, 0, "/img/sol.png");

        // Se crean los objetos planetas
        //Posición en x diferente a solo WIDTH/2, para que en la simulación partan desde diferentes puntos y resalte más la orbita de cada uno
        float dxTierra = (float) (Math.cos(Math.toRadians(30)) * distanciaTierra * escala);
        float dyTierra = (float) (Math.sin(Math.toRadians(30)) * distanciaTierra * escala);
        planetas.add(new CuerpoCeleste(WIDTH / 2 + dxTierra, HEIGHT / 2 - dyTierra, 5.974e24, 14, -10e3 * escala, -29.783e3 * escala, "/img/tierra.png"));

        float dxMarte = (float) (Math.cos(Math.toRadians(30)) * distanciaMarte * escala);
        float dyMarte = (float) (Math.sin(Math.toRadians(30)) * distanciaMarte * escala);
        planetas.add(new CuerpoCeleste(WIDTH / 2 + dxMarte, HEIGHT / 2 - dyMarte, 6.419e23, 16, -13e3 * escala, -24.017e3 * escala, "/img/marte.png"));

        float dxMercurio = (float) (Math.cos(Math.toRadians(60)) * distanciaMercurio * escala);
        float dyMercurio = (float) (Math.sin(Math.toRadians(60)) * distanciaMercurio * escala);
        planetas.add(new CuerpoCeleste(WIDTH / 2 + dxMercurio, HEIGHT / 2 - dyMercurio, 3.302e23, 10, -25e3 * escala, -47.4e3 * escala, "/img/mercurio.png"));

        float dxVenus = (float) (Math.cos(Math.toRadians(45)) * distanciaVenus * escala);
        float dyVenus = (float) (Math.sin(Math.toRadians(45)) * distanciaVenus * escala);
        planetas.add(new CuerpoCeleste(WIDTH / 2 + dxVenus, HEIGHT / 2 - dyVenus, 4.869e24, 12, -15e3 * escala, -35.02e3 * escala, "/img/venus.png"));

        // Timer para actualizar la simulación
        timer = new Timer(10, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        sol.dibujar(g2);

        // Dibujar los planetas y actualizar sus posiciones
        for (CuerpoCeleste planeta : planetas) {
            planeta.dibujar(g2);
        }

        // Se añade la información de velocidades y fuerza por planeta
        g2.setColor(Color.WHITE);
        //ArrayList<Point> A = new ArrayList<Point>();
        for (CuerpoCeleste planeta : planetas) {
            double dx = planeta.x - sol.x;
            double dy = planeta.y - sol.y;

            String velocidadTexto = String.format("V: %.2f m/s", planeta.getVelocidadReal(escala));
            String fuerzaTexto = String.format("F: %.0f N", planeta.getFuerzaReal(G, dx, dy, planeta.masa, sol.masa, escala));
            g2.drawString(velocidadTexto, (int) planeta.x + 15, (int) planeta.y - 15);
            g2.drawString(fuerzaTexto, (int) planeta.x + 15, (int) planeta.y - 38);

            /**
             * A.add(new Point((int) planeta.x, (int) planeta.y)); if (A.size()
             * > 1) { //g2.drawRect(A.get(A.size() - 1).x, A.get(A.size() -
             * 1).y, 1, 1); g2.drawLine(A.get(A.size() - 1).x, A.get(A.size() -
             * 1).y, A.get(A.size() - 2).x, A.get(A.size() - 2).y);
            }
             */
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actualizarPosiciones();
        repaint();
    }

    private void actualizarPosiciones() {
        for (CuerpoCeleste planeta : planetas) {
            double dx = planeta.x - sol.x;
            double dy = planeta.y - sol.y;
            double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r

            double fuerza = planeta.getFuerzaReal(G, dx, dy, planeta.masa, sol.masa, escala);
            double ax = -fuerza * (dx / distancia) / planeta.masa;
            double ay = -fuerza * (dy / distancia) / planeta.masa;

            // Actualizar velocidades
            planeta.vx += ax * dt * escala;
            planeta.vy += ay * dt * escala;

            // Actualizar posiciones
            planeta.x += planeta.vx * dt;
            planeta.y += planeta.vy * dt;
            planeta.trayectoria.add(new Point((int) planeta.x, (int) planeta.y));
            if (planeta.trayectoria.size() > 2000) { // Para evitar sobreposición de lineas
                planeta.trayectoria.remove(0);
            }

        }
    }

    static class CuerpoCeleste {

        float x, y;
        double vx, vy;
        double masa;
        int radio;
        Image imagen;
        ArrayList<Point> trayectoria;

        public CuerpoCeleste(float x, float y, double masa, int radio, double vx, double vy, String rutaImagen) {
            this.x = x;
            this.y = y;
            this.masa = masa;
            this.radio = radio;
            this.imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();

            this.vx = vx;
            this.vy = vy;
            this.trayectoria = new ArrayList<>();
        }

        public void dibujar(Graphics2D g2) {
            g2.setColor(Color.GRAY);
            for (int i = 1; i < trayectoria.size(); i++) {
                Point p1 = trayectoria.get(i - 1);
                Point p2 = trayectoria.get(i);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            g2.drawImage(imagen, (int) (x - radio), (int) (y - radio), radio * 2, radio * 2, null);

        }

        public double getVelocidadReal(float escala) {
            return Math.sqrt(vx * vx + vy * vy) / escala;
        }

        public double getFuerzaReal(double G, double dx, double dy, double masa, double solMasa, float escala) {
            double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r
            return (G * solMasa * masa) / Math.pow(distancia / escala, 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema Solar");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new SistemaSolar());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
