package com.eli.glesstep.renderer;
/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/


import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.eli.glesstep.Constant;
import com.eli.glesstep.LoggerConfig;
import com.eli.glesstep.R;
import com.eli.glesstep.object.Mallet;
import com.eli.glesstep.object.Table;
import com.eli.glesstep.programs.ColorShaderProgram;
import com.eli.glesstep.programs.TextureShaderProgram;
import com.eli.glesstep.utils.MatrixHelper;
import com.eli.glesstep.utils.ShaderHelper;
import com.eli.glesstep.utils.TextResourceReader;
import com.eli.glesstep.utils.TextureHelper;

public class AirHockeyRenderer implements Renderer {
    private final Context context;
    //保留通过透视投影和平移生成的矩阵
    private final float[] projectMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet();

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

        //设置平移矩阵
        Matrix.setIdentityM(modelMatrix, 0);

        //旋转桌子
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        //合并透视和平移矩阵
        final float temp[] = new float[16];
        Matrix.multiplyMM(temp, 0, projectMatrix, 0,
                modelMatrix, 0);
        System.arraycopy(temp, 0, projectMatrix,
                0, temp.length);

    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GL_COLOR_BUFFER_BIT);

        //table
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniForm(projectMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        //mallets
        mallet = new Mallet();
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
    }
}