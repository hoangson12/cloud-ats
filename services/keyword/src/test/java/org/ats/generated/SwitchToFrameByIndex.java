 

package org.ats.generated;

import java.text.ParseException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.DataProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.*;
import static org.openqa.selenium.OutputType.*;

public class SwitchToFrameByIndex {

  private RemoteWebDriver wd;
  
  @BeforeClass
  public void beforeClass() throws Exception {
  System.out.println("[Start][Suite]{\"name\": \"SwitchToFrameByIndex\", \"id\": \"493c3d5a-10ba-4e39-bdd0-526d80579347\", \"jobId\" : \"\", \"timestamp\": \""+System.currentTimeMillis()+"\"}");
  }
   
  @AfterClass
  public void afterClass() throws Exception {
  System.out.println("[End][Suite]{\"name\": \"SwitchToFrameByIndex\", \"id\": \"493c3d5a-10ba-4e39-bdd0-526d80579347\", \"jobId\" : \"\", \"timestamp\": \""+System.currentTimeMillis()+"\"}");
  }

  @BeforeMethod
  public void setUp() throws Exception {
    wd = new FirefoxDriver();
    wd.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    wd.manage().window().maximize();
  }
   
  @AfterMethod
  public void tearDown() {
    wd.quit();
  }
  
  
  @Test
  public void test2162c92d() throws Exception {
    System.out.println("[Start][Case]{\"name\": \"test\", \"id\": \"2162c92d-3a14-4a44-9d0d-9f531c0745f1\", \"timestamp\": \""+System.currentTimeMillis()+"\"} "); 

    System.out.println("[Start][Step]{\"keyword_type\":\"get \",\"url\":\"http://seleniumbuilder.github.io/se-builder/test/frames.html\",\"timestamp\": \""+System.currentTimeMillis()+"\",\"params\":[\"url\"]} "); 
    try {
wd.get("http://seleniumbuilder.github.io/se-builder/test/frames.html");
System.out.println("[End][Step]");
} catch (Exception e) {
wd.getScreenshotAs(FILE).renameTo(new File("target/error_"+System.currentTimeMillis()+"_get.png"));
e.printStackTrace();
throw e ;
}


    System.out.println("[Start][Step]{\"keyword_type\":\"switchToFrameByIndex \",\"index\":\"1\",\"timestamp\": \""+System.currentTimeMillis()+"\",\"params\":[\"index\"]} "); 
    try {
wd = (FirefoxDriver) wd.switchTo().frame(1);
System.out.println("[End][Step]");
} catch (Exception e) {
wd.getScreenshotAs(FILE).renameTo(new File("target/error_"+System.currentTimeMillis()+"_switchToFrameByIndex.png"));
e.printStackTrace();
throw e ;
}


    System.out.println("[Start][Step]{\"keyword_type\":\"clickElement \",\"locator\":{\"type\":\"link text\",\"value\":\"Next\"},\"timestamp\": \""+System.currentTimeMillis()+"\",\"params\":[\"locator\"]} "); 
    try {
wd.findElement(By.linkText("Next")).click();
System.out.println("[End][Step]");
} catch (Exception e) {
wd.getScreenshotAs(FILE).renameTo(new File("target/error_"+System.currentTimeMillis()+"_clickElement.png"));
e.printStackTrace();
throw e ;
}


    System.out.println("[Start][Step]{\"keyword_type\":\"assertTextPresent \",\"text\":\"Content Two\",\"timestamp\": \""+System.currentTimeMillis()+"\",\"params\":[\"text\"]} "); 
    try {
assertTrue( wd.findElement(By.tagName("html")).getText().contains("Content Two"));
System.out.println("[End][Step]");
} catch (AssertionError ae) {
wd.getScreenshotAs(FILE).renameTo(new File("target/error_"+System.currentTimeMillis()+"_assertTextPresent.png"));
ae.printStackTrace();
throw ae ;
}

    System.out.println("[End][Case]"); 
  }

  public static boolean isAlertPresent(RemoteWebDriver wd) {
    try {
      wd.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }
}