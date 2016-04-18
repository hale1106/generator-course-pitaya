package com.flyfinger.wx.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>
 * 微信HttpClient工具类
 * </p>
 * HttpClient版本:4.3.3
 * 
 * @author Changsoul.Wu
 * @date 2014-3-29 下午6:10:49
 */
public class WxHttpClientUtils {

	private static final Log log = LogFactory.getLog(WxHttpClientUtils.class);

	public static final ContentType UTF8_TEXT_PLAIN_CONTENT_TYPE = ContentType
			.create("text/plain", Consts.UTF_8);
	public static final ContentType DEFAULT_BINARY_CONTENT_TYPE = ContentType
			.create("application/octet-stream", Consts.UTF_8);

	private static final String ACCEPT_LANGUAGE = "zh-CN,zh;q=0.8";
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36";
	private static final String JSON_CONTENT_TYPE = "application/json; encoding=utf-8";
	private static final int TIME_OUT = 10 * 1000;

	private static CloseableHttpClient httpClient = null;

	public static CloseableHttpClient getHttpClient() {

		if (httpClient == null) {

			try {
				ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {

					public long getKeepAliveDuration(HttpResponse response,
							HttpContext context) {
						// Honor 'keep-alive' header
						HeaderElementIterator it = new BasicHeaderElementIterator(
								response.headerIterator(HTTP.CONN_KEEP_ALIVE));
						while (it.hasNext()) {
							HeaderElement he = it.nextElement();
							String param = he.getName();
							String value = he.getValue();
							if (value != null
									&& param.equalsIgnoreCase("timeout")) {
								try {
									return Long.parseLong(value) * 1000;
								} catch (NumberFormatException ignore) {
								}
							}
						}
						HttpHost target = (HttpHost) context
								.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
						if ("file.api.weixin.qq.com".equalsIgnoreCase(target
								.getHostName())) {
							// Keep alive for 5 seconds only
							return 3 * 1000;
						} else {
							// otherwise keep alive for 30 seconds
							return 30 * 1000;
						}
					}

				};

				HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

					public boolean retryRequest(IOException exception,
							int executionCount, HttpContext context) {
						if (executionCount >= 3) {
							// 如果已经重试了3次，就放弃
							return false;
						}
						if (exception instanceof InterruptedIOException) {
							// 超时
							return false;
						}
						if (exception instanceof UnknownHostException) {
							// 目标服务器不可达
							return false;
						}
						if (exception instanceof ConnectTimeoutException) {
							// 连接被拒绝
							return false;
						}
						if (exception instanceof SSLException) {
							// ssl握手异常
							return false;
						}
						HttpClientContext clientContext = HttpClientContext
								.adapt(context);
						HttpRequest request = clientContext.getRequest();
						boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
						if (idempotent) {
							// 如果请求是幂等的，就再次尝试
							return true;
						}
						return false;
					}

				};

				// Trust own CA and all self-signed certs
				SSLContext sslcontext = SSLContexts.custom()
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
				// Allow TLSv1 protocol only
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslcontext,
						new String[] { "TLSv1" },
						null,
						SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

				Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
						.<ConnectionSocketFactory> create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", sslsf).build();

				PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
						socketFactoryRegistry);
				// 将最大连接数增加到200
				connManager.setMaxTotal(200);
				// 将每个路由基础的连接增加到20
				connManager.setDefaultMaxPerRoute(50);
				// 将目标主机的最大连接数增加到100
				HttpHost weiXinApiHost = new HttpHost("api.weixin.qq.com", 443);
				connManager.setMaxPerRoute(new HttpRoute(weiXinApiHost), 100);

				// Create socket configuration
				SocketConfig socketConfig = SocketConfig.custom()
						.setTcpNoDelay(true).build();
				connManager.setDefaultSocketConfig(socketConfig);

				// Create connection configuration
				ConnectionConfig connectionConfig = ConnectionConfig.custom()
						.setMalformedInputAction(CodingErrorAction.IGNORE)
						.setUnmappableInputAction(CodingErrorAction.IGNORE)
						.setCharset(Consts.UTF_8).build();
				connManager.setDefaultConnectionConfig(connectionConfig);

				// Use custom cookie store if necessary.
				// CookieStore cookieStore = new BasicCookieStore();
				// Create global request configuration
				RequestConfig defaultRequestConfig = RequestConfig
						.custom()
						// .setCookieSpec(CookieSpecs.BEST_MATCH)
						.setExpectContinueEnabled(false)
						.setStaleConnectionCheckEnabled(true)
						.setTargetPreferredAuthSchemes(
								Arrays.asList(AuthSchemes.NTLM,
										AuthSchemes.DIGEST))
						.setProxyPreferredAuthSchemes(
								Arrays.asList(AuthSchemes.BASIC))
						.setConnectionRequestTimeout(TIME_OUT)
						.setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT)
						.build();

