/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

import org.ats.services.keyword.VariableFactory;
import org.ats.services.keyword.VariableFactory.DataType;

/**
 * @author NamBV2
 *
 * Apr 17, 2015
 */
@SuppressWarnings("serial")
public class StoreAlertText extends AbstractAction{

  /** .*/
  private String variable;
  
  /** .*/
  private VariableFactory factory;
  
  public StoreAlertText(String variable, VariableFactory factory) {
    this.variable = variable;
    this.factory = factory;
  }
  
  public String transform() throws IOException {
    StringBuilder sb = new StringBuilder(factory.getVariable(DataType.STRING, variable)).append(" = \"\";\n");
	sb.append("try { \n");
	sb.append(variable);
	sb.append("     wd.switchTo().alert().getText();\n");
	sb.append("     System.out.println(\"[End][Step]\"); \n");
	sb.append("   } catch (Exception e) { \n");
	sb.append("     time = dateFormat.parse(dateFormat.format(new Date())).getTime();\n");
	sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/error_\"+time+\"_storeAlertText.png\"));\n");
	sb.append("     e.printStackTrace();\n");
	sb.append("     throw e ; \n");
	sb.append("   }\n");
    return sb.toString();
  }

  @Override
  public String getAction() {
    return "storeAlertText";
  }

}
