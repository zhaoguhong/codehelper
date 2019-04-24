package com.zhaoguhong.codehelper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtil {

  public static List<String> find(String content, String reg) {
    Pattern p = Pattern.compile(reg);
    Matcher m = p.matcher(content);
    List<String> strs = new ArrayList<String>();
    while (m.find()) {
      strs.add(m.group(1));
    }
    return strs;
  };


}
