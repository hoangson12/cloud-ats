package org.ats.service;

import org.ats.service.report.ReportService;
import org.ats.service.report.ReportUploadService;
import org.ats.service.report.function.ReportTestNgFactory;
import org.ats.service.report.function.ReportTestNgUploadFactory;
import org.ats.service.report.jmeter.ReportJmeterFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ReportModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ReportService.class);
    bind(ReportUploadService.class);
    
    install(new FactoryModuleBuilder().build(ReportJmeterFactory.class));
    install(new FactoryModuleBuilder().build(ReportTestNgFactory.class));
    install(new FactoryModuleBuilder().build(ReportTestNgUploadFactory.class));
  }
}
