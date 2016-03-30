/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 8, 2015
 */
@SuppressWarnings("serial")
public class GoForward extends AbstractAction {

  public String transform() throws IOException {
	  StringBuilder sb = new StringBuilder();
		sb.append("try { \n");
		sb.append("     wd.navigate().forward();\n");
		sb.append("     System.out.println(\"[End][Step]\"); \n");
		sb.append("   } catch (Exception e) { \n");
		sb.append("     time = dateFormat.parse(dateFormat.format(new Date())).getTime();\n");
		sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/error_\"+time+\"_goForward.png\"));\n");
		sb.append("     e.printStackTrace();\n");
		sb.append("     throw e ; \n");
		sb.append("   }\n");
    return sb.toString();
  }

  public String getAction() {
    return "goForward";
  }

}
