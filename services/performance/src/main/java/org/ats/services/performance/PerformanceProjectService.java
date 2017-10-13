/**
 * 
 */
package org.ats.services.performance;

import java.util.logging.Logger;

import org.ats.services.OrganizationContext;
import org.ats.services.data.MongoDBService;
import org.ats.services.organization.SpaceService;
import org.ats.services.organization.TenantService;
import org.ats.services.organization.UserService;
import org.ats.services.organization.base.AbstractMongoCRUD;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Jun 9, 2015
 */
@Singleton
public class PerformanceProjectService extends AbstractMongoCRUD<PerformanceProject> {
  
  private final String COL_NAME = "performance-project";
  
  @Inject
  private PerformanceProjectFactory factory;
  
  @Inject
  PerformanceProjectService(MongoDBService mongo, Logger logger) {
    this.col = mongo.getDatabase().getCollection(COL_NAME);
    this.logger = logger;
    
    //create text index
    this.createTextIndex("name");
    
    this.col.createIndex(new BasicDBObject("created_date", 1));
    this.col.createIndex(new BasicDBObject("creator._id", 1));
    this.col.createIndex(new BasicDBObject("tenant._id", 1));
    this.col.createIndex(new BasicDBObject("space._id", 1));
  }

  @Override
  public PerformanceProject transform(DBObject source) {
    
    BasicDBObject object = (BasicDBObject) source.get("creator");
    PerformanceProject project = factory.create((String) source.get("name"), source.get("mix_id") != null ? (String) source.get("mix_id") : "");
    project.put("created_date", source.get("created_date"));
    project.put("active", source.get("active"));
    project.put("_id", source.get("_id"));
    project.put("status", source.get("status"));
    project.put("creator", object);
//    project.put("creator", source.get("creator"));
//    project.put("space", source.get("space"));
//    project.put("tenant", source.get("tenant"));
    return project;
  }

}
