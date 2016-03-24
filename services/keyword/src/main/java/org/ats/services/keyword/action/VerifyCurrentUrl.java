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
 * Apr 14, 2015
 */
@SuppressWarnings("serial")
public class VerifyCurrentUrl extends AbstractAction {
  
  private Value url;
  
  private boolean negated;
  
  public VerifyCurrentUrl(Value url, boolean negated) {
    this.url = url;
    this.negated = negated;
  }

  public String transform() throws IOException {
//    StringBuilder sb = new StringBuilder("if (").append(negated ? "" : "!");
//    sb.append("wd.getCurrentUrl().equals(@url)) {\n");
//    sb.append("      System.out.println(\"").append(negated ? "!" : "").append("verifyCurrentUrl failed\");\n");
//    sb.append("    }\n");
    StringBuilder sb = new StringBuilder();
	sb.append("try { \n");
	sb.append("     if (").append(negated ? "" : "!");
	sb.append("     wd.getCurrentUrl().equals(@url)) {\n");
	sb.append("      System.out.println(\"").append(negated ? "!" : "").append("verifyCurrentUrl failed\");\n");
	sb.append("    }\n");
	sb.append("   } catch (Exception e) { \n");
	sb.append("     SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy/MM/dd HH:mm:ss\");\n");
	sb.append("     long time = dateFormat.parse(dateFormat.format(new Date())).getTime();\n");
	sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/\"+time+\".png\"));\n");
	sb.append("     throw e ; \n");
	sb.append("   }\n");
    RythmEngine engine = new RythmEngine(new MapBuilder<String, Boolean>("codegen.compact", false).build());
    return engine.render(sb.toString(), url.transform());
  }

  public String getAction() {
    return "verifyCurrentUrl";
  }

}
