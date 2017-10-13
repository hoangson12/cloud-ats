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
public class CssSelectorLocator extends AbstractLocator {

  public CssSelectorLocator(Value locator) {
    super(locator);
  }

  public String getName() {
    return "css selector";
  }

  @Override
  public String transform() throws IOException {
    return Rythm.render("By.cssSelector(@locator)", locator.transform());
  }

}
