package com.onegateafrica.Controllers.utils;

public abstract class DataValidationUtils {


  public static Boolean isValidId(Long id) {
    if (id == null)
      return false;
    else {
      return true;
    }
  }


  public static Boolean isValid(String any) {
    if (any == null)
      return false;
    else {
      return true;
    }
  }
}
