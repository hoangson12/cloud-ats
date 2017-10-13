/**
 * 
 */
package org.ats.services.keyword.locator;

import java.io.IOException;

import org.ats.services.keyword.Value;
import org.rythmengine.Rythm;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 8, 2015
 */
@SuppressWarnings("serial")
public class IDLocator extends AbstractLocator {

  public IDLocator(Value locator) {
    super(locator);
  }

  public String getName() {
    return "id";
  }

  @Override
  public String transform() throws IOException {
    return Rythm.render("By.id(@locator)", locator.transform());
  }

  
}
