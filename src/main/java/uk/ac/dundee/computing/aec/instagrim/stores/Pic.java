package uk.ac.dundee.computing.aec.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;

/**
 * Class to store images, their length and type, and also assign them an UUID.
 *
 * @author Administrator
 * @author jslvtr
 *
 * @since 16 Oct 2014
 */
public class Pic {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID = null;

    /**
     * Sets the UUID of this image instance.
     * @param UUID the Universal Unique ID to give this image.
     */
    public void setUUID(java.util.UUID UUID){
        this.UUID = UUID;
    }

    /**
     * Gets the UUID of this image instance.
     * @return the Universal Unique ID of this image.
     */
    public String getUUID(){
        return UUID.toString();
    }

    /**
     * Sets the picture in this image.
     *
     * @param bImage a ByteBuffer which contains the image itself.
     * @param length the length of the buffer.
     * @param type the file-type of the image.
     */
    public void setPic(ByteBuffer bImage, int length, String type) {
        this.bImage = bImage;
        this.length = length;
        this.type=type;
    }

    /**
     * Gets the ByteBuffer of this image.
     * @return ByteBuffer: the bytes in the actual image.
     */
    public ByteBuffer getBuffer() {
        return this.bImage;
    }

    /**
     * Gets the length of this image.
     * @return int: the length of this image
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Gets the file-type of this image.
     * @return String: the type of this image.
     */
    public String getType(){
        return this.type;
    }

    /**
     * Gets a Byte array that represents this image.
     * @return byte[]: an array to represent this image in Bytes.
     */
    public byte[] getBytes() {
        return Bytes.getArray(bImage);
    }
}
