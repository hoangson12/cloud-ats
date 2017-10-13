/**
 * 
 */
package org.ats.services.organization.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ats.common.PageList;
import org.ats.common.SetBuilder;
import org.ats.services.data.common.MongoPageList;
import org.ats.services.data.common.Reference;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Mar 10, 2015
 */
public abstract class AbstractMongoCRUD<T extends DBObject> implements MongoCRUD<T> {

  /** .*/
  protected Logger logger;
  
  /** .*/
  protected DBCollection col;
  
  public void createTextIndex(String... fields) {
    String colName = col.getName();
    DBObject keys = new BasicDBObject();
    for (String field : fields) {
      keys.put(field, "text");
    }
    col.createIndex(keys);
    logger.log(Level.INFO, colName + " collection has created text index for " + keys);
  }
  
  public long count() {
    return this.col.count();
  }
  
  @SuppressWarnings("unchecked")
  public void create(T... obj) {
    this.col.insert(obj);
  }
  
  public void create(List<DBObject> list) {
    this.col.insert(list);
  }
  
  public void update(T obj) {
    if (obj == null) return;
    this.col.save(obj);
  }

  public void bulkUpdate(DBObject query, DBObject update) {
    BulkWriteOperation builder = this.col.initializeUnorderedBulkOperation(); 
    builder.find(query).update(update);
    builder.execute();
  }

  public void delete(T obj) {
    if (obj == null) return;
    this.col.remove(obj);
  }
  
  public void deleteBy(DBObject query) {
    this.col.remove(query);
  }
  
  public void delete(String id) {
    this.col.remove(new BasicDBObject("_id", id)); 
  }
  
  public void active(String id) {
    this.active(get(id));
  }
  
  public void active(T obj) {
    if (obj == null) return;
    obj.put("active", true);
    this.update(obj);
  }
  
  public void inActive(String id) {
    this.inActive(get(id));
  }
  
  public void inActive(T obj) {
    if (obj == null) return;
    obj.put("active", false);
    this.update(obj);
  }
  
  public MapReduceOutput mapreduce(MapReduceCommand cmd) {
    return this.col.mapReduce(cmd);
  }
  
  public T get(String id) {
    DBObject source = this.col.findOne(new BasicDBObject("_id", id));
    return source == null ? null : transform(source);
  }
  
 public T get(String id, String... mixins) {
   return get(id, new SetBuilder<String>(mixins).build());
 }
  
  public T get(String id, Set<String> mixins) {
    DBObject source = this.col.findOne(new BasicDBObject("_id", id));
    if (source == null) return null;
    
    T entity = transform(source);
    for (String mixin : mixins) {
      entity.put(mixin, source.get(mixin));
    }
    return entity;
  }
  
  public PageList<T> list() {
    return list(10);
  }

  public PageList<T> list(int pageSize) {
    DBObject query = new BasicDBObject();
    return buildPageList(pageSize, col, query);
  }

  public PageList<T> query(DBObject query) {
    return query(query, 10);
  }

  public PageList<T> query(DBObject query, int pageSize) {
    return buildPageList(pageSize, col, query);
  }
  
  public PageList<T> search(String text) {
    DBObject query = new BasicDBObject("$text", new BasicDBObject("$search", text));
    return buildPageList(10, col, query);
  }
  
  public PageList<T> findIn(String field, Reference<?> ref) {
    BasicDBObject query = new BasicDBObject(field, new BasicDBObject("$elemMatch", ref.toJSon()));
    return query(query);
  }
  
  
  @SuppressWarnings("serial")
  protected PageList<T> buildPageList(int pageSize, DBCollection col, DBObject query) {
    return new MongoPageList<T>(pageSize, col, query) {
      @Override
      protected List<T> get(int from) {
        DBObject sortable = null;
        
        if (this.sortableKeys != null && this.sortableKeys.size() > 0) {
          sortable = new BasicDBObject();
          for (Map.Entry<String, Boolean> entry : this.sortableKeys.entrySet()) {
            sortable.put(entry.getKey(), entry.getValue() ? 1 : -1);
          }
        }
        
        DBCursor cursor = null;
        if (sortable != null) {
          cursor = this.col.find(query).sort(sortable).skip(from).limit(pageSize);
        } else {
          cursor = this.col.find(query).skip(from).limit(pageSize);
        }
        
        List<T> list = new ArrayList<T>();
        while(cursor.hasNext()) {
          DBObject source = cursor.next();
          T entity = transform(source);
          if (this.mixins != null && this.mixins.size() > 0) {
            for (String mixin : this.mixins) {
              entity.put(mixin, source.get(mixin));
            }
          }
          list.add(entity);
        }
        return list;
      }
    };
  }
}
