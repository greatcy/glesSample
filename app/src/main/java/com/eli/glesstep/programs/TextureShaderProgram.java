package com.eli.glesstep.programs;

import android.content.Context;

import com.eli.glesstep.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by chenjunheng on 2017/9/4.
 * 纹理shader
 */

public class TextureShaderProgram extends ShaderProgram {
    private final int aPositionLocation;
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.table_vertex_shader, R.raw.table_fragment_shader);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
    }

    /**
     * 传递矩阵和纹理ID
     *
     * @param matrix
     * @param textureId
     */
    public void setUniForm(float[] matrix, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId);

        glUniform1i(uTextureUnitLocation,0);
    }

    public int getPositionLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesLocation() {
        return aTextureCoordinatesLocation;
    }
}
