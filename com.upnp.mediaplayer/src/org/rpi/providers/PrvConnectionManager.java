package org.rpi.providers;

import org.apache.log4j.Logger;
import org.openhome.net.device.DvDevice;
import org.openhome.net.device.IDvInvocation;
import org.openhome.net.device.providers.DvProviderUpnpOrgConnectionManager1;

public class PrvConnectionManager extends DvProviderUpnpOrgConnectionManager1 {

	private Logger log = Logger.getLogger(PrvConnectionManager.class);

	private String currentConnectionIDs = "";
	private String sinkProtocolInfo = "";
	private String sourceProtocolInfo = "";

	public PrvConnectionManager(DvDevice device) {
		super(device);
		enablePropertyCurrentConnectionIDs();
		enablePropertySinkProtocolInfo();
		enablePropertySourceProtocolInfo();

		setPropertyCurrentConnectionIDs(currentConnectionIDs);
		setPropertySinkProtocolInfo(sinkProtocolInfo);
		setPropertySourceProtocolInfo(sourceProtocolInfo);

		enableActionConnectionComplete();
		enableActionGetCurrentConnectionIDs();
		enableActionGetCurrentConnectionInfo();
		enableActionPrepareForConnection();
	}

	protected void connectionComplete(IDvInvocation paramIDvInvocation, int paramInt) {
		log.debug("ConnectionManager  ConnectionComplete: " + paramInt);
	}

	protected DvProviderUpnpOrgConnectionManager1.GetProtocolInfo getProtocolInfo(IDvInvocation paramIDvInvocation) {
		log.debug("GetProtolInfo Source=" + sourceProtocolInfo + " Sink=" + sinkProtocolInfo);
		return new DvProviderUpnpOrgConnectionManager1.GetProtocolInfo(sourceProtocolInfo, sinkProtocolInfo);
	}

	protected String getCurrentConnectionIDs(IDvInvocation paramIDvInvocation) {
		log.debug("ConnectionManager getCurrentConnectionIDs ConnectionIDs=" + currentConnectionIDs);
		return currentConnectionIDs;
	}

	protected DvProviderUpnpOrgConnectionManager1.GetCurrentConnectionInfo getCurrentConnectionInfo(IDvInvocation paramIDvInvocation, int paramInt) {
		log.debug("ConnectionManager action: GetCurrentConnectionInfo");
		log.debug(" ConnectionID=" + paramInt);

		int iRcsID = 0;
		int iAVTransportID = 0;
		String iProtocolInfo = "";
		String iPeerConnectionManager = "";
		int iPeerConnectionID = -1;
		String iDirection = "Output";
		String iStatus = "Unknown";

		log.debug("ConnectionManager response: GetCurrentConnectionInfo");
		log.debug(" RcsID=" + iRcsID + " AVTransportID=" + iAVTransportID + " ProtocolInfo=" + iProtocolInfo + " PeerConnectionManager=" + iPeerConnectionManager + " PeerConnectionID=" + iPeerConnectionID + " Direction=" + iDirection + " Status=" + iStatus);
		return new DvProviderUpnpOrgConnectionManager1.GetCurrentConnectionInfo(iRcsID, iAVTransportID, iProtocolInfo, iPeerConnectionManager, iPeerConnectionID, iDirection, iStatus);
	}

}