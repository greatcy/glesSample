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

import com.eli.glesstep.R;
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
 * 整体思路是先准备好绘制顶点数组和渲染shader，在绘制
 * 时使用状态机，改变渲染的物体状态并绘制
 */
public class AirHockeyRenderer implements Renderer {
    private final Context context;

    private final float[] modelMatrix = new float[16];

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    //保留通过透视投影和平移生成的矩阵
    private final float[] projectMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;


    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
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

        //合并视口和物体透视投影矩阵的变化
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectMatrix, 0, viewMatrix, 0);

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
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1, 0, 0);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        //在视口和透视投影已经完成的状态下，设置x = 0 (坐标系上-1,1) y=mallet.height / 2f (物体的高，用来透视),z=-0.4(table 中间的位置)
        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1, 0, 0);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        // Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f);
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

    public void handleTouchPress(float normalizeX,float normalizeY){

    }

    public void handleTouchDrag(float normalizeX,float normalizeY){

    }
}