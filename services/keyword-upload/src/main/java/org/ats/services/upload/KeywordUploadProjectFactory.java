/**
 * 
 */
package org.ats.services.upload;

import org.ats.services.OrganizationContext;

import com.google.inject.assistedinject.Assisted;

/**
 * @author NamBV2
 *
 * Sep 17, 2015
 */
public interface KeywordUploadProjectFactory {
  public KeywordUploadProject create(@Assisted("context") OrganizationContext context, @Assisted("name") String name);
}
