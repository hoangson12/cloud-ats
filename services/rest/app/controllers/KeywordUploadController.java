/**
 * 
 */
package controllers;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.ats.common.MapBuilder;
import org.ats.common.PageList;
import org.ats.services.OrganizationContext;
import org.ats.services.executor.ExecutorUploadService;
import org.ats.services.executor.job.AbstractJob;
import org.ats.services.executor.job.KeywordUploadJob;
import org.ats.services.organization.acl.Authenticated;
import org.ats.services.upload.KeywordUploadProject;
import org.ats.services.upload.KeywordUploadProjectFactory;
import org.ats.services.upload.KeywordUploadProjectService;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import actions.CorsComposition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

/**
 * @author NamBV2
 *
 *         Sep 17, 2015
 */
@CorsComposition.Cors
@Authenticated
public class KeywordUploadController extends Controller {

  @Inject
  private KeywordUploadProjectFactory projectFactory;

  @Inject
  private OrganizationContext context;

  @Inject
  private KeywordUploadProjectService keywordUploadService;

  @Inject
  private ExecutorUploadService executorUploadService;

  private static final int BUFFER_SIZE = 4096;

  private SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm");

  public Result get(String projectId) {
    KeywordUploadProject project = keywordUploadService.get(projectId);

    if (project == null)
      return status(404);

    project.put("type", "keyword");
    KeywordUploadProject upload = keywordUploadService.get(projectId, "raw");
    boolean rawExist = false;
    if (upload.getRawData() != null) {
      rawExist = true;
    }
    project.put("raw_exist", rawExist);
    PageList<AbstractJob<?>> jobList = executorUploadService.query(
        new BasicDBObject("project_id", projectId), 1);
    jobList.setSortable(new MapBuilder<String, Boolean>("created_date", false)
        .build());

    if (jobList.totalPage() > 0) {
      AbstractJob<?> lastJob = jobList.next().get(0);
      project.put("lastRunning", formater.format(lastJob.getCreatedDate()));
      project.put("log", lastJob.getLog());
      project.put("lastJobId", lastJob.getId());
    }
    return ok(Json.parse(project.toString()));
  }

  public Result list() {
    PageList<KeywordUploadProject> list = keywordUploadService.list();
    ArrayNode array = Json.newObject().arrayNode();
    while (list.hasNext()) {
      for (KeywordUploadProject project : list.next()) {
        project.put("type", "keyword");
        project.put("upload_project", true);

        BasicDBObject query = new BasicDBObject("project_id", project.getId())
            .append("status", AbstractJob.Status.Completed.toString());
        PageList<AbstractJob<?>> jobList = executorUploadService
            .query(query, 1);
        jobList.setSortable(new MapBuilder<String, Boolean>("created_date",
            false).build());

        if (jobList.totalPage() > 0) {
          AbstractJob<?> lastJob = jobList.next().get(0);
          project.put("lastRunning", formater.format(lastJob.getCreatedDate()));
          project.put("lastJobId", lastJob.getId());
          project.put("log", lastJob.getLog());
        }
        array.add(Json.parse(project.toString()));
      }
    }
    return ok(array);
  }

  public Result update() {
    JsonNode data = request().body().asJson();
    String id = data.get("id").asText();
    String name = data.get("name").asText();

    KeywordUploadProject project = keywordUploadService.get(id);

    if (name.equals(project.getString("name"))) {
      return status(304);
    }

    project.put("name", name);
    keywordUploadService.update(project);

    return status(202, id);
  }

  public Result delete() {

    String id = request().body().asText();

    KeywordUploadProject project = keywordUploadService.get(id);
    if (project == null) {
      return status(404);
    }

    executorUploadService.deleteBy(new BasicDBObject("project_id", id));
    keywordUploadService.delete(project);

    return status(200);
  }

  public Result run(String projectId) throws Exception {
    KeywordUploadProject project = keywordUploadService.get(projectId, "raw");
    if (project == null)
      return status(404);

    if (project.getStatus() == KeywordUploadProject.Status.RUNNING)
      return status(204);

    KeywordUploadJob job = executorUploadService.execute(project);
    return status(201, Json.parse(job.toString()));
  }

  public Result create() {
    JsonNode json = request().body().asJson();
    String name = json.get("name").asText();
    KeywordUploadProject project = projectFactory.create(context, name);
    keywordUploadService.create(project);
    return status(201, project.getId());
  }

  public Result report(String projectId, String jobId) {
    AbstractJob<?> job = executorUploadService.get(jobId);
    ObjectNode obj = Json.newObject();
    String result = "";
    obj.put("created_date", formater.format(job.getCreatedDate()));
    obj.put("log", job.getLog());
    obj.put("jobId", job.getId());
    if((job.getResult() != null) && ("SUCCESS".equals(job.getResult()))) {
      result = "Pass";
    } else {
      result = "Fail";
    }
    obj.put("result", result);
    return status(200, obj);
  }

