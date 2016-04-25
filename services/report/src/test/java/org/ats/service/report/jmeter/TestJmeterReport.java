package org.ats.service.report.jmeter;

import java.util.Arrays;

import org.ats.common.PageList;
import org.ats.service.ReportModule;
import org.ats.service.report.Report;
import org.ats.service.report.ReportService;
import org.ats.service.report.ReportService.Type;
import org.ats.services.DataDrivenModule;
import org.ats.services.ExecutorModule;
import org.ats.services.GeneratorModule;
import org.ats.services.KeywordServiceModule;
import org.ats.services.OrganizationServiceModule;
import org.ats.services.PerformanceServiceModule;
import org.ats.services.data.DatabaseModule;
import org.ats.services.data.MongoDBService;
import org.ats.services.event.EventModule;
import org.ats.services.event.EventService;
import org.ats.services.executor.ExecutorService;
import org.ats.services.executor.job.AbstractJob;
import org.ats.services.executor.job.AbstractJob.Status;
import org.ats.services.executor.job.PerformanceJob;
import org.ats.services.iaas.IaaSService;
import org.ats.services.iaas.IaaSServiceProvider;
import org.ats.services.iaas.VMachineServiceModule;
import org.ats.services.organization.base.AuthenticationService;
import org.ats.services.organization.entity.Tenant;
import org.ats.services.organization.entity.User;
import org.ats.services.organization.entity.fatory.ReferenceFactory;
import org.ats.services.organization.event.AbstractEventTestCase;
import org.ats.services.performance.JMeterFactory;
import org.ats.services.performance.JMeterSampler;
import org.ats.services.performance.JMeterScript;
import org.ats.services.performance.JMeterScriptReference;
import org.ats.services.performance.JMeterScriptService;
import org.ats.services.performance.PerformanceProject;
import org.ats.services.performance.PerformanceProjectFactory;
import org.ats.services.performance.PerformanceProjectService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class TestJmeterReport extends AbstractEventTestCase {

  private AuthenticationService<User> authService;

  private Tenant tenant;
  private User user;

  private PerformanceProjectFactory perfFactory;
  private PerformanceProjectService perfService;
  private ReferenceFactory<JMeterScriptReference> jmeterScriptRef;
  private JMeterScriptService jmeterService;

  private ExecutorService executorService;
  
  private IaaSService openstackService;
  
  private IaaSServiceProvider iaasProvider;

  private ReportService reportService;

  @BeforeClass
  public void init() throws Exception {
    System.setProperty(EventModule.EVENT_CONF, "src/test/resources/event.conf");
    
    VMachineServiceModule vmModule = new VMachineServiceModule("src/test/resources/iaas.conf");
    vmModule.setProperty("org.ats.cloud.iaas", "org.ats.services.iaas.OpenStackService");

    this.injector = Guice.createInjector(
        new DatabaseModule(), 
        new EventModule(), 
        new OrganizationServiceModule(), 
        new DataDrivenModule(),
        new KeywordServiceModule(), 
        new PerformanceServiceModule(),
        new GeneratorModule(), 
        vmModule,
        new ExecutorModule(), new ReportModule());

    this.mongoService = injector.getInstance(MongoDBService.class);
    this.mongoService.dropDatabase();

    this.authService = injector.getInstance(Key.get(new TypeLiteral<AuthenticationService<User>>() {
    }));

    // performance
    this.perfFactory = injector.getInstance(PerformanceProjectFactory.class);
    this.perfService = injector.getInstance(PerformanceProjectService.class);
    this.jmeterScriptRef = this.injector.getInstance(Key.get(new TypeLiteral<ReferenceFactory<JMeterScriptReference>>() {
    }));
    this.jmeterService = this.injector.getInstance(JMeterScriptService.class);

    this.executorService = injector.getInstance(ExecutorService.class);
    
    this.iaasProvider = injector.getInstance(IaaSServiceProvider.class);
    this.openstackService = iaasProvider.get();

    // start event service
    this.eventService = injector.getInstance(EventService.class);
    this.eventService.setInjector(injector);
    this.eventService.start();

    initService();

    this.tenant = tenantFactory.create("fsoft-testonly");
    this.tenantService.create(this.tenant);

    this.openstackService.addCredential("admin", "admin", "ADMIN_PASS");
    this.openstackService.initTenant(tenantRefFactory.create(this.tenant.getId()));

    this.openstackService.addCredential("fsoft-testonly");

    this.user = userFactory.create("haint@cloud-ats.net", "Hai", "Nguyen");
    this.user.setTenant(tenantRefFactory.create(this.tenant.getId()));
    this.user.setPassword("12345");
    this.userService.create(this.user);

    this.authService.logIn("haint@cloud-ats.net", "12345");

    this.reportService = injector.getInstance(ReportService.class);
  }

  @AfterClass
  public void shutdown() throws Exception {
    this.openstackService.destroyTenant(tenantRefFactory.create(this.tenant.getId()));
    this.mongoService.dropDatabase();
  }

  @Test
  public void testParseJtlContent() throws Exception {

    PerformanceProject project = perfFactory.create("Test Performance", "");

    JMeterFactory factory = new JMeterFactory();
    JMeterSampler loginPost = factory.createHttpPost("Login codeproject post",
        "https://www.codeproject.com/script/Membership/LogOn.aspx?rp=%2f%3floginkey%3dfalse", "kakalot", 0, factory.createArgument("FormName", "MenuBarForm"),
        factory.createArgument("Email", "kakalot8x08@gmail.com"), factory.createArgument("Password", "tititi"));

    JMeterSampler gotoArticle = factory.createHttpGet("Go to top article", "http://www.codeproject.com/script/Articles/TopArticles.aspx?ta_so=5", null, 0);

    JMeterScript loginScript = factory.createJmeterScript("LoginCodeProject", 1, 20, 5, false, 0, project.getId(), "haint@cloudats.net", loginPost);

    jmeterService.create(loginScript);

    JMeterScript gotoArticleScript = factory.createJmeterScript("GoToArticle", 1, 20, 5, false, 0, project.getId(), "haint@cloudats.net", gotoArticle);

    jmeterService.create(gotoArticleScript);

    perfService.create(project);

    PerformanceJob job = executorService.execute(project,
        Arrays.asList(jmeterScriptRef.create(loginScript.getId()), jmeterScriptRef.create(gotoArticleScript.getId())));

    Assert.assertEquals(job.getStatus(), Status.Queued);
    Assert.assertNull(job.getTestVMachineId());

    job = (PerformanceJob) waitUntilJobFinish(job);

    job = (PerformanceJob) executorService.get(job.getId());

    Assert.assertEquals(job.getStatus(), AbstractJob.Status.Completed);
    Assert.assertNotNull(job.getRawDataOutput());
    Assert.assertEquals(job.getRawDataOutput().size(), 2);
    Assert.assertTrue(job.getRawDataOutput().keySet().contains(loginScript.getId()));
    Assert.assertTrue(job.getRawDataOutput().keySet().contains(gotoArticleScript.getId()));

    {
      PageList<Report> list = reportService.getList(job.getId(), Type.PERFORMANCE, loginScript.getId());
      System.out.println("LIST:" + list.count());
      Assert.assertTrue(list.count() > 0);
    }
    {
      PageList<Report> list = reportService.getList(job.getId(), Type.PERFORMANCE, gotoArticleScript.getId());
      System.out.println("LIST:" + list.count());
      Assert.assertTrue(list.count() > 0);
    }

  }

  private AbstractJob<?> waitUntilJobFinish(AbstractJob<?> job) throws InterruptedException {
    while (job.getStatus() != Status.Completed) {
      job = executorService.get(job.getId());
      Thread.sleep(3000);
    }
    return job;
  }
}
