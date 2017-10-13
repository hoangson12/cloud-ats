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
 * Apr 9, 2015
 */
@SuppressWarnings("serial")
public class PartialLinkTextLocator extends AbstractLocator {

  public PartialLinkTextLocator(Value locator) {
    super(locator);
  }

  public String getName() {
    return "partial link text";
  }

  @Override
  public String transform() throws IOException {
    return Rythm.render("By.partialLinkText(@locator)", locator.transform());
  }

}
