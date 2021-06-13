package de.k3b.util;

import org.junit.Assert;
import org.junit.Test;

public class ImageResizeTest {

    @Test
    public void getResizeFactor() {
        Assert.assertNull("to small", ImageResize.getResizeFactor(40,30,48));
        Assert.assertEquals("96 x 48", 0.5, ImageResize.getResizeFactor(96,48,48), 0.00001);
        Assert.assertEquals("48 x 96",0.5, ImageResize.getResizeFactor(48,96,48), 0.00001);
    }
}