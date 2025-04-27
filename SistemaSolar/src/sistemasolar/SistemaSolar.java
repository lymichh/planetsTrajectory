package sistemasolar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

/**
 * Simulación visual del sistema solar en 2D. Esta clase contiene el panel
 * principal donde se renderizan el Sol y los planetas, así como la lógica de
 * movimiento basada en la gravedad newtoniana.
 *
 * @author Kesly Rodríguez, Joshua Lobo, Alexy Salcedo y Santiago Vengoechea
 */
public class SistemaSolar extends JPanel implements ActionListener {

    // Constantes y escala
    private final static double G = 6.67428e-11; // Constante gravitacional
    private final static double dt = 3600 * 24; // Paso de tiempo (1 día en segundos)
    private final static double unidadAstro = 1.496e11; // 1 Unidad Astronómica (UA)
    private final static float escala = 200 / (float) unidadAstro; // Factor de escala
    private final int WIDTH = 1200, HEIGHT = 900;

    double distanciaTierra = unidadAstro; // 1 UA
    double distanciaMarte = 1.524 * unidadAstro; // 1.524 UA
    double distanciaMercurio = 0.387 * unidadAstro; // 0.387 UA
    double distanciaVenus = 0.723 * unidadAstro; // 0.723 UA

    double masaTierra = 5.974e24;
    double masaMercurio = 3.302e23;
    double masaSol = 1.989e30; // masa del Sol en kg

    // Listas para los cuerpos celestes
    private final ArrayList<CuerpoCeleste> planetas = new ArrayList<>();
    private final static CuerpoCeleste sol = new CuerpoCeleste(1200 / 2, 900 / 2, 1.9890e30, 30, 0, 0, "/img/sol.png");

    private final Timer timer;

    private int numParticles = 4000;

    static Rectangle pantalla = new Rectangle(0, 0, 1200, 900);
    static QuadTree quadTree = new QuadTree(pantalla, 20);

    /**
     * Constructor que inicializa la simulación y los planetas con sus
     * parámetros iniciales.
     */
    public SistemaSolar() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);

        //sol = new CuerpoCeleste(WIDTH / 2, HEIGHT / 2, 1.9890e30, 30, 0, 0, "/img/sol.png");
        Random rnorm = new Random();
        double min = 0.387 * unidadAstro;
        double max = 12.0 * unidadAstro;

        for (int i = 0; i < numParticles; i++) {
            double angulo = rnorm.nextDouble() * 2 * Math.PI;
            double distancia = min + (max - min) * rnorm.nextDouble();
            double masaAleatoria = masaMercurio + (masaTierra - masaMercurio) * rnorm.nextDouble();

            double velocidadOrbital = Math.sqrt(G * masaSol / distancia); // m/s

            // Convertir posición en coordenadas visuales
            double x = WIDTH / 2 + Math.cos(angulo) * distancia * escala;
            double y = HEIGHT / 2 + Math.sin(angulo) * distancia * escala;

            // vx = 0, vy = perpendicular al ángulo para que la partícula orbite
            double vx = Math.sin(angulo) * velocidadOrbital * escala;
            double vy = -Math.cos(angulo) * velocidadOrbital * escala;

            CuerpoCeleste nuevo = new CuerpoCeleste((float) x, (float) y, masaAleatoria, 1, vx, vy, null);
            quadTree.insert(nuevo);
            planetas.add(nuevo);

        }

        // Timer para actualizar la simulación
        timer = new Timer(10, this);
        timer.start();
    }

    /**
     * Método de renderizado que dibuja el Sol, los planetas, sus trayectorias y
     * datos informativos como velocidad y fuerza gravitacional.
     *
     * @param g Objeto tipo Graphics usado para el dibujo
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        sol.dibujar(g2);

        // Dibujar los planetas y actualizar sus posiciones
        for (CuerpoCeleste planeta : planetas) {
            planeta.dibujar(g2);
        }

    }

    /**
     * Llamado automáticamente por el Timer. Actualiza la lógica y repinta la
     * pantalla.
     *
     * @param e Evento generado por el Timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        quadTree.actualizarPosiciones();
        repaint();
    }

    /**
     * Calcula la fuerza gravitacional ejercida sobre cada planeta y actualiza
     * su posición y velocidad en base a la segunda ley de Newton.
     */
