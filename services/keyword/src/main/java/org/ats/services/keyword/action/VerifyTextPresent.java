/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

import org.ats.common.MapBuilder;
import org.ats.services.keyword.Value;
import org.rythmengine.RythmEngine;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 13, 2015
 */
@SuppressWarnings("serial")
public class VerifyTextPresent extends AbstractAction {

  private Value text;
  
  private boolean negated;
  
  public VerifyTextPresent(Value text, boolean negated) {
    this.text = text;
    this.negated = negated;
  }
  
  public String transform() throws IOException {
    StringBuilder sb = new StringBuilder();
	sb.append("try { \n");
	sb.append("     if (").append(negated ? "!" : "");
	sb.append("wd.findElement(By.tagName(\"html\")).getText().contains(@text)) {\n");
	sb.append("     System.out.println(\"[End][Step]\"); \n");
    sb.append("    } else {\n");
    sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/error\"+System.currentTimeMillis()+\"_verifyTextPresent.png\"));\n");
    sb.append("    }\n");
	sb.append("   } catch (Exception e) { \n");
	sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/error\"+System.currentTimeMillis()+\"_verifyTextPresent.png\"));\n");
	sb.append("     e.printStackTrace();\n");
	sb.append("     throw e ; \n");
	sb.append("   }\n");
    RythmEngine engine = new RythmEngine(new MapBuilder<String, Boolean>("codegen.compact", false).build());
    return engine.render(sb.toString(), text.transform());
  }

  public String getAction() {
    return "verifyTextPresent";
  }

}
