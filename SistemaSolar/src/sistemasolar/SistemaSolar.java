
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
    private final double dt = 3600*24 ; // Paso de tiempo (1 día en segundos)
    private final double unidadAstro = 1.496e11 ; // 1 Unidad Astronómica (UA)
    private final float escala = 250 / (float) unidadAstro; // Factor de escala
    private final int WIDTH = 1100, HEIGHT = 900;
    
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

        // Crear el Sol
        sol = new CuerpoCeleste(WIDTH/2, HEIGHT/2, 1.9890e30, 30, "/img/sol.png");

        // Crear planetas con sus posiciones y masas
        planetas.add(new CuerpoCeleste((float)(distanciaTierra * escala) + WIDTH/2, HEIGHT/2, 5.974e24, 14, "/img/tierra.png")); // Tierra
        planetas.add(new CuerpoCeleste((float)(distanciaMarte * escala) + WIDTH/2, HEIGHT/2, 6.419e23, 16, "/img/marte.png")); // Marte
        planetas.add(new CuerpoCeleste((float)(distanciaMercurio * escala) + WIDTH/2, HEIGHT/2, 3.30e23, 10, "/img/mercurio.png")); // Mercurio
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
        g2.setFont(new Font("Times New Roman", Font.PLAIN,18));

        // Dibujar el Sol
        sol.dibujar(g2);

        // Dibujar los planetas y actualizar sus posiciones
        for (CuerpoCeleste planeta : planetas) {
            planeta.dibujar(g2);
        }

        // Dibujar información de velocidades
        g2.setColor(Color.WHITE);
        for (CuerpoCeleste planeta : planetas) {
            double dx = planeta.x - sol.x;
            double dy = planeta.y - sol.y;
            
            String velocidadTexto = String.format("V: %.2f m/s", planeta.getVelocidadReal(escala));
            String fuerzaTexto = String.format("F: %.0f N", planeta.getFuerzaReal(G, dx, dy, planeta.masa, sol.masa, escala));
            g2.drawString(velocidadTexto, (int) planeta.x + 15, (int) planeta.y - 15);
            g2.drawString(fuerzaTexto, (int) planeta.x + 15, (int) planeta.y - 38);
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
            double ax = -fuerza * (dx/distancia) / planeta.masa;
            double ay = -fuerza * (dy/distancia) / planeta.masa;

            // Actualizar velocidades
            planeta.vx += ax * dt * escala;
            planeta.vy += ay * dt * escala;
            
            // Actualizar posiciones
            planeta.x += planeta.vx * dt;
            planeta.y += planeta.vy * dt;
        }
    }

    static class CuerpoCeleste {
        float x, y;
        float vx, vy;
        double masa;
        int radio;
        Image imagen;

        public CuerpoCeleste(float x, float y, double masa, int radio, String rutaImagen) {
            this.x = x;
            this.y = y;
            this.masa = masa;
            this.radio = radio;
            this.imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
            
            this.vx = 0;
            this.vy = 0;
        }

        public void dibujar(Graphics2D g2) {
            g2.drawImage(imagen, (int)(x - radio), (int)(y - radio), radio * 2, radio * 2, null);
        }

        public double getVelocidadReal(float escala) {
            return Math.sqrt(vx * vx + vy * vy)/escala;
        }
        
        public double getFuerzaReal(double G, double dx, double dy, double masa, double solMasa, float escala) {
            double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r
            return (G * solMasa * masa) / Math.pow(distancia/escala, 2);
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
