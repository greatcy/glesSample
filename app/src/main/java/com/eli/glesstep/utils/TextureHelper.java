package com.eli.glesstep.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.eli.glesstep.Config;

/**
 * Created by chenjunheng on 2017/9/4.
 */

public class TextureHelper {
    private final static String TAG = TextureHelper.class.getSimpleName();

    /**
     * 生成2D纹理对象
     *
     * @param context
     * @param resourceId 加载到opengl服务端的纹理ID
     * @return
     */
    public static int loadTexture(Context context,
                           int resourceId) {
        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            if (Config.LOG_ON) {
                Log.w(TAG, "Could not gen a new texture object!");
            }
            return 0;
        }

        //bind bitmap
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                resourceId, options);

        if (bitmap == null) {
            if (Config.LOG_ON) {
                Log.w(TAG, "resource id could not be decoded!");
            }
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        //设置OPENGL状态机，之后的纹理调用使用该纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);

        //设置纹理过滤器
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        //加载位图到OPENGL，读入纹理数据到之前绑定的状态机
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        //完成纹理的加载，重置状态机
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

}
