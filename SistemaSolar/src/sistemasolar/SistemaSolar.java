
package sistemasolar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class SistemaSolar extends JPanel implements ActionListener {

    // Constantes físicas y escala
    private final double G = 6.67428e-11; // Constante gravitacional
    private final double dt = 3600*24 ; // Paso de tiempo (1 día en segundos)
    private final double unidadAstro = 1.496e11 ; // 1 Unidad Astronómica (UA)
    private final float escala = 250 / (float) unidadAstro; // Factor de escala
    private final int WIDTH = 1100, HEIGHT = 900;
    
    double distanciaTierra = unidadAstro; // 1 UA
    double distanciaMarte = 1.524 * unidadAstro; // 1.524 UA
    double distanciaMercurio = 0.387 * unidadAstro; // 0.387 UA
    double distanciaVenus = 0.723 * unidadAstro;

    // Listas para los cuerpos celestes
    private final ArrayList<CuerpoCeleste> planetas = new ArrayList<>();
    private final CuerpoCeleste sol;

    private final Timer timer;

    public SistemaSolar() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);

        // Crear el Sol
        sol = new CuerpoCeleste(WIDTH/2, HEIGHT/2, 1.9890e30, 30, "/img/sol.png");

        // Crear planetas con sus posiciones y masas
        planetas.add(new CuerpoCeleste((float)(distanciaTierra * escala) + WIDTH/2, HEIGHT/2, 5.974e24, 14, "/img/tierra.png")); // Tierra
        planetas.add(new CuerpoCeleste((float)(distanciaMarte * escala) + WIDTH/2, HEIGHT/2, 6.419e23, 16, "/img/marte.png")); // Marte
        planetas.add(new CuerpoCeleste((float)(distanciaMercurio * escala) + WIDTH/2, HEIGHT/2, 3.302e23, 10, "/img/mercurio.png")); // Mercurio
        planetas.add(new CuerpoCeleste((float)(distanciaVenus * escala) + WIDTH/2, HEIGHT/2, 4.869e24, 12, "/img/venus.png")); // Venus

        // Inicializar velocidades para simular órbitas
        for (CuerpoCeleste planeta : planetas) {
            double distanciaReal = (planeta.x - WIDTH / 2) / escala;
            double velocidadOrbital = Math.sqrt(G * sol.masa / distanciaReal);
            planeta.vx = 0;
            planeta.vy = (float) (velocidadOrbital * escala);
        }


        // Configurar el Timer para actualizar la simulación
        timer = new Timer(10, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dibujar el Sol
        sol.dibujar(g2);

        // Dibujar los planetas y actualizar sus posiciones
        for (CuerpoCeleste planeta : planetas) {
            planeta.dibujar(g2);
        }

        // Dibujar información de velocidades
        g2.setColor(Color.WHITE);
        for (CuerpoCeleste planeta : planetas) {
            String velocidadTexto = String.format("Vel: %.2f m/s", planeta.getVelocidadReal(escala));
            g2.drawString(velocidadTexto, (int) planeta.x + 15, (int) planeta.y - 15);
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
            double distancia = Math.sqrt(dx * dx + dy * dy);

            double fuerza = (G * sol.masa * planeta.masa) / Math.pow(distancia/escala, 2);
            double ax = -fuerza * (dx/distancia) / planeta.masa;
            double ay = -fuerza * (dy/distancia) / planeta.masa;

            planeta.vx += ax * dt * escala;
            planeta.vy += ay * dt * escala;
            planeta.x += planeta.vx * dt;
            planeta.y += planeta.vy * dt;
        }
    }

    // Clase para representar los cuerpos celestes
    static class CuerpoCeleste {
        float x, y;
        float vx, vy;
        double masa;
        int radio;
        Color color;
        Image imagen;

        public CuerpoCeleste(float x, float y, double masa, int radio, String rutaImagen) {
            this.x = x;
            this.y = y;
            this.masa = masa;
            this.radio = radio;
            //this.color = color;
            this.imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
            
            this.vx = 0;
            this.vy = 0;
        }

        public void dibujar(Graphics2D g2) {
            g2.drawImage(imagen, (int)(x - radio), (int)(y - radio), radio * 2, radio * 2, null);
            //g2.setColor(color);
            //g2.fillOval((int) (x - radio), (int) (y - radio), radio * 2, radio * 2);
        }

        public double getVelocidadReal(float escala) {
            return Math.sqrt(vx * vx + vy * vy)/escala;
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
