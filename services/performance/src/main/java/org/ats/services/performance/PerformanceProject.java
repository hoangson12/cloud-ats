/**
 * 
 */
package org.ats.services.performance;

import java.util.Date;
import java.util.UUID;

import org.ats.services.OrganizationContext;
import org.ats.services.organization.entity.AbstractEntity;
import org.ats.services.organization.entity.User;
import org.ats.services.organization.entity.fatory.ReferenceFactory;
import org.ats.services.organization.entity.reference.SpaceReference;
import org.ats.services.organization.entity.reference.TenantReference;
import org.ats.services.organization.entity.reference.UserReference;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.BasicDBObject;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Jun 9, 2015
 */
@SuppressWarnings("serial")
public class PerformanceProject extends AbstractEntity<PerformanceProject> {
  
  private ReferenceFactory<TenantReference> tenantRefFactory;
  private ReferenceFactory<UserReference> userRefFactory;
  private ReferenceFactory<SpaceReference> spaceRefFactory;
  
  public static enum Status {
    READY, RUNNING
  }
  
  @Inject
  PerformanceProject(ReferenceFactory<TenantReference> tenantRefFactory, 
      ReferenceFactory<UserReference> userRefFactory,
      ReferenceFactory<SpaceReference> spaceRefFactory,
      OrganizationContext context,
      @Assisted("name") String name, @Assisted("mix_id") String mix_id) {
    
    this.tenantRefFactory = tenantRefFactory;
    this.userRefFactory = userRefFactory;
    this.spaceRefFactory = spaceRefFactory;
    
    if (context == null || context.getUser() == null) 
      throw new IllegalStateException("You need logged in system to creat new functional project");
    
    User user = context.getUser();
    this.put("name", name);
    this.put("creator", new BasicDBObject("_id", user.getEmail()));
    this.put("mix_id", mix_id);
    if (context.getSpace() != null) {
      this.put("space", new BasicDBObject("_id", context.getSpace().getId()));
    }
    
    this.put("tenant", user.getTanent().toJSon());
    this.put("created_date", new Date());
    this.setActive(true);
    this.put("_id", UUID.randomUUID().toString());
    //this.put("scripts", null);
    setStatus(Status.READY);
  }
  
  public String getName() {
    return this.getString("name");
  }
  
  public String getId() {
    return this.getString("_id");
  }
  
  public void setMixId(String id) {
    this.put("mix_id", id);
  }
  
  public String getMixId() {
    return this.getString("mix_id");
  }
  
  public Status getStatus() {
    return this.get("status") != null ? Status.valueOf(this.getString("status")) : Status.READY;
  }
  
  public void setStatus(Status status) {
    this.put("status", status.toString());
  }
  
  public UserReference getCreator() {
    return userRefFactory.create(((BasicDBObject)this.get("creator")).getString("_id"));
  }
  
  public SpaceReference getSpace() {
    if (this.get("space") == null) return null;
    BasicDBObject obj = (BasicDBObject) this.get("space");
    return spaceRefFactory.create(obj.getString("_id"));
  }
  
  public TenantReference getTenant() {
    return tenantRefFactory.create(((BasicDBObject)this.get("tenant")).getString("_id"));
  }
  
}
