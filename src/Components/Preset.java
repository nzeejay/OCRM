package Components;

public class Preset {
    public String text;

    public int[] rgb;

    public boolean isMoveable;

    public Preset(String text, int r, int g, int b, boolean moveable) {
        this.text = text;
        this.rgb = new int[]{r, g, b};

        isMoveable = moveable;
    }
}
