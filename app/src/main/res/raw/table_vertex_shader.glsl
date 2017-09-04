uniform mat4 u_Matrix;//变换矩阵
attribute vec4 a_Position;//位置属性
attribute vec2 a_TextureCoordinates;//纹理坐标

varying vec2 v_TextureCoordinates;//插值纹理坐标

void main()                    
{                            
	v_TextureCoordinates=a_TextureCoordinates;
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 20.0;
}          