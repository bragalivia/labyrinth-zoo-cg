package org.yourorghere;

public class Vertice {

    private float x;
    private float y;
    private float z;
    private boolean origem;
    int r, g, b;

    Vertice(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertice(float x, float y, float z, boolean origem) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.origem = origem;
    }

    public Vertice(float x, float y, float z, boolean origem, int r, int g, int b) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.origem = origem;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Vertice(float x, float y, float z, int r, int g, int b) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public boolean isOrigem() {
        return this.origem;
    }

    public int getB() {
        return b;
    }

    public int getG() {
        return g;
    }

    public int getR() {
        return r;
    }
}
