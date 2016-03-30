/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

import org.rythmengine.Rythm;

/**
 * @author TrinhTV3
 *
 * Email: TrinhTV3@fsoft.com.vn
 */
@SuppressWarnings("serial")
public class SwitchToFrameByIndex extends AbstractAction {

  private int index;

  public SwitchToFrameByIndex(int index) {
    this.index = index;
  }
  
  public String transform() throws IOException {
    StringBuilder sb = new StringBuilder();
	sb.append("try { \n");
	sb.append("     wd = (FirefoxDriver) wd.switchTo().frame(@index);\n");
	sb.append("     System.out.println(\"[End][Step]\"); \n");
	sb.append("   } catch (Exception e) { \n");
	sb.append("     time = dateFormat.parse(dateFormat.format(new Date())).getTime();\n");
	sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/error_\"+time+\"_switchToFrameByIndex.png\"));\n");
	sb.append("     e.printStackTrace();\n");
	sb.append("     throw e ; \n");
	sb.append("   }\n");
    return Rythm.render(sb.toString(), index);
  }

  public String getAction() {
    return "switchToFrameByIndex";
  }
  
}
