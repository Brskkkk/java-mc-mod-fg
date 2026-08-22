package com.framegen;

public class FrameBuffer {
    private int width;
    private int height;
    private float[] colorData;
    private float[] depthData;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.colorData = new float[width * height * 4]; // RGBA
        this.depthData = new float[width * height];
    }

    public void setPixel(int x, int y, float r, float g, float b, float a) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        int idx = (y * width + x) * 4;
        colorData[idx] = r;
        colorData[idx + 1] = g;
        colorData[idx + 2] = b;
        colorData[idx + 3] = a;
    }

    public void getPixel(int x, int y, float[] out) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        int idx = (y * width + x) * 4;
        out[0] = colorData[idx];
        out[1] = colorData[idx + 1];
        out[2] = colorData[idx + 2];
        out[3] = colorData[idx + 3];
    }

    public void blend(FrameBuffer other, float alpha) {
        for (int i = 0; i < colorData.length; i++) {
            colorData[i] = colorData[i] * (1 - alpha) + other.colorData[i] * alpha;
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float[] getColorData() { return colorData; }
    public float[] getDepthData() { return depthData; }
}
