/**
 * 
 */
package org.ats.common.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 */
public class HttpClientUtil {
  public static String fetch(HttpClient httpclient, String uri) throws IOException {
    return getContentBodyAsString(execute(httpclient, uri));
  }

  public static HttpResponse post(HttpClient httpclient, String uri, Map<String, String> parameters) throws IOException {
    if (httpclient == null) {
      throw new NullPointerException();
    }
    if (uri == null) {
      throw new NullPointerException();
    }
    HttpPost post = new HttpPost(uri);
    List<NameValuePair> list = new ArrayList<NameValuePair>();
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    }
    post.setEntity(new UrlEncodedFormEntity(list));
    return httpclient.execute(post);
  }

  public static HttpResponse execute(HttpClient httpclient, String uri) throws IOException {
    if (httpclient == null) {
      throw new NullPointerException();
    }
    if (uri == null) {
      throw new NullPointerException();
    }
    HttpGet get = new HttpGet(uri);
    return httpclient.execute(get);
  }


  public static byte[] getContentBodyAsByteArray(HttpResponse res) throws IOException {
    InputStream is = res.getEntity().getContent();
    BufferedInputStream bis = new BufferedInputStream(is);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buff = new byte[1024];
    for (int l = bis.read(buff); l != -1; l = bis.read(buff)) {
      baos.write(buff, 0, l);
    }
    return baos.toByteArray();
  }

  public static String getContentBodyAsString(HttpResponse res) throws IOException {
    return new String(getContentBodyAsByteArray(res), "UTF-8");
  }

  public static String getContentType(HttpResponse res) {
    Header header = res.getFirstHeader("Content-Type");
    if (header == null) {
      return "unknown/unknown";
    }
    String value = header.getValue();
    return value.substring(0, value.lastIndexOf(';'));
  }
}
