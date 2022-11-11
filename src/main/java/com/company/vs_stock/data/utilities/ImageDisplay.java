package com.company.vs_stock.data.utilities;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageDisplay {
    public static final String uploadPathItem = "C:\\jar\\pics\\items\\";
    public static final String uploadPathProject= "C:\\jar\\pics\\projects\\";
    public static final String defaultPic = "/img/exclamation-square.svg";


    public static String displayImageFromPath(String imageLocationOnC) throws IOException {
        var is = new BufferedInputStream(new FileInputStream(imageLocationOnC));
        byte[] imageBytes = IOUtils.toByteArray(is);
        Base64.Encoder encoder = Base64.getEncoder();
        return "data:image/jpeg;base64,"+ encoder.encodeToString(imageBytes);
    }

}
