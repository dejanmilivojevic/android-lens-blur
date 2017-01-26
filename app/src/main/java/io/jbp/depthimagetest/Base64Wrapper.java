package io.jbp.depthimagetest;

/**
 * Created by vanitas on 28.04.15.
 */
public abstract class Base64Wrapper
{
    public abstract byte[] decode(byte[] data);
    public abstract byte[] encode(byte[] data);
}