				HttpRequestInterceptor requestInterceptor = new HttpRequestInterceptor() {

					public void process(final HttpRequest request,
							final HttpContext context) throws HttpException,
							IOException {
						if (!request.containsHeader("Accept-Encoding")) {
							request.addHeader("Accept-Encoding", "gzip");
						}

						request.addHeader(HttpHeaders.ACCEPT_LANGUAGE,
								ACCEPT_LANGUAGE);
						request.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);

					}
				};

				HttpResponseInterceptor gizpResponseInterceptor = new HttpResponseInterceptor() {

					public void process(final HttpResponse response,
							final HttpContext context) throws HttpException,
							IOException {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							Header ceheader = entity.getContentEncoding();
							if (ceheader != null) {
								HeaderElement[] codecs = ceheader.getElements();
								for (int i = 0; i < codecs.length; i++) {
									if (codecs[i].getName().equalsIgnoreCase(
											"gzip")) {
										response.setEntity(new GzipDecompressingEntity(
												response.getEntity()));
										return;
									}
								}
							}
						}
					}
				};

				httpClient = HttpClients
						.custom()
						.setConnectionManager(connManager)
						.setKeepAliveStrategy(myStrategy)
						// .setDefaultCookieStore(cookieStore)
						.setDefaultRequestConfig(defaultRequestConfig)
						.setRetryHandler(myRetryHandler)
						.addInterceptorFirst(requestInterceptor)
						.addInterceptorFirst(gizpResponseInterceptor).build();

			} catch (KeyManagementException e) {
				log.error(e.getMessage(), e);
			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage(), e);
			} catch (KeyStoreException e) {
				log.error(e.getMessage(), e);
			}
		}

		return httpClient;
	}

	/**
	 * GET提交数据,并返回JSON格式的结果数据
	 * 
	 * @param url
	 *            请求URL
	 * @return JSONObject or null if error or no response
	 */
	public static JSONObject getForJsonResult(String reqUrl) {

		return getForJsonResult(reqUrl, null);
	}

	/**
	 * GET提交数据,并返回JSON格式的结果数据
	 * 
	 * @param url
	 *            请求URL
	 * @param 请求参数MAP
	 * @return JSONObject or null if error or no response
	 */
	public static JSONObject getForJsonResult(String reqUrl,
			Map<String, String> params) {
		try {
			if (params != null) {
				reqUrl = buildReqUrl(reqUrl, params);
			}
			return getHttpClient().execute(new HttpGet(reqUrl),
					new JsonResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * GET提交并返回XML数据
	 * 
	 * @param url
	 *            请求URL
	 * @return org.w3c.dom.Document or null
	 */
	public static Document getForXmlResult(String reqUrl) {

		return getForXmlResult(reqUrl, null);
	}

	/**
	 * GET提交并返回XML数据
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            请求参数MAP
	 * @return org.w3c.dom.Document or null
	 */
	public static Document getForXmlResult(String reqUrl,
			Map<String, String> params) {
		try {
			if (params != null) {
				reqUrl = buildReqUrl(reqUrl, params);
			}
			return getHttpClient().execute(new HttpGet(reqUrl),
					new XmlResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * GET提交并返回String数据
	 * 
	 * @param url
	 *            请求URL
	 * @return String or null
	 */
	public static String getForStringResult(String reqUrl) {

		return getForStringResult(reqUrl, null);
	}

	/**
	 * GET提交并返回String数据
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            请求参数MAP
	 * @return String or null
	 */
	public static String getForStringResult(String reqUrl,
			Map<String, String> params) {
		try {
			if (params != null) {
				reqUrl = buildReqUrl(reqUrl, params);
			}
			return getHttpClient().execute(new HttpGet(reqUrl),
					new StringResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * POST提交数据,并返回JSON格式的结果数据
	 * 
	 * @param url
	 *            请求URL
	 * @return JSONObject or null if error or no response
	 */
	public static JSONObject postForJsonResult(String url) {

		return postForJsonResult(url, null);
	}

	/**
	 * POST提交数据,并返回JSON格式的结果数据
	 * 
	 * @param url
	 *            请求URL
	 * @param 请求参数MAP
	 * @return JSONObject or null if error or no response
	 */
	public static JSONObject postForJsonResult(String url,
			Map<String, String> params) {
		try {
			HttpPost post = new HttpPost(url);

			if (params != null) {
				post.setEntity(buildUrlEncodedFormEntity(params));
			}
			return getHttpClient().execute(post, new JsonResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * POST提交JSON数据,并返回JSON格式的结果数据
	 * 
	 * @param url
	 *            请求URL
	 * @param jsonDataStr
	 *            jsonData String
	 * @return JSONObject or null if error or no response
	 */
	public static JSONObject postJsonDataForJsonResult(String url,
			String jsonDataStr) {
		try {
			HttpPost post = new HttpPost(url);

			if (jsonDataStr != null) {
				StringEntity reqEntity = new StringEntity(jsonDataStr,
						Consts.UTF_8);
				reqEntity.setContentType(JSON_CONTENT_TYPE);
				post.setEntity(reqEntity);
			}
			return getHttpClient().execute(post, new JsonResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * POST提交并返回XML数据
	 * 
	 * @param url
	 *            请求URL
	 * @return org.w3c.dom.Document or null
	 */
	public static Document postForXmlResult(String url) {

		return postForXmlResult(url, null);
	}

	/**
	 * POST提交并返回XML数据
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            请求参数MAP
	 * @return org.w3c.dom.Document or null
	 */
	public static Document postForXmlResult(String url,
			Map<String, String> params) {
		try {
			HttpPost post = new HttpPost(url);

			if (params != null) {
				post.setEntity(buildUrlEncodedFormEntity(params));
			}
			return getHttpClient().execute(post, new XmlResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * POST提交并返回String数据
	 * 
	 * @param url
	 *            请求URL
	 * @return String or null
	 */
	public static String postForStringResult(String url) {

		return postForStringResult(url, null);
	}

	/**
	 * POST提交并返回String数据
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            请求参数MAP
	 * @return String or null
	 */
	public static String postForStringResult(String url,
			Map<String, String> params) {
		try {
			HttpPost post = new HttpPost(url);

			if (params != null) {
				post.setEntity(buildUrlEncodedFormEntity(params));
			}
			return getHttpClient().execute(post, new StringResponseHandler());
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static String buildReqUrl(String reqUrl, Map<String, String> params) {
		if (reqUrl == null || params == null)
			return null;

		String[] reqUrls = reqUrl.split("\\?");

		StringBuilder sp = new StringBuilder();

		sp.append(reqUrls[0]).append("?");

		List<NameValuePair> parameters = new ArrayList<NameValuePair>(
				params.size());

		for (Map.Entry<String, String> entry : params.entrySet()) {
			parameters.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
		}

		sp.append(URLEncodedUtils.format(parameters, Consts.UTF_8));

		return sp.toString();
	}

	public static UrlEncodedFormEntity buildUrlEncodedFormEntity(
			Map<String, String> params) throws ClientProtocolException {
		if (params == null)
			throw new ClientProtocolException("Params is null");

		List<NameValuePair> parameters = new ArrayList<NameValuePair>(
				params.size());

		for (Map.Entry<String, String> entry : params.entrySet()) {
			parameters.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
		}

		return new UrlEncodedFormEntity(parameters, Consts.UTF_8);
	}

}
