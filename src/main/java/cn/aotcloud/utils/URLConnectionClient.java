/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.aotcloud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;


/**
 * Implementation of the Oltu OAuth HttpClient using URL Connection
 */
public class URLConnectionClient {
	
	protected static Logger logger = LoggerFactory.getLogger(URLConnectionClient.class);
	
    public Map<String, Object> executeBase(
    		String requestLocationUri, 
    		Map<String, String> requestHeaders, 
    		String requestBody, 
    		Map<String, String> headers, 
    		String requestMethod) throws RuntimeException {
    	
    	Map<String, Object> map = Maps.newHashMap();
        InputStream responseBody = null;
        URLConnection c = null;
        Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
        int responseCode;
        try {
            URL url = new URL(requestLocationUri);

            c = url.openConnection();
            responseCode = -1;
            if (c instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) c;

                if (headers != null && !headers.isEmpty()) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
                    }
                }

                if (requestHeaders != null) {
                    for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                        httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
                    }
                }

                if (StringUtils.isEmpty(requestMethod)) {
                    httpURLConnection.setRequestMethod("GET");
                } else {
                    httpURLConnection.setRequestMethod(requestMethod);
                    setRequestBody(requestBody, requestMethod, httpURLConnection);
                }

                httpURLConnection.connect();

                InputStream inputStream;
                responseCode = httpURLConnection.getResponseCode();
                if (responseCode == SC_BAD_REQUEST || responseCode == SC_UNAUTHORIZED) {
                    inputStream = httpURLConnection.getErrorStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }

                responseHeaders = httpURLConnection.getHeaderFields();
                responseBody = inputStream;
                
                map.put("responseBodyBytes", IOUtils.toByteArray(responseBody));
                map.put("contentType", c.getContentType());
                map.put("responseCode", responseCode);
                map.put("responseHeaders", responseHeaders);
                
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
        	if (c instanceof HttpURLConnection) {
        		((HttpURLConnection) c).disconnect();
        	}
        	if (responseBody != null) {
        		try {
					responseBody.close();
				} catch (IOException e) {
				}
        	}
        }
    }

    private void setRequestBody(String requestBody, String requestMethod, HttpURLConnection httpURLConnection) throws IOException {
        if (StringUtils.isEmpty(requestBody)) {
            return;
        }

        if (StringUtils.equalsAnyIgnoreCase(requestMethod, "POST", "PUT")) {
        	String encoding = httpURLConnection.getContentEncoding() != null ? httpURLConnection.getContentEncoding() : "UTF-8";
            httpURLConnection.setDoOutput(true);
            OutputStream ost = httpURLConnection.getOutputStream();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(ost, encoding));) {
            	pw.print(requestBody);
                pw.flush();
            }
        }
    }

}
