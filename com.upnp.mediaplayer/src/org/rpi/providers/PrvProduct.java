package org.rpi.providers;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.openhome.net.device.DvDevice;
import org.openhome.net.device.IDvInvocation;
import org.openhome.net.device.providers.DvProviderAvOpenhomeOrgProduct1;
import org.rpi.config.Config;
import org.rpi.playlist.PlayManager;

public class PrvProduct extends DvProviderAvOpenhomeOrgProduct1 {

	private Logger log = Logger.getLogger(PrvProduct.class);
	private String  friendly_name = Config.friendly_name;
	//private String iSourceXml = "<SourceList><Source><Name>Playlist</Name><Type>Playlist</Type><Visible>1</Visible></Source><Source><Name>Receiver</Name><Type>Receiver</Type><Visible>1</Visible></Source><Source><Name>Radio</Name><Type>Radio</Type><Visible>1</Visible></Source></SourceList>";
	private String iSourceXml = "";
	private boolean standby = true;
	private String attributes = "Info Time Volume Radio";
	private String man_name = "Java Inc";
	private String man_info = "Developed in Java using OpenHome and MPlayer";
	private String man_url = "";
	private String man_image = "";
	private String model_name = "Test Model Name";
	private String model_info = "Test Model Info";
	private String model_url = "";
	private String model_image = "";
	private String prod_room = friendly_name;
	private String prod_name = "Java MediaPlayer";
	private String prod_info = "Developed by Pete";
	private String prod_url = "";
	private String prod_image = "";

	private PlayManager iPlayer = null;

	private long iSourceXMLChangeCount = 0;

	public PrvProduct(DvDevice iDevice) {
		super(iDevice);
		log.debug("Creating CustomProduct");
		iPlayer = PlayManager.getInstance();
		enablePropertyStandby();
		enablePropertyAttributes();
		enablePropertyManufacturerName();
		enablePropertyManufacturerInfo();
		enablePropertyManufacturerUrl();
		enablePropertyManufacturerImageUri();
		enablePropertyModelName();
		enablePropertyModelInfo();
		enablePropertyModelUrl();
		enablePropertyModelImageUri();
		enablePropertyProductRoom();
		enablePropertyProductName();
		enablePropertyProductInfo();
		enablePropertyProductUrl();
		enablePropertyProductImageUri();
		enablePropertySourceIndex();
		enablePropertySourceCount();
		enablePropertySourceXml();

		setPropertyStandby(standby);
		setPropertyAttributes(attributes);

		setPropertyManufacturerName(man_name);
		setPropertyManufacturerInfo(man_info);
		setPropertyManufacturerUrl(man_url);
		setPropertyManufacturerImageUri(man_image);

		setPropertyModelName(model_name);
		setPropertyModelInfo(model_info);
		setPropertyModelUrl(model_url);
		setPropertyModelImageUri(model_image);

		setPropertyProductRoom(prod_room);
		setPropertyProductName(prod_name);
		setPropertyProductInfo(prod_info);
		setPropertyProductUrl(prod_url);
		setPropertyProductImageUri(prod_image);

		setPropertySourceIndex(0);
		setPropertySourceCount(sources.size());
		setPropertySourceXml(iSourceXml);

		enableActionManufacturer();
		enableActionModel();
		enableActionProduct();
		enableActionStandby();
		enableActionSetStandby();
		enableActionSourceCount();
		enableActionSourceXml();
		enableActionSourceIndex();
		enableActionSetSourceIndex();
		enableActionSetSourceIndexByName();
		enableActionSource();
		enableActionAttributes();
		enableActionSourceXmlChangeCount();
		initSources();
	}

	private CopyOnWriteArrayList<Source> sources = new CopyOnWriteArrayList<Source>();

	private void initSources() {
		addSource(Config.friendly_name, "Playlist", "Playlist", true);
		addSource(Config.friendly_name, "Radio", "Radio", true);
		addSource(Config.friendly_name, "Receiver", "Receiver", true);
	}

	private void addSource(String system_name, String type, String name, boolean visible) {
		Source source = new Source(system_name, type, name, visible);
		sources.add(source);
		iSourceXMLChangeCount++;
		updateSourceXML();
	}

	private void updateSourceXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<SourceList>");
		for (Source s : sources) {
			sb.append("<Source>");
			sb.append("<Name>");
			sb.append(s.getName());
			sb.append("</Name>");
			sb.append("<Type>");
			sb.append(s.getType());
			sb.append("</Type>");
			sb.append("<Visible>");
			sb.append(s.getVisible());
			sb.append("</Visible>");
			sb.append("</Source>");
		}
		sb.append("</SourceList>");
		iSourceXml = sb.toString();
		log.debug("SourceXML \r\n " + iSourceXml);
		propertiesLock();
		setPropertySourceCount(sources.size());
		setPropertySourceXml(iSourceXml.trim());
		propertiesUnlock();
	}

	@Override
	protected void setStandby(IDvInvocation paramIDvInvocation, boolean paramBoolean) {
		log.debug("SetStandby: " + paramBoolean);
		standby = paramBoolean;
		setPropertyStandby(standby);
		if (paramBoolean)
			iPlayer.stop();
	}

	@Override
	protected boolean standby(IDvInvocation paramIDvInvocation) {
		log.debug("GetStandby: " + standby);
		return standby;
	}

	@Override
	protected String attributes(IDvInvocation paramIDvInvocation) {
		log.debug("Attributes: " + attributes);
		return attributes;
	}

	@Override
	protected Manufacturer manufacturer(IDvInvocation paramIDvInvocation) {
		log.debug("Manufacturer");
		Manufacturer man = new Manufacturer(man_name, man_info, man_url, man_image);
		return man;
	}

	@Override
	protected Model model(IDvInvocation paramIDvInvocation) {
		log.debug("Manufacturer");
		Model model = new Model(model_name, model_info, model_url, model_image);
		return model;
	}

	@Override
	protected Product product(IDvInvocation paramIDvInvocation) {
		log.debug("Product");
		Product product = new Product(Config.friendly_name, prod_name, prod_info, prod_url, prod_image);
		return product;
	}

	@Override
	protected Source source(IDvInvocation paramIDvInvocation, long iD) {
		log.debug("Source: " + iD);
		if(sources.size()>= iD )
		{
			try
			{
				Source s = sources.get((int)iD);
				return s;
			}
			catch(Exception e)
			{
				log.error("Error GetSource: " + e);
			}
		}
		return null;
	}

	@Override
	protected long sourceCount(IDvInvocation paramIDvInvocation) {
		long source_count = getPropertySourceCount();
		log.debug("SourceCount: " + source_count);
		return source_count;
	}

	@Override
	protected void setSourceIndex(IDvInvocation paramIDvInvocation, long paramLong) {
		log.debug("SetSourceIndex: " + paramLong + " Port: " + paramIDvInvocation.getClientAddress());
		setPropertySourceIndex(paramLong);
	}

	@Override
	protected long sourceIndex(IDvInvocation paramIDvInvocation) {
		long source_index = getPropertySourceIndex();
		log.debug("SourceIndex: " + source_index);
		return source_index;
	}

	@Override
	protected String sourceXml(IDvInvocation paramIDvInvocation) {
		log.debug("SourceXML: " + iSourceXml);
		return iSourceXml;
	}

	@Override
	protected long sourceXmlChangeCount(IDvInvocation paramIDvInvocation) {
		log.debug("SourceXmlChangeCount: " + iSourceXMLChangeCount);
		return iSourceXMLChangeCount;
	}
	
	public void updateStandby(boolean value)
	{
		setPropertyStandby(value);
	}
}