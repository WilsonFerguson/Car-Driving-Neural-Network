import library.core.*;

public class Button extends PComponent {
    PVector pos;
    PVector dim;

    color bg;
    color strokeColor;
    color textColor;

    String text;

    public boolean mouseDown = false;

    // this. from coding train
    public Button(PVector pos, PVector dim, String text) {
        this.pos = pos;
        this.dim = dim;
        this.text = text;

        bg = new color(0, 0, 0);
        strokeColor = new color(255, 255, 255);
        textColor = new color(0, 0, 0);
    }

    public boolean hover() {
        return mouseX > pos.x && mouseX < pos.x + dim.x && mouseY > pos.y && mouseY < pos.y + dim.y;
    }

    // public void mouseReleased() {
    // if (hover())
    // clicked();
    // }

    public void clicked() {
    }

    public void setBackground(int r, int g, int b) {
        bg = new color(r, g, b);
    }

    public void setStrokeColor(int r, int g, int b) {
        strokeColor = new color(r, g, b);
    }

    public void setTextColor(int r, int g, int b) {
        textColor = new color(r, g, b);
    }

    // draw ideas from processing
    public void draw() {
        push();
        rectMode(CORNER);
        stroke(strokeColor);
        fill(bg);
        rect(pos.x, pos.y, dim.x, dim.y, 30);
        fill(textColor);
        textAlign(CENTER);
        textSize(50);
        text(text, pos.x + dim.x / 2, pos.y + dim.y / 2);
        pop();

        if (hover() && mousePressed && !mouseDown) {
            mouseDown = true;
            clicked();
        } else if (!mousePressed) {
            mouseDown = false;
        }
    }
}