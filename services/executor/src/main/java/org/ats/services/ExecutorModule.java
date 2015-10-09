/**
 * 
 */
package org.ats.services;

import org.ats.services.executor.ExecutorService;
import org.ats.services.executor.ExecutorUploadService;
import org.ats.services.executor.job.KeywordJobFactory;
import org.ats.services.executor.job.KeywordJobReference;
import org.ats.services.executor.job.KeywordUploadJobFactory;
import org.ats.services.executor.job.KeywordUploadJobReference;
import org.ats.services.executor.job.PerformanceJobFactory;
import org.ats.services.executor.job.PerformanceJobReference;
import org.ats.services.organization.entity.fatory.ReferenceFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Jul 21, 2015
 */
public class ExecutorModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ExecutorService.class);
    bind(ExecutorUploadService.class);
    
    install(new FactoryModuleBuilder().build(PerformanceJobFactory.class));
    install(new FactoryModuleBuilder().build(KeywordJobFactory.class));
    install(new FactoryModuleBuilder().build(KeywordUploadJobFactory.class));
    
    install(new FactoryModuleBuilder().build(new TypeLiteral<ReferenceFactory<KeywordUploadJobReference>>(){}));
    install(new FactoryModuleBuilder().build(new TypeLiteral<ReferenceFactory<KeywordJobReference>>(){}));
    install(new FactoryModuleBuilder().build(new TypeLiteral<ReferenceFactory<PerformanceJobReference>>(){}));
  }

}
