/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

import org.ats.services.keyword.Value;
import org.ats.services.keyword.VariableFactory;
import org.ats.services.keyword.VariableFactory.DataType;
import org.rythmengine.Rythm;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 16, 2015
 */
@SuppressWarnings("serial")
public class StoreCookieByName extends AbstractAction {

  private Value name;
  
  private String variable;
  
  private VariableFactory factory;
  
  public StoreCookieByName(Value name, String variable, VariableFactory factory) {
    this.name = name;
    this.variable = variable;
    this.factory = factory;
  }
  
  public String transform() throws IOException {
    StringBuilder sb = new StringBuilder(factory.getVariable(DataType.STRING, variable));
    sb.append(" = wd.manage().getCookieNamed(").append(name).append(").getValue();\n");
    sb.append("     System.out.println(\"[End][Step]\"); \n");
    return Rythm.render(sb.toString(), name.transform());
  }

  public String getAction() {
    return "storeCookieByName";
  }

}
