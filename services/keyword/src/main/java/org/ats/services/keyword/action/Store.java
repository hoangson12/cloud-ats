/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

import org.ats.services.keyword.Value;
import org.ats.services.keyword.VariableFactory;
import org.ats.services.keyword.VariableFactory.DataType;

/**
 * @author TrinhTV3
 *
 * Email: TrinhTV3@fsoft.com.vn
 */
@SuppressWarnings("serial")
public class Store extends AbstractAction {

  /** .*/
  private Value text;
  
  /** .*/
  private String variable;
  
  /** .*/
  private VariableFactory factory;
  
  public Store(Value text, String variable, VariableFactory factory) {
    this.text = text;
    this.variable = variable;
    this.factory = factory;
  }
  public String transform() throws IOException {
//    StringBuilder sb = new StringBuilder(factory.getVariable(DataType.STRING, variable)).append(" = ");
//    sb.append(text.transform());
//    sb.append(";\n");
    StringBuilder sb = new StringBuilder();
	sb.append("try { \n");
	sb.append(factory.getVariable(DataType.STRING, variable)).append(" = ");
	sb.append(text.transform());
	sb.append(";\n");
	sb.append("   } catch (Exception e) { \n");
	sb.append("     SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy/MM/dd HH:mm:ss\");\n");
	sb.append("     long time = dateFormat.parse(dateFormat.format(new Date())).getTime();\n");
	sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/\"+time+\".png\"));\n");
	sb.append("     throw e ; \n");
	sb.append("   }\n");
    return sb.toString();
  }

  public String getAction() {
    return "store";
  }

}
