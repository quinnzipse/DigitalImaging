/*
 * Copyright (c) 2010, Kenny A. Hunt
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of  Kenny A. Hunt nor the names
 *    of its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package hw1;

/**
 * 
 * @author Kenny Hunt
 */
public class ArrayDigitalImage extends AbstractDigitalImage {
    private int[][][] raster;

    public ArrayDigitalImage(int width, int height, int bands) {
        super(width, height, bands);
        raster = new int[height][width][bands];
    }

    public int getSample(int x, int y, int b) {
        return raster[y][x][b];
    }

    public void setSample(int x, int y, int b, int s) {
        raster[y][x][b] = s;
    }

    public int[] getPixel(int x, int y) {
        int[] result = new int[bands];
        System.arraycopy(raster[y][x], 0, result, 0, bands);
        return result;
    }

    public void setPixel(int x, int y, int[] pixel) {
        System.arraycopy(pixel, 0, raster[y][x], 0, bands);
    }
}
