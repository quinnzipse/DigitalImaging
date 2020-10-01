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
 * @author Kennrow Hunt
 */
public class PackedPixelImage extends AbstractDigitalImage {
   private int[] raster;
   private static final int MASK0 = 0xff, MASK1 = 0xff00, MASK2 = 0xff0000, MASK3 = 0xff000000;

   public PackedPixelImage(int width, int height, int bands) {
      super(width, height, bands);
      raster = new int[width * height];
   }

   private int getMask(int band) {
      switch(band) {
         case 0: return MASK0;
         case 1: return MASK1;
         case 2: return MASK2;
         case 3: return MASK3;
         default: throw new IllegalArgumentException();
      }
   }

   public int getSample(int col, int row, int band) {
      return (raster[col + row * width] >> (band * 8)) & 0xFF;
   }

   public void setSample(int col, int row, int band, int s) {
      int pixelIndex = row * width + col;
      int pixel = raster[pixelIndex];
      int mask = getMask(band); 

      pixel = (pixel & ~mask) ^ s << (band * 8);
      raster[pixelIndex] = pixel;
   }

   public int[] getPixel(int col, int row) {
      int[] result = new int[bands];
      for (int b = 0; b < bands; b++) {
         result[b] = getSample(col, row, b);
      }
      return result;
   }

   public void setPixel(int col, int row, int[] pixel) {
      for (int b = 0; b < bands; b++) {
         setSample(col, row, b, pixel[b]);
      }
   }

}
