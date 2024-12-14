package com.example.imagetagseditor.model

import androidx.exifinterface.media.ExifInterface

class ImageData(tags: Map<String, String>) {
    val tags = tags.keys.zip(tags.values).toMutableList()

    companion object {
        val tagNames = listOf(
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_IMAGE_DESCRIPTION,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY,
            ExifInterface.TAG_SATURATION,
            ExifInterface.TAG_SHARPNESS,
            ExifInterface.TAG_WHITE_BALANCE,
            ExifInterface.TAG_BRIGHTNESS_VALUE,
            ExifInterface.TAG_EXPOSURE_TIME,
        )

        val editableTagNames = listOf(
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE
        )
    }
}