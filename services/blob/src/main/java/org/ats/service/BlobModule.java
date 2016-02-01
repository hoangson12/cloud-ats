/**
 * TrinhTV3@fsoft.com.vn
 */
package org.ats.service;

import org.ats.service.blob.FileService;

import com.google.inject.AbstractModule;

/**
 * @author TrinhTV3
 *
 */
public class BlobModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(FileService.class);
    
  }

}
