/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 10, 2015
 */
@SuppressWarnings("serial")
public class Close extends AbstractAction {

  public String transform() throws IOException {
	  StringBuilder sb = new StringBuilder();
	  sb.append("try { \n");
	  sb.append("     wd.close();\n");
	  sb.append("     System.out.println(\"[End][Step]\"); \n");
	  sb.append("   } catch (Exception e) { \n");
	  sb.append("     e.printStackTrace();\n");
	  sb.append("     throw e ; \n");
	  sb.append("   }\n");
    return sb.toString();
  }

  public String getAction() {
    return "close";
  }

}
