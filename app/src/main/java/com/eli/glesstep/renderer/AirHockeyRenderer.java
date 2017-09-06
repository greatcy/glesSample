package com.eli.glesstep.renderer;
/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.eli.glesstep.Config;
import com.eli.glesstep.R;
import com.eli.glesstep.geometry.Geometry;
import com.eli.glesstep.object.DebugRayLine;
import com.eli.glesstep.object.Mallet;
import com.eli.glesstep.object.Puck;
import com.eli.glesstep.object.Table;
import com.eli.glesstep.programs.ColorShaderProgram;
import com.eli.glesstep.programs.TextureShaderProgram;
import com.eli.glesstep.utils.MatrixHelper;
import com.eli.glesstep.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * !使用右手坐标系
 * 整体思路是先准备好绘制顶点数组和渲染shader，在绘制
 * 时使用状态机，改变渲染的物体状态并绘制
 */
public class AirHockeyRenderer implements Renderer {
    private final Context context;

    private final float[] modelMatrix = new float[16];

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;


    private int texture;

    //保留通过透视投影和平移生成的矩阵
    private final float[] projectMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;
    private DebugRayLine debugRayLine;

    private final float invertedViewProjectionMatrix[] = new float[16];
    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

    private Geometry.Point previousBlueMalletPosition;
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;


    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        puckVector = new Geometry.Vector(0f, 0f, 0f);
        puckPosition = new Geometry.Point(0f, puck.height / 2, 0f);

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);

    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width  The new width, in pixels.
     * @param height The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        //使用透视投影的方法
        MatrixHelper.perspectiveM(projectMatrix,
                45, (float) width / (float) height, 1f, 10f);

        //设置视口位置，替换之前我们对场景物体的移动旋转
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);

    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GL_COLOR_BUFFER_BIT);

        puckPosition = puckPosition.translate(puckVector);

        // If the puck struck a side, reflect it off that side.
        if (puckPosition.x < leftBound + puck.radius
                || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius
                || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        // Friction factor
        puckVector = puckVector.scale(0.99f);

        // Clamp the puck position.
        puckPosition = new Geometry.Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );

        //合并视口和物体透视投影矩阵的变化
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectMatrix, 0, viewMatrix, 0);

        //记录当前视口和投影的逆矩阵
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        //draw table
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniForm(modelViewProjectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        //mallets
        //在视口和透视投影已经完成的状态下，设置x = 0 (坐标系上-1,1) y=mallet.height / 2f (物体的高，用来透视),z=-0.4(table 中间的位置)
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        //在视口和透视投影已经完成的状态下，设置x = 0 (坐标系上-1,1) y=mallet.height / 2f (物体的高，用来透视),z=0.4(table 中间的位置)
//        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        //draw debug line
        if (debugRayLine != null && Config.IS_DEBUG) {
            colorShaderProgram.useProgram();
            colorShaderProgram.setUniforms(invertedViewProjectionMatrix, 0f, 1f, 0f);
            debugRayLine.bindData(colorShaderProgram);
            debugRayLine.draw();
        }

        // Draw the puck.
//        positionObjectInScene(0f, puck.height / 2f, 0f);
        positionObjectInScene(puckPosition.x, puck.height / 2f, puckPosition.z);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorShaderProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        //在已有的变换基础上增加物体旋转
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        //在已有的变换基础上增加物体平移
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    public void handleTouchPress(float normalizeX, float normalizeY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizeX, normalizeY);

        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z),
                mallet.height / 2f);

        malletPressed = Geometry.intersects(malletBoundingSphere, ray);

        //do draw debug line
        debugRayLine = new DebugRayLine(normalized2DPointToRay(normalizeX, normalizeY));
    }

    public void handleTouchDrag(float normalizeX, float normalizeY) {
        if (malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizeX, normalizeY);

            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));

            Geometry.Point touchedPoint = Geometry.intersects(ray, plane);

            //这里不使用farBound设定中点
            blueMalletPosition = new Geometry.Point(clamp(touchedPoint.x,
                    leftBound + mallet.radius,
                    rightBound - mallet.radius)
                    , mallet.height / 2,
                    clamp(touchedPoint.z, 0 + mallet.radius, nearBound - mallet.radius));

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
            previousBlueMalletPosition = blueMalletPosition;
        }

        //do draw debug line
        debugRayLine = new DebugRayLine(normalized2DPointToRay(normalizeX, normalizeY));
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    public void handleTouchUp(float normalizeX, float normalizeY) {
        debugRayLine = null;
    }

    /**
     * @param normalizedX
     * @param normalizedY
     * @return 屏幕转换到世界坐标的3D射线
     */
    private Geometry.Ray normalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        Geometry.Point nearPoint = new Geometry.Point(nearPointNdc[0], nearPointNdc[1], nearPointNdc[2]);
        Geometry.Point farPoint = new Geometry.Point(farPointNdc[0], farPointNdc[1], farPointNdc[2]);

        return new Geometry.Ray(nearPoint, Geometry.vectorBetween(nearPoint, farPoint));
    }

    /**
     * 这里对屏幕射线进行还原是因为，需要检测相交性等计算，是在场景中的物体坐标未进行视口和投影转换之前做的
     *
     * @param normalizedX
     * @param normalizedY
     * @return 获得通过逆矩阵返回的3D射线
     */
    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }
}