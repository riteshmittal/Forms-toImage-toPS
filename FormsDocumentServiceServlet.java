/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.community.core.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.cpdf.api.ConvertPdfException;
import com.adobe.fd.cpdf.api.ConvertPdfService;
import com.adobe.fd.cpdf.api.ToImageOptionsSpec;
import com.adobe.fd.cpdf.api.ToPSOptionsSpec;
import com.adobe.fd.cpdf.api.enumeration.ImageConvertFormat;
import com.adobe.fd.cpdf.api.enumeration.PSLevel;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Solr Index Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/formsService" })
public class FormsDocumentServiceServlet extends SlingSafeMethodsServlet {

	@Reference
	private ConvertPdfService cpdfService;

	@OSGiService
	private ConvertPdfService cpdfServic2e;

	private String documentPath = "/content/dam/formsanddocuments/sample.pdf";

	private static final long serialVersionUid = 1L;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");

		toPSAPI();
		toImageAPI();

		resp.getWriter().write("File generated successfully!");

	}

	private void toImageAPI() throws IOException {
		Document inputPDF = new Document(documentPath);
		ToImageOptionsSpec toImageOptions = new ToImageOptionsSpec();
		toImageOptions.setImageConvertFormat(ImageConvertFormat.JPEG);

		List<Document> convertedImages;
		try {
			convertedImages = cpdfService.toImage(inputPDF, toImageOptions);
			for (int i = 0; i < convertedImages.size(); i++) {
				Document pageImage = convertedImages.get(i);
				pageImage.copyToFile(new File("C:/temp/out_" + (i + 1) + ".jpeg"));
			}
		} catch (ConvertPdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void toPSAPI() throws IOException {
		Document inputPDF = new Document(documentPath);
		ToPSOptionsSpec toPSOptions = new ToPSOptionsSpec();
		toPSOptions.setPsLevel(PSLevel.LEVEL_3);
		Document convertedPS;
		try {
			convertedPS = cpdfService.toPS(inputPDF, toPSOptions);
			convertedPS.copyToFile(new File("C:/temp/out.ps"));
		} catch (ConvertPdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
