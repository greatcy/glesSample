package com.eli.glesstep.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.eli.glesstep.utils.ShaderHelper;
import com.eli.glesstep.utils.TextResourceReader;

/**
 * Created by chenjunheng on 2017/9/4.
 * Shader 基类
 */

public class ShaderProgram {
    //Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_textureUnit";
    protected static final String U_COLOR = "u_Color";

    //attr constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    //shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
