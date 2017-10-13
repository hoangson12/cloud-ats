/**
 * 
 */
package org.ats.services.keyword.action;

import java.io.IOException;

/**
 * @author TrinhTV3
 *
 *         Email: TrinhTV3@fsoft.com.vn
 */

@SuppressWarnings("serial")
public class AcceptAlert extends AbstractAction {

	@Override
	public String transform() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("try { \n");
		sb.append("    wd.switchTo().alert().accept();\n");
		sb.append("    System.out.println(\"[End][Step]\"); \n");
		sb.append("   } catch (Exception e) { \n");
		sb.append("     wd.getScreenshotAs(FILE).renameTo(new File(\"target/error_\"+System.currentTimeMillis()+\"_acceptAlert.png\"));\n");
		sb.append("     e.printStackTrace();\n");
		sb.append("     throw e ; \n");
		sb.append("   }\n");
		return sb.toString() ;

	}

	@Override
	public String getAction() {
		return "acceptAlert";
	}

}
