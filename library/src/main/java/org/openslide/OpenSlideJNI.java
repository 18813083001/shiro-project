/*
 *  OpenSlide, a library for reading whole slide image files
 *
 *  Copyright (c) 2007-2010 Carnegie Mellon University
 *  All rights reserved.
 *
 *  OpenSlide is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, version 2.1.
 *
 *  OpenSlide is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with OpenSlide. If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 */

package org.openslide;

import com.thorough.library.utils.PropertyUtil;

class OpenSlideJNI{
    private OpenSlideJNI() {
    }

    static {
        try {
            System.load(PropertyUtil.getProperty("pathology.image.openslide.jni-so"));
        } catch (Exception e) {
            throw new RuntimeException("initialize openslide failure,openslide.so.not.found");
        }
    }

    native static String openslide_detect_vendor(String file);

    native static boolean openslide_accessable(String path);

    native static long openslide_open(String file);

    native static int openslide_get_level_count(long osr);

    native static void openslide_get_level_dimensions(long osr, int level,
                                                      long dim[]);

    native static double openslide_get_level_downsample(long osr, int level);

    native static void openslide_close(long osr);

    native static String[] openslide_get_property_names(long osr);

    native static String openslide_get_property_value(long osr, String name);

    native static String[] openslide_get_associated_image_names(long osr);

    native static void openslide_read_region(long osr, int dest[], long x,
                                             long y, int level, long w, long h);

    native static void openslide_get_associated_image_dimensions(long osr,
                                                                 String name, long dim[]);

    //    native static void openslide_read_associated_image(long osr, String name,
//            int dest[]);
    native static byte[] openslide_read_associated_image(long osr);

    native static String openslide_get_error(long osr);

    native static String openslide_get_version();
    native static int openslide_get_tile_size(long osr);
    native static byte[] openslide_read_raw_tile(long osr, long tx, long ty, int level);
}
