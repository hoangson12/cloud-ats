/**
 * 
 */
package org.ats.services.keyword;

import java.io.IOException;

import com.mongodb.BasicDBObject;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 8, 2015
 */
@SuppressWarnings("serial")
public abstract class AbstractTemplate extends BasicDBObject {

  public abstract String transform() throws IOException;
}
