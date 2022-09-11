package com.onegateafrica.Controllers.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

public abstract class ImageIO {

  private static final  String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/images/";
  private static final String DEFAULT_IMAGE_EXTENSION = "png";


  public static Boolean uploadImage(MultipartFile multiPartFile,String filename) {
    try {
      makeDirectoryIfNotExist(IMAGE_DIRECTORY);
      Path fileNamePath = Paths.get(IMAGE_DIRECTORY,formatFileName(filename,DEFAULT_IMAGE_EXTENSION));
      Files.write(multiPartFile.getBytes(), new File(fileNamePath.toUri()));
      return true;
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    }
  }


  public static byte[] getImage( String imagename) {

    try {
      if (imagename=="DEFAULT")
      {
        FileInputStream in = new FileInputStream(IMAGE_DIRECTORY+"staticImages/placeholderprofilepicture"+"."+DEFAULT_IMAGE_EXTENSION);
        return IOUtils.toByteArray(in);
      }
      else
      {
        FileInputStream in = new FileInputStream(IMAGE_DIRECTORY+imagename+"."+DEFAULT_IMAGE_EXTENSION);
        return IOUtils.toByteArray(in);
      }

    } catch (Exception e) {
      e.printStackTrace();
    return null;
    }
  }
  public static Boolean isImage(String imageName){
    return new File(IMAGE_DIRECTORY+imageName+"."+DEFAULT_IMAGE_EXTENSION).isFile();
  }

  public static byte[] getImagePlaceholder() {
    try {
      FileInputStream in = new FileInputStream(IMAGE_DIRECTORY+"staticImages/placeholder.png");
      return IOUtils.toByteArray(in);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  private static String formatFileName(String filename, String extension) {
    return filename+ "." + extension;
  }

  private static  void makeDirectoryIfNotExist(String imageDirectory) {
    File directory = new File(imageDirectory);
    if (!directory.exists()) {
      directory.mkdir();
    }
  }
  public static byte[] getProfilImagePlaceholder() {
    try {
      FileInputStream in = new FileInputStream(IMAGE_DIRECTORY+"staticImages/placeholderprofilepicture.png");
      return IOUtils.toByteArray(in);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
