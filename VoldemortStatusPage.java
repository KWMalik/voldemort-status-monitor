/*
 * Developed by Srikanth P Shreenivas (srikanthps (at) yahoo (dot) com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import voldemort.client.protocol.admin.AdminClient;
import voldemort.client.protocol.admin.AdminClientConfig;
import voldemort.cluster.Cluster;
import voldemort.cluster.Node;
import voldemort.store.metadata.MetadataStore.VoldemortState;

/**
 * @author Srikanth Shreenivas (http://www.srikanthps.com/)
 * License: MIT
 */
public class VoldemortStatusPage {
	
	Logger logger = Logger.getLogger(VoldemortStatusPage.class);

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("Usage: java -cp .:/path/to/voldemort-installation/lib/* VoldemortStatusPage <http-port-number> <cluster-details>.properties");
			System.out.println(" Properties file should contain list of bootstrap URLs that needs to be monitored.");
			System.out.println(" Example enries:");
			System.out.println(" cluster.1 = tcp://1.2.3.4:7001/");
			System.out.println(" cluster.2 = tcp://1.2.3.5:7001/");
			System.out.println(" cluster.3 = tcp://1.2.3.6:7001/");
			return;
		}
		
		Properties p = lookupProperties(args[1]);
		startJettyServer(Integer.valueOf(args[0]), p);
	}

	private static void startJettyServer(Integer portNumber, Properties p) throws Exception, InterruptedException {
		Server server = new Server(portNumber);
		server.setHandler(new StatusHandler(p));
		server.start();
		server.join();
	}

	private static Properties lookupProperties(String fileName) throws IOException, FileNotFoundException {
		Properties p = new Properties();
		File propertyFile = new File(fileName);
		FileInputStream is = null;
		is = new FileInputStream(propertyFile);
		p.load(is);
		is.close();
		return p;
	}

	private static String getNodeStatusAsHtml(AdminClient c, Node n) {
		try {
			 VoldemortState state = c.getRemoteServerState(n.getId()).getValue();
			 return inGreen(state.toString());
		} catch (Exception e) {
			 return inRed("DOWN");
		}
	}

	private static String inGreen(String value) {
		return inColor("green", value);
	}

	private static String inRed(String value) {
		return inColor("red", value);
	}
	
	private static String inColor(String color, String value) {
		return "<span style=\"color:" + color + "\">" + value + "</span>";
	}
	
	public static class StatusHandler extends AbstractHandler
	{
		Logger logger = Logger.getLogger(StatusHandler.class);
		Properties clusters;
		
		/**
		 * Provide clusters to manage in the following form in the properties file
		 * cluster.1=<boot-strap-url-1>
		 * cluster.2=<boot-strap-url-2>
		 * cluster.3=<boot-strap-url-3>
		 * ...
		 */
		public StatusHandler(Properties clusters) {
			this.clusters = clusters;
		}
		
	    @Override
	    public void handle(String target, HttpServletRequest req, HttpServletResponse res, int dispatch) throws IOException, ServletException {
	    	
			res.setContentType("text/html;charset=utf-8");
	        res.setStatus(HttpServletResponse.SC_OK);
	    	for (Object cluster : clusters.keySet()) {
		        writeStatusForCluster(res, cluster.toString(), clusters.getProperty(cluster.toString()));
	    	}
	        
	        res.getWriter().close();
	        res.setStatus(200);
	    	
	    }

		private void writeStatusForCluster(HttpServletResponse res, String clusterName, String bootstrapUrl) throws IOException {

			res.getWriter().println("<h2>Voldemort Cluster '" + clusterName +"' Status </h2>");
			res.getWriter().println("<h4>(");

			try {
				String[] bootStrapUrls = bootstrapUrl.split(",");
				
				AdminClient reachableClient = null;
				for (String b : bootStrapUrls) {
					if (StringUtils.isNotBlank(b)) {
						try {
							AdminClient c = new AdminClient(b, new AdminClientConfig());
							res.getWriter().print(inGreen(b) + ",");
							if (reachableClient == null) {
								reachableClient = c;
							}
						} catch (Exception e) {
							logger.error("Could not open admin client using: " + b, e);
							res.getWriter().print(inRed(b) + ",");
						}
					}
				}
				res.getWriter().println(")</h4>");
				res.getWriter().println();
				
				for (String b : bootStrapUrls) {
					if (StringUtils.isNotBlank(b)) {
						try {
							if (reachableClient != null) {
								Cluster cluster = reachableClient.getAdminClientCluster();
								for (Node n : cluster.getNodes()) {
									res.getWriter().println("<div><span>" + n.getHost() + ":" + n.getSocketPort() + " : " + "</span>" + getNodeStatusAsHtml(reachableClient, n) + "</div>");
								}
								break;
							}
						} catch (Exception e) {
							logger.error("Could not open admin client using: " + b, e);
							res.getWriter().println("<div><span>" + b + " : " + "</span>" + inRed("NOT REACHABLE") + "</div>");
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
