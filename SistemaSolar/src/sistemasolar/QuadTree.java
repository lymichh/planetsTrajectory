package sistemasolar;

import java.util.ArrayList;
import java.util.List;
import sistemasolar.SistemaSolar.CuerpoCeleste;

class Point {
    
    public double x, y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Rectangle {
    
    public double x, y, width, height;
    
    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean contains(CuerpoCeleste point) {
        return point.x >= this.x - this.width
                && point.x <= this.x + this.width
                && point.y >= this.y - this.height
                && point.y <= this.y + this.height;
    }
}

class QuadTree {
    
    private Rectangle boundary;
    private int capacity;
    public List<CuerpoCeleste> points;
    private boolean divided = false;
    
    private QuadTree northeast;
    private QuadTree northwest;
    private QuadTree southeast;
    private QuadTree southwest;
    
    public QuadTree(Rectangle boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.points = new ArrayList<>();
    }

    // Método que subdivide el quadtree en 4 cuadrantes
    public void subdivide() {
        double x = this.boundary.x;
        double y = this.boundary.y;
        double w = this.boundary.width / 2;
        double h = this.boundary.height / 2;
        
        Rectangle ne = new Rectangle(x + w, y - h, w, h);
        northeast = new QuadTree(ne, capacity);
        
        Rectangle nw = new Rectangle(x - w, y - h, w, h);
        northwest = new QuadTree(nw, capacity);
        
        Rectangle se = new Rectangle(x + w, y + h, w, h);
        southeast = new QuadTree(se, capacity);
        
        Rectangle sw = new Rectangle(x - w, y + h, w, h);
        southwest = new QuadTree(sw, capacity);
        
        divided = true;
    }
    
    public void insert(CuerpoCeleste point) {
        if (!boundary.contains(point)) {
            return;
        }
        
        if (points.size() < capacity) {
            points.add(point);
        } else {
            if (!divided) {
                subdivide();
                
                for (CuerpoCeleste planeta : this.points) {
                    this.northwest.insert(planeta);
                    this.northeast.insert(planeta);
                    this.southwest.insert(planeta);
                    this.southeast.insert(planeta);
                }
            }
            this.northwest.insert(point);
            this.northeast.insert(point);
            this.southwest.insert(point);
            this.southeast.insert(point);
        }
    }
    
    public void actualizarPosiciones() {
        
        if (this.divided) {
            this.northeast.actualizarPosiciones();
            this.southeast.actualizarPosiciones();
            this.northwest.actualizarPosiciones();
            this.southwest.actualizarPosiciones();
        } else {
//            ArrayList<CuerpoCeleste> particles = null;
//            particles.addAll(this.points);
            for (CuerpoCeleste planeta : this.points) {
                planeta.actualizarPosiciones((ArrayList<CuerpoCeleste>) this.points);
            }
        }
        
//        for (CuerpoCeleste planeta : this.points) {
//
//            //calcular con el sol
//            double fuerza = CalcularFuerza(G, dx, dy, planeta.masa, 1.9890e30, escala);
//            for (CuerpoCeleste planeta2 : this.points) {
//                //calcular con los demás planetas
//                if (planeta != planeta2) {
//                    
//                    double dx = planeta.x - x;
//                    double dy = planeta.y - y;
//                    double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r
//
//                    double fuerzaSol = CalcularFuerza(G, dx, dy, planeta.masa, 1.9890e30, escala);
//                    
//                    double ax = -fuerza * (dx / distancia) / planeta.masa;
//                    double ay = -fuerza * (dy / distancia) / planeta.masa;
//
//                    // Actualizar velocidades
//                    planeta.vx += ax * dt * escala;
//                    planeta.vy += ay * dt * escala;
//
//                    // Actualizar posiciones
//                    planeta.x += planeta.vx * dt;
//                    planeta.y += planeta.vy * dt;
//                }
//            }
//        }
    }
    
    public double CalcularFuerza(double G, double dx, double dy, double masa, double solMasa, float escala) {
        double distancia = Math.sqrt(dx * dx + dy * dy); // Distancia r
        return (G * solMasa * masa) / Math.pow(distancia / escala, 2);
    }
    
}
