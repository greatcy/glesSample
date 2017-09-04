precision mediump float;

uniform sampler2D u_TextureUnit;//纹理数据
varying vec2 v_TextureCoordinates;//插值纹理坐标
  
void main()                    		
{                              	
    gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates);//读入v_TextureCoordinates在纹理u_TextureUnit中的颜色值，赋值给gl_fragcolor
}