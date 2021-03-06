package com.mockey.storage.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.ServiceMergeResults;

@Test
public class MockeyXmlFileManagerTest {

	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	@Test
	public void validateServiceAdd() {
		// Clean Store
		store.deleteEverything();
		for (Service service : getServiceList()) {
			store.saveOrUpdateService(service);
		}

		assert (store.getServices().size() == 2) : "Length should have been 2 but was "
				+ store.getServices().size();

	}

	@Test
	public void checkProxyAdditionMsg() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		for (Service service : getServiceList()) {
			store.saveOrUpdateService(service);
		}
		assert (store.getServices().size() == 2) : "Length should have been 2 but was "
				+ store.getServices().size();

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Upload the store again.
		// ***************************
		ServiceMergeResults mergeResults = getMergeResults(storeAsXml);

		// ******************************************************
		// There should be 1 Addition message e.g. 'Proxy set'
		// ******************************************************
		assert (mergeResults.getAdditionMessages().size() == 1) : "Length should have been 1 but was "
				+ mergeResults.getAdditionMessages().size()
				+ ". Addition messages were: \n"
				+ getPrettyPrint(mergeResults.getAdditionMessages());

	}

	@Test
	public void checkForMergedRealUrls() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		service.setTag("abc");
		List<Url> realUrlList = new ArrayList<Url>();
		realUrlList.add(new Url("http://www.abc.com"));
		service.setRealServiceUrls(realUrlList);
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Rebuild the store with:
		// - Same Service
		// - Different URL
		// ***************************
		store.deleteEverything();
		service = new Service();
		service.setServiceName("Service 1");
		service.setTag("def");
		realUrlList = new ArrayList<Url>();
		realUrlList.add(new Url("http://www.def.com"));
		service.setRealServiceUrls(realUrlList);
		store.saveOrUpdateService(service);

		// ***************************
		// Upload/Merge the store again.
		// Result should be 1 Service in the store with 2 Real URLS (merged)
		// ***************************
		getMergeResults(storeAsXml);

		// ******************************************************
		// There should be 1 Conflict messages e.g. 'Service not added because
		// of conflicting name'
		// ******************************************************
		List<Service> storeServices = store.getServices();
		assert (storeServices.size() == 1) : "Number of Services in the Store should have been 1 but was "
				+ storeServices.size();

		Service serviceToTest = storeServices.get(0);
		assert (serviceToTest.getRealServiceUrls().size() == 2) : "Number of Real URLS in the Service should have been 2 but was "
				+ serviceToTest.getRealServiceUrls().size();
		

	}
	
	@Test
	public void checkForMergedServiceTags() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		service.setTag("abc");
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Rebuild the store with:
		// - Same Service
		// - Different URL
		// ***************************
		store.deleteEverything();
		service = new Service();
		service.setServiceName("Service 1");
		service.setTag("def");
		store.saveOrUpdateService(service);

		// ***************************
		// Upload/Merge the store again.
		// Result should be 1 Service in the store with 2 Real URLS (merged)
		// ***************************
		getMergeResults(storeAsXml);

		List<Service> storeServices = store.getServices();
		assert (storeServices.size() == 1) : "Number of Services in the Store should have been 1 but was "
				+ storeServices.size();
		Service serviceToTest = storeServices.get(0);
		assert (serviceToTest.getTagList().size() == 2) : "Number of Tags in the Service should have been size 2, with value 'abc def' but was size "
		+ serviceToTest.getTagList().size() + " with value '" + serviceToTest.getTag() +"'";

	}
	
	@Test
	public void checkForMergedServiceScenarioTags() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		Scenario scenario = new Scenario();
		scenario.setScenarioName("ABC");
		scenario.setTag("abc");
		service.saveOrUpdateScenario(scenario);
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Rebuild the store with:
		// - Same Service, same scenario 
		// - Service scenario has different tag 
		// ***************************
		store.deleteEverything();
		service = new Service();
		service.setServiceName("Service 1");
		scenario = new Scenario();
		scenario.setScenarioName("ABC");
		scenario.setTag("def");
		service.saveOrUpdateScenario(scenario);
		store.saveOrUpdateService(service);

		// ***************************
		// Upload/Merge the store again.
		// Result should be 1 Service in the store with 2 Real URLS (merged)
		// ***************************
		getMergeResults(storeAsXml);

		List<Service> storeServices = store.getServices();
		assert (storeServices.size() == 1) : "Number of Services in the Store should have been 1 but was "
				+ storeServices.size();
		Service serviceToTest = storeServices.get(0);
		List<Scenario> scenarioList = serviceToTest.getScenarios();
		assert (scenarioList.size() == 1) : "Number of Service scenarios in the Store should have been 1 but was "
		+ scenarioList.size() + " with value: \n" +  getScenarioListAsString(scenarioList);
		
		Scenario scenarioTest = scenarioList.get(0);
		assert (scenarioTest.getTagList().size() == 2) : "Number of Tags in the Service Scenario should have been size 2, with value 'abc def' but was size "
		+ scenarioTest.getTagList().size() + " with value '" + scenarioTest.getTag() +"'";

	}

	@Test
	public void checkForConflictingServiceName() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Upload the store again.
		// ***************************
		ServiceMergeResults mergeResults = getMergeResults(storeAsXml);

		// ******************************************************
		// There should be 1 Conflict messages e.g. 'Service not added because
		// of conflicting name'
		// ******************************************************
		assert (mergeResults.getConflictMsgs().size() == 1) : "Length should have been 1 but was "
				+ mergeResults.getConflictMsgs().size()
				+ ". Conflict messages were: \n"
				+ getPrettyPrint(mergeResults.getConflictMsgs());

	}

	// ************************************************************************************************************
	// HELPFUL UTILIITY METHODS BELOW
	// ************************************************************************************************************

	private String getStoreAsXml() {
		MockeyXmlFactory g = new MockeyXmlFactory();

		String storeAsXml = null;

		try {
			storeAsXml = g.getStoreAsString(store, true);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (TransformerException e) {

			e.printStackTrace();
		}
		return storeAsXml;
	}
	
	private String getScenarioListAsString(List<Scenario> scenarioList){
		StringBuffer sb = new StringBuffer();
		for(Scenario scenario : scenarioList){
			sb.append(scenario.toString() + "\n");
			
		}
		return sb.toString();
	}

	private List<Service> getServiceList() {
		List<Service> serviceList = new ArrayList<Service>();
		List<Url> realUrlList = new ArrayList<Url>();
		realUrlList.add(new Url("http://www.abc.com"));
		realUrlList.add(new Url("http://www.nbc.com"));
		Service service = new Service();
		service.setServiceName("Service 1");
		service.setRealServiceUrls(realUrlList);
		serviceList.add(service);

		service = new Service();
		service.setServiceName("Service 2");
		service.setRealServiceUrls(realUrlList);
		serviceList.add(service);
		return serviceList;
	}

	private ServiceMergeResults getMergeResults(String storeAsXml) {
		MockeyXmlFileManager configurationReader = new MockeyXmlFileManager();
		ServiceMergeResults mergeResults = null;
		try {
			mergeResults = configurationReader.loadConfigurationWithXmlDef(
					storeAsXml, "");

		} catch (SAXParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mergeResults;
	}

	private String getPrettyPrint(List<String> g) {
		StringBuffer sb = new StringBuffer();
		for (String gg : g) {
			sb.append(gg);
			sb.append("\n");
		}
		return sb.toString();
	}
}