  public Result listReport(String projectId) {
    PageList<AbstractJob<?>> jobList = executorUploadService.query(
        new BasicDBObject("project_id", projectId), 1);
    jobList.setSortable(new MapBuilder<String, Boolean>("created_date", false)
        .build());
    ArrayNode array = Json.newObject().arrayNode();
    String result = "";
    while (jobList.hasNext()) {
      for (AbstractJob<?> job : jobList.next()) {
        if (job.getLog() != null) {
          ObjectNode obj = Json.newObject();
          obj.put("created_date", formater.format(job.getCreatedDate()));
          obj.put("log", job.getLog());
          obj.put("jobId", job.getId());
          if((job.getResult() != null) && ("SUCCESS".equals(job.getResult()))) {
            result = "Pass";
          } else {
            result = "Fail";
          }
          obj.put("result", result);
          array.add(obj);
        }
      }
    }
    
    return ok(array);
  }

  public Result download(String projectId, String jobId) {
    AbstractJob<?> job = executorUploadService.get(jobId);
    BufferedWriter writer = null;
    String path = "/tmp/"+jobId;
    String log = job.getLog();
    String report = job.getRawDataOutput() != null ? job.getRawDataOutput()
        .get("report") : null;
    byte[] buffer = new byte[BUFFER_SIZE];
    int bytes_read;
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdir();
    }
    try {
      writer = new BufferedWriter(new FileWriter(path + "/" + "report.xml"));
      if (report != null) {
        writer.write(report);
      }
      writer.close();
      writer = new BufferedWriter(new FileWriter(path + "/" + "log.txt"));
      if (log != null) {
        writer.write(log);
      }
      writer.close();

      // compress file
      File fromDir = new File(path);
      List<String> filesListInDir = new ArrayList<String>();
      File[] files = fromDir.listFiles();

      for (File file : files) {
        if (file.isFile())
          filesListInDir.add(file.getAbsolutePath());
      }

      ZipOutputStream outDir = new ZipOutputStream(new FileOutputStream(path + ".zip"));
      for (String filePath : filesListInDir) {
        ZipEntry ze = new ZipEntry(filePath.substring(fromDir.getAbsolutePath()
            .length() + 1, filePath.length()));
        outDir.putNextEntry(ze);
        FileInputStream fis = new FileInputStream(filePath);
        while ((bytes_read = fis.read(buffer)) > 0) {
          outDir.write(buffer, 0, bytes_read);
        }
        outDir.closeEntry();
        fis.close();
      }
      outDir.close();

      // end compress file

    } catch (IOException e) {
      e.printStackTrace();
      return status(404);
    }
    
    String fileName = jobId+".zip";
    response().setContentType("application/x-download");
    response().setHeader("Content-disposition",
        "attachment; filename=report-"+fileName);
    return ok(new File(path + ".zip"));
  }

  public Result upload(String projectId) {
    MultipartFormData body = request().body().asMultipartFormData();
    MultipartFormData.FilePart typeFile = body.getFile("file");
    if (typeFile != null) {
      boolean formatProject = false;
      File file = typeFile.getFile();
      FileInputStream fileInputStream = null;
      byte[] bFile = new byte[(int) file.length()];

      // delete file pom.xml if it's exist before unzip
      File folderExist = new File("/tmp/" + projectId.substring(0, 8));
      if (folderExist.exists()) {
        File[] listOfFilesExist = folderExist.listFiles();
        for (File item : listOfFilesExist) {
          if (item.isFile() && "pom.xml".equals(item.getName())) {
            item.delete();
          }
        }
      }

      // unzip file
      String destDirectory = "/tmp/" + projectId.substring(0, 8);
      try {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
          destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
          String filePath = destDirectory + "/" + entry.getName();
          if (!entry.isDirectory()) {
            BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
              bos.write(bytesIn, 0, read);
            }
            bos.close();
          } else {
            File dir = new File(filePath);
            dir.mkdir();
          }
          zipIn.closeEntry();
          entry = zipIn.getNextEntry();
        }
        zipIn.close();

        // Check format of file upload
        File folder = new File("/tmp/" + projectId.substring(0, 8));
        File[] listOfFiles = folder.listFiles();

        for (File item : listOfFiles) {
          if (item.isFile()) {
            if ("pom.xml".equals(item.getName())) {
              formatProject = true;
            }
          }
        }
        if (!formatProject)
          return status(404);

        // convert file into array of bytes
        fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);
        fileInputStream.close();

        KeywordUploadProject project = keywordUploadService.get(projectId);
        if (project.getRawData() != null) {
          project.setRawData(null);
        }
        project.setRawData(bFile);
        keywordUploadService.update(project);

      } catch (Exception ex) {
        return status(404);
      }
      // end unzip file
      return status(201);
    } else {
      return badRequest();
    }
  }
}
