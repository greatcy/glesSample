package com.eli.glesstep.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.eli.glesstep.R;

/**
 * Created by chenjunheng on 2017/9/4.
 * 顔色shader
 */

public class ColorShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;

    private final int aPositionLocation;
    private final int uColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
    }

    public void setUniforms(float[] matrix, float r, float g, float b) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionLocation() {
        return aPositionLocation;
    }

}
