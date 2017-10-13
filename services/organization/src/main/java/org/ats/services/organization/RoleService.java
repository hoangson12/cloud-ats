/**
 * 
 */
package org.ats.services.organization;

import java.util.List;
import java.util.logging.Logger;

import org.ats.services.data.MongoDBService;
import org.ats.services.event.Event;
import org.ats.services.event.EventFactory;
import org.ats.services.organization.base.AbstractMongoCRUD;
import org.ats.services.organization.entity.Role;
import org.ats.services.organization.entity.fatory.RoleFactory;

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
public class RoleService extends AbstractMongoCRUD<Role> {
  
  /** .*/
  private final String COL_NAME = "org-role";
  
  /** .*/
  @Inject
  private RoleFactory factory;
  
  /** .*/
  @Inject
  private EventFactory eventFactory;
  
  @Inject
  RoleService(MongoDBService mongo, Logger logger) {
    this.col = mongo.getDatabase().getCollection(COL_NAME);
    this.logger = logger;
    
    this.createTextIndex("name");
    this.col.createIndex(new BasicDBObject("created_date", 1));
    this.col.createIndex(new BasicDBObject("space._id", 1));
  }
  
  @Override
  public void delete(Role obj) {
    if (obj == null) return;
    super.delete(obj);
    Event event = eventFactory.create(obj, "delete-role");
    event.broadcast();
  }
  
  @Override
  public void delete(String id) {
    Role role = get(id);
    this.delete(role);
  }
  
  @Override
  public void active(Role obj) {
    if (obj == null) return;
    super.active(obj);
    Event event = eventFactory.create(obj, "active-role");
    event.broadcast();
  }
  
  @Override
  public void active(String id) {
    Role role = get(id);
    this.active(role);
  }
  
  @Override
  public void inActive(Role obj) {
    if (obj == null) return;
    super.inActive(obj);
    Event event = eventFactory.create(obj, "in-active-role");
    event.broadcast();
  }
  
  @Override
  public void inActive(String id) {
    Role role = get(id);
    this.inActive(role);
  }

  public Role transform(DBObject source) {
    Role role = factory.create((String) source.get("name"));
    role.put("_id", source.get("_id"));
    role.put("created_date", source.get("created_date"));
    role.put("active", source.get("active"));
    role.put("desc", source.get("desc"));
    role.put("space", source.get("space"));
    role.put("permissions", source.get("permissions"));
    return role;
  }

  public void restoreRole(List<DBObject> list) {
    this.col.insert(list);
  }
}