//    private void actualizarPosiciones() {
//        for (CuerpoCeleste planeta : planetas) {
//            double dx = planeta.x - sol.x;
//            double dy = planeta.y - sol.y;
//            double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r
//
//            double fuerza = quadTree.CalcularFuerza(G, dx, dy, planeta.masa, sol.masa, escala, planetas);
//            double ax = -fuerza * (dx / distancia) / planeta.masa;
//            double ay = -fuerza * (dy / distancia) / planeta.masa;
//
//            // Actualizar velocidades
//            planeta.vx += ax * dt * escala;
//            planeta.vy += ay * dt * escala;
//
//            // Actualizar posiciones
//            planeta.x += planeta.vx * dt;
//            planeta.y += planeta.vy * dt;
//
//        }
//    }
    /**
     * Clase interna que representa un cuerpo celeste como el Sol o un planeta.
     */
    static class CuerpoCeleste {

        float x, y;
        double vx, vy;
        double masa;
        int radio;
        Image imagen;

        /**
         * Constructor del cuerpo celeste con parámetros físicos e imagen
         * asociada.
         *
         * @param x Posición x inicial
         * @param y Posición y inicial
         * @param masa Masa en kilogramos
         * @param radio Radio en píxeles para visualización
         * @param vx Velocidad inicial en x
         * @param vy Velocidad inicial en y
         * @param rutaImagen Ruta de la imagen que representa al cuerpo
         */
        public CuerpoCeleste(float x, float y, double masa, int radio, double vx, double vy, String rutaImagen) {
            this.x = x;
            this.y = y;
            this.masa = masa;
            this.radio = radio;
            if (rutaImagen != null) {
                this.imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
            } else {
                this.imagen = null;
            }
            this.vx = vx;
            this.vy = vy;
        }

        /**
         * Dibuja el cuerpo celeste y su trayectoria pasada.
         *
         * @param g2 Contexto gráfico para dibujar
         */
        public void dibujar(Graphics2D g2) {
            if (imagen != null) {
                //Dibujo para el Sol
                g2.drawImage(imagen, (int) (x - radio), (int) (y - radio), radio * 2, radio * 2, null);
            } else {
                //Dibujo para las particulas
                g2.setColor(Color.WHITE);
                g2.fillOval((int) (x - radio), (int) (y - radio), radio * 2, radio * 2);
            }
        }

        /**
         * Devuelve la magnitud de la velocidad real (sin escalar) del cuerpo.
         *
         * @param escala Escala usada para el renderizado
         * @return Velocidad en m/s
         */
        public double getVelocidadReal(float escala) {
            return Math.sqrt(vx * vx + vy * vy) / escala;
        }

        /**
         * Calcula la magnitud de la fuerza gravitacional entre este cuerpo y el
         * Sol.
         *
         * @param G Constante gravitacional
         * @param dx Diferencia en x
         * @param dy Diferencia en y
         * @param masa Masa del planeta
         * @param solMasa Masa del Sol
         * @param escala Escala usada
         * @return Fuerza gravitacional en Newtons
         */
        public double getFuerzaReal(double G, double dx, double dy, double masa, double solMasa, float escala) {
            double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r
            return (G * solMasa * masa) / Math.pow(distancia / escala, 2);
        }

        public void actualizarPosiciones(ArrayList<CuerpoCeleste> cercanos) {
            double ax = 0;
            double ay = 0;

            // Atraído por el Sol
            double dx = this.x - sol.x;
            double dy = this.y - sol.y;
            double distancia = Math.sqrt(dx * dx + dy * dy);
            if (distancia != 0) {
                double fuerza = (SistemaSolar.G * this.masa * sol.masa) / Math.pow(distancia / SistemaSolar.escala, 2);
                ax -= fuerza * (dx / distancia) / this.masa;
                ay -= fuerza * (dy / distancia) / this.masa;
            }

//            // Atraído por partículas cercanas
//            for (CuerpoCeleste otro : cercanos) {
//                if (this == otro) {
//                    continue;
//                }
//
//                double dx2 = this.x - otro.x;
//                double dy2 = this.y - otro.y;
//                double distancia2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
//                if (distancia2 != 0) {
//                    double fuerza2 = (SistemaSolar.G * this.masa * otro.masa) / Math.pow(distancia2 / SistemaSolar.escala, 2);
//                    ax -= fuerza2 * (dx2 / distancia2) / this.masa;
//                    ay -= fuerza2 * (dy2 / distancia2) / this.masa;
//                }
//            }

            this.vx += ax * SistemaSolar.dt * SistemaSolar.escala;
            this.vy += ay * SistemaSolar.dt * SistemaSolar.escala;

            this.x += this.vx * SistemaSolar.dt;
            this.y += this.vy * SistemaSolar.dt;
        }
    }

    /**
     * Método principal que lanza la aplicación y crea la ventana principal.
     *
     * @param args Argumentos de línea de comandos
     */
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
