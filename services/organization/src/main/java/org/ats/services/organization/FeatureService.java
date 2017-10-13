/**
 * 
 */
package org.ats.services.organization;

import java.util.logging.Logger;

import org.ats.services.data.MongoDBService;
import org.ats.services.event.Event;
import org.ats.services.event.EventFactory;
import org.ats.services.organization.base.AbstractMongoCRUD;
import org.ats.services.organization.entity.Feature;
import org.ats.services.organization.entity.fatory.FeatureFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Mar 13, 2015
 */
@Singleton
public class FeatureService extends AbstractMongoCRUD<Feature> {
  
  /** .*/
  private final String COL_NAME = "org-feature";
  
  /** .*/
  @Inject
  private FeatureFactory factory;
  
  /** .*/
  @Inject
  private EventFactory eventFactory;
  
  @Inject
  FeatureService(MongoDBService mongo, Logger logger) {
    this.col = mongo.getDatabase().getCollection(COL_NAME);
    this.logger = logger;
    
    this.createTextIndex("_id");
    this.col.createIndex(new BasicDBObject("created_date", 1));
    this.col.createIndex(new BasicDBObject("actions._id", 1));
  }

  public Feature transform(DBObject source) {
    Feature feature = factory.create((String) source.get("_id"));
    feature.put("created_date", source.get("created_date"));
    feature.put("active", source.get("active"));
    feature.put("actions", source.get("actions"));
    return feature;
  }
  
  @Override
  public void delete(Feature obj) {
    if (obj == null) return;
    super.delete(obj);
    Event event = eventFactory.create(obj, "delete-feature");
    event.broadcast();
  }
  
  @Override
  public void delete(String id) {
    Feature feature = get(id);
    this.delete(feature);
  }
  
  @Override
  public void active(Feature obj) {
    if (obj == null) return;
    super.active(obj);
    Event event = eventFactory.create(obj, "active-feature");
    event.broadcast();
  }
  
  @Override
  public void active(String id) {
    Feature feature = get(id);
    this.active(feature);
  }
  
  @Override
  public void inActive(Feature obj) {
    if (obj == null) return;
    super.inActive(obj);
    Event event = eventFactory.create(obj, "in-active-feature");
    event.broadcast();
  }
  
  @Override
  public void inActive(String id) {
    Feature feature = get(id);
    this.inActive(feature);
  }

}
