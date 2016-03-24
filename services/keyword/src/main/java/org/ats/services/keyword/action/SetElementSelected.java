/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

import org.ats.common.MapBuilder;
import org.ats.services.keyword.locator.AbstractLocator;
import org.rythmengine.RythmEngine;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 9, 2015
 */
@SuppressWarnings("serial")
public class SetElementSelected extends AbstractAction {

  private AbstractLocator locator;
  
  public SetElementSelected(AbstractLocator locator) {
    this.locator = locator;
  }
  
  public String transform() throws IOException {
//    String template = "if (!wd.findElement(@locator).isSelected()) {\n"
//        + "      wd.findElement(@locator).click();\n"
//        + "    }\n";
    StringBuilder sb = new StringBuilder();
	sb.append("try { \n");
	sb.append("     if (!wd.findElement(@locator).isSelected()) {\n");
	sb.append("     wd.findElement(@locator).click();\n");
	sb.append("     }\n");
	sb.append("   } catch (Exception e) { \n");
	sb.append("     SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy/MM/dd HH:mm:ss\");\n");
	sb.append("     long time = dateFormat.parse(dateFormat.format(new Date())).getTime();\n");
	sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/\"+time+\".png\"));\n");
	sb.append("     throw e ; \n");
	sb.append("   }\n");
    RythmEngine engine = new RythmEngine(new MapBuilder<String, Boolean>("codegen.compact", false).build());
    return engine.render(sb.toString(), locator.transform());
  }

  public String getAction() {
    return "setElementSelected";
  }
}
