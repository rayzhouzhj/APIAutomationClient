package com.auto.web.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.auto.api.response.AbstractResponse;
import com.auto.framework.logs.AutoLogger;
import com.auto.framework.test.TestContext;
import com.auto.utils.DBServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractRequest {

	public static String COOKIE = "";
	public static String HOST = "";
	public static String SCHEME = "https";

	private boolean hideResponse = false;
	private HttpClient httpClient;
	private HttpRequestBase request;
	private String path = "";
	private List<NameValuePair> parameters;
	private AbstractResponse response;
	private static AutoLogger log = new AutoLogger("Request");

	public AbstractRequest(String path, HttpClient httpClient)
	{

		this.setHttpClient(httpClient);
		this.setPath(path);
		this.parameters = new ArrayList<NameValuePair>();
	}

	public void hideResponse(boolean flag)
	{
		hideResponse = flag;
	}
	
	public HttpClient createClient() {
		return HttpClientBuilder.create().build();
	}

	public abstract void initRequest();

	public AbstractRequest buildPostRequest() {
		HttpPost request = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme(SCHEME).setHost(HOST).setPath(path);

			URI uri = builder.build();

			// Create a method instance.
			request = new HttpPost(uri);
			request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			request.addHeader("Cookie", COOKIE);
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.addHeader("Connection", "keep-alive");
			//			request.addHeader("Content-Length", "1536");

			request.setEntity(new UrlEncodedFormEntity(this.getParameters()));

			this.setRequest(request);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return this;
	}

	public AbstractRequest buildGetRequest() {
		HttpGet request = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme(SCHEME).setHost(HOST).setPath(path);
			//			builder.addParameters(this.getParameters());

			URI uri = builder.build();

			// Create a method instance.
			request = new HttpGet(uri);
			request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			request.addHeader("Cookie", COOKIE);
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.addHeader("Connection", "keep-alive");

			this.setRequest(request);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return this;
	}

	public String sendRequest()
	{
		StringBuffer responseString = new StringBuffer();
		HttpGet redirectRequest = null;
		
		if(this.request == null)
		{
			throw new RuntimeException("Please build the request before sending out!");
		}

		log.info("Sending Request: " + request.getURI().toString());
		if(this.getParameters().size() > 0)
		{
			System.out.println("Request Parameters:");
			System.out.println("---------------------------------------");
			StringBuilder strBuilder = new StringBuilder();
			this.getParameters().forEach(
					a -> 
					strBuilder.append(a.getName() + ":" + a.getValue()).append("<br>\n")
					);
			log.info("Request Parameters:<br>\n" + strBuilder.toString());
			System.out.println("---------------------------------------");
		}
		else
		{
			log.info("Request Parameters: N/A");
		}

		try {
			// Execute the method.
			HttpResponse httpResponse = httpClient.execute(this.request);

			if (httpResponse.getStatusLine().getStatusCode() == 302) {
				String redirectURL = httpResponse.getFirstHeader("Location").getValue();
				System.out.println("Page Redirect To: " + redirectURL);

				// no auto-redirecting at client side, need manual send the request.
				redirectRequest = new HttpGet(redirectURL);
				httpResponse = httpClient.execute(redirectRequest);
				
				if (httpResponse.getStatusLine().getStatusCode() == 302) {
					redirectURL = httpResponse.getFirstHeader("Location").getValue();
					System.out.println("Page Redirect To: " + redirectURL);

					// no auto-redirecting at client side, need manual send the request.
					redirectRequest = new HttpGet(redirectURL);
					httpResponse = httpClient.execute(redirectRequest);
				}
			}
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log.fatal("Send Request", "Method failed: " + httpResponse.getStatusLine());
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				responseString.append(line);
			}

			if(!hideResponse)
			{
				System.out.println("Response: " + responseString.toString());
			}
			
			System.out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
			log.fatal("Send Request", "Fatal transport error: " + e.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			// Release the connection.
			request.releaseConnection();
			
			if(redirectRequest != null)
			{
				redirectRequest.releaseConnection();
			}
		}

		return responseString.toString();
	}

	public <T> T sendRequest(Class<T> responseType)
	{
		String responseString = this.sendRequest();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		T response = gson.fromJson(responseString, responseType);

		return response;
	}

	public HttpRequestBase getRequest() {
		return request;
	}

	public void setRequest(HttpRequestBase request) {
		this.request = request;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
	public String getPath()
	{
		return this.path;
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

	public void setParameters(List<NameValuePair> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String key, String value)
	{
		this.addParameter(new BasicNameValuePair(key, value));
	}

	public void addParameter(NameValuePair parameter)
	{
		boolean isExisted = false;
		int index = 0;
		for(; index < this.getParameters().size(); index++)
		{
			NameValuePair param = this.getParameters().get(index);
			if(param.getName().equals(parameter.getName()))
			{
				parameter = new BasicNameValuePair(param.getName(), parameter.getValue());
				isExisted = true;
				break;
			}
		}

		if(isExisted && !parameter.getName().contains("hotel_room_numbers"))
		{
			this.getParameters().set(index, parameter);
		}
		else
		{
			parameters.add(parameter);
		}
	}

	public void setResponse(AbstractResponse response)
	{
		this.response = response;
	}
	public AbstractResponse getResponse()
	{
		return this.response;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
		if(this.httpClient == null) this.httpClient = HttpClientBuilder.create().build();
	}
}
