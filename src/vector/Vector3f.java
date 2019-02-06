package vector;

/**
 * Created by Dante on 6/29/2016.
 * Some helper classes to conveniently wrap two and three floats
 */
public class Vector3f {
    public float x, y, z;

    public Vector3f(float a, float b, float c) {
        x = a;
        y = b;
        z = c;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3f)) return false;
        Vector3f v = (Vector3f) obj;
        return x == v.x && y == v.y && z == v.z;
    }

    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + (int) (x * 127);
        hash = hash * 31 + (int) (y * 127);
        hash = hash * 31 + (int) (z * 127);
        return hash;
    }

    public String toString() {
        return x + " " + y + " " + z;
    }

    public float dist(Vector3f v) {
        return (float) Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2) + Math.pow(z - v.z, 2));
    }

    public void scale(float f) {
        x *= f;
        y *= f;
        z *= f;
    }

    public Vector3f scaled(float f) {
        Vector3f result = new Vector3f(x, y, z);
        result.scale(f);
        return result;
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalized() {
        float m = magnitude();
        return new Vector3f(x / m, y / m, z / m);
    }
}
