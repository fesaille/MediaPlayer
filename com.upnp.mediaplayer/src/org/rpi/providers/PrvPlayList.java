package org.rpi.providers;

import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;
import org.openhome.net.device.DvDevice;
import org.openhome.net.device.providers.DvProviderAvOpenhomeOrgPlaylist1;
import org.rpi.config.Config;
import org.rpi.playlist.CustomTrack;
import org.rpi.playlist.PlayListReader;
import org.rpi.playlist.PlayListWriter;
import org.rpi.playlist.PlayManager;

public class PrvPlayList extends DvProviderAvOpenhomeOrgPlaylist1 {

	private Logger log = Logger.getLogger(PrvPlayList.class);
	private int next_id;
	private PlayListWriter plw = null;
	private int playlist_max = Config.playlist_max;

	private CopyOnWriteArrayList<CustomTrack> tracks = new CopyOnWriteArrayList<CustomTrack>();

	private PlayManager iPlayer = PlayManager.getInstance();

	public PrvPlayList(DvDevice iDevice) {
		super(iDevice);
		log.debug("Creating CustomPlayList");

		plw = new PlayListWriter();
		plw.start();
		enablePropertyTransportState();
		enablePropertyRepeat();
		enablePropertyShuffle();
		enablePropertyId();
		enablePropertyTracksMax();
		enablePropertyProtocolInfo();
		enablePropertyIdArray();

		byte[] array = new byte[0];
		setPropertyId(0);
		setPropertyProtocolInfo(Config.getProtocolInfo());
		setPropertyRepeat(false);
		setPropertyShuffle(false);
		setPropertyTracksMax(playlist_max);
		setPropertyTransportState("");
		setPropertyIdArray(array);

		enableActionPlay();
		enableActionPause();
		enableActionStop();
		enableActionNext();
		enableActionPrevious();
		enableActionSetRepeat();
		enableActionRepeat();
		enableActionSetShuffle();
		enableActionShuffle();
		enableActionSeekSecondAbsolute();
		enableActionSeekSecondRelative();
		enableActionSeekId();
		enableActionSeekIndex();
		enableActionTransportState();
		enableActionId();
		enableActionRead();
		enableActionReadList();
		enableActionInsert();
		enableActionDeleteId();
		enableActionDeleteAll();
		enableActionTracksMax();
		enableActionIdArray();
		enableActionIdArrayChanged();
		enableActionProtocolInfo();
		loadPlayList();
	}

	private void loadPlayList() {
		if (Config.save_local_playlist) {
			PlayListReader plr = new PlayListReader(this);
			plr.getXML();
		}
	}

	protected void pause(org.openhome.net.device.IDvInvocation arg0) {
		log.debug("Pause");
		iPlayer.pause(true);
	};

	protected void play(org.openhome.net.device.IDvInvocation arg0) {
		log.debug("Play");
		iPlayer.play();
	};

	protected void stop(org.openhome.net.device.IDvInvocation arg0) {
		log.debug("Stop");
		iPlayer.stop();
	};
	
	/***
	 * Returns the track Id
	 * @return
	 */
	public int getNext_id() {
		next_id++;
		log.debug("GetNextId: " + next_id);
		return next_id;
	}
	
	/***
	 * If reading the playList from the xml file, make sure that the nextId is set to the max_id of the .xml entry..
	 * @param max_id
	 */
	public void setNextId(int max_id) {
		next_id = max_id;
		
	}

	protected long insert(org.openhome.net.device.IDvInvocation arg0, long aAfterId, String aUri, String aMetaData) {
		if(tracks.size() >= playlist_max)
		{
			log.error("Maximum Size of PlayList Reached...");
			return -1;
		}
		log.debug("Insert After: " + aAfterId + " URI: " + aUri + " MetaDate: \r\n" + aMetaData);
		int id = getNext_id();
		CustomTrack track = new CustomTrack(aUri, aMetaData, id);
		int iCount = 0;
		if (aAfterId != 0) {
			for (CustomTrack t : tracks) {
				if (t.getId() == aAfterId) {
					iCount++;
					break;
				}
				iCount++;
			}
		}
		try {
			tracks.add(iCount, track);
		} catch (Exception e) {
			log.error("Error Adding Track: " + track.getId() + " After: " + aAfterId, e);
		}
		iPlayer.insertTrack(aAfterId, track );
		UpdateIdArray();
		return id;
	};
	

	protected void deleteAll(org.openhome.net.device.IDvInvocation arg0) {
		log.debug("DeleteAll");
		tracks.clear();
		UpdateIdArray();
		iPlayer.deleteAllTracks();
	};

	protected void deleteId(org.openhome.net.device.IDvInvocation arg0, long iD) {
		log.debug("DeleteId: " + iD);
		int iCount = 0;
		boolean found = false;
		for (CustomTrack t : tracks) {
			if (t.getId() == iD) {
				found = true;
				break;
			}
			iCount++;
		}
		if (found) {
			try {
				log.debug("Deleteing Id: " + iD + " at Position : " + iCount + " In List");
				tracks.remove(iCount);
				iPlayer.DeleteTrack(iD);
			} catch (Exception e) {
				log.error("Unable to Delete Track Id: " + iD + " At List Postion : " + iCount, e);
			}
		}
		UpdateIdArray();
	};

	protected long id(org.openhome.net.device.IDvInvocation arg0) {
		log.debug("GetId: ");
		long id = getPropertyId();
		return id;
	};

	protected String protocolInfo(org.openhome.net.device.IDvInvocation arg0) {
		String protocolInfo = getPropertyProtocolInfo();
		log.debug("GetProtocolInfo: \r\n" + protocolInfo);
		return protocolInfo;
	};

	protected IdArray idArray(org.openhome.net.device.IDvInvocation arg0) {
		log.debug("GetIdArray");
		byte[] array = getPropertyIdArray();
		DvProviderAvOpenhomeOrgPlaylist1.IdArray idArray = new IdArray(0, array);
		return idArray;
	};

	protected boolean idArrayChanged(org.openhome.net.device.IDvInvocation paramIDvInvocation, long paramLong) {
		log.debug("GetIdArrayChanged");
		boolean changed = false;
		return changed;
	};

	protected void next(org.openhome.net.device.IDvInvocation paramIDvInvocation) {
		log.debug("Next");
		iPlayer.nextTrack();
	};

	protected void previous(org.openhome.net.device.IDvInvocation paramIDvInvocation) {
		log.debug("Previous");
		iPlayer.previousTrack();
	};

	protected Read read(org.openhome.net.device.IDvInvocation paramIDvInvocation, long paramLong) {
		log.debug("Read Index: " + paramLong);
		for (CustomTrack t : tracks) {
			if (t.getId() == paramLong) {
				DvProviderAvOpenhomeOrgPlaylist1.Read read = new Read(t.getUri(), t.getMetadata());
				return read;
			}
		}
		return null;
	};

	protected String readList(org.openhome.net.device.IDvInvocation paramIDvInvocation, String paramString) {

		log.debug("ReadList");

		return getList();
	};

	protected boolean repeat(org.openhome.net.device.IDvInvocation paramIDvInvocation) {
		boolean repeat = getPropertyRepeat();
		log.debug("Repeat: " + repeat);
		return repeat;
	};

	protected void seekId(org.openhome.net.device.IDvInvocation paramIDvInvocation, long id) {
		log.debug("SeekId: " + id);
	};

	protected void seekIndex(org.openhome.net.device.IDvInvocation paramIDvInvocation, long id) {
		log.debug("SeekIndex: " + id);
		iPlayer.playIndex(id);
	};

	protected void seekSecondAbsolute(org.openhome.net.device.IDvInvocation paramIDvInvocation, long seconds) {
		log.debug("SeekSecondAbsolute: " + seconds);
		iPlayer.seekAbsolute(seconds);
	};

	protected void seekSecondRelative(org.openhome.net.device.IDvInvocation paramIDvInvocation, int paramInt) {
		log.debug("SeekSecondRelative: " + paramInt);
	};

	protected void setRepeat(org.openhome.net.device.IDvInvocation paramIDvInvocation, boolean repeat) {
		log.debug("SetRepeat: " + repeat);
		setPropertyRepeat(repeat);
		iPlayer.setRepeatPlayList(repeat);
	};

	protected void setShuffle(org.openhome.net.device.IDvInvocation paramIDvInvocation, boolean paramBoolean) {
		log.debug("SetShuffle: " + paramBoolean);
		setPropertyShuffle(paramBoolean);
		iPlayer.setShuffle(paramBoolean);
	};

	protected boolean shuffle(org.openhome.net.device.IDvInvocation paramIDvInvocation) {
		boolean shuffle = getPropertyShuffle();
		log.debug("GetShuffle: " + shuffle);
		return shuffle;
	};

	protected long tracksMax(org.openhome.net.device.IDvInvocation paramIDvInvocation) {
		long tracksMax = getPropertyTracksMax();
		log.debug("GetTracksMax: " + tracksMax);
		return tracksMax;
	};

	protected String transportState(org.openhome.net.device.IDvInvocation paramIDvInvocation) {
		String state = getPropertyTransportState();
		log.debug("TransportState: " + state);
		return state;
	};

	private synchronized void UpdateIdArray(boolean bUpdateFile) {
		int size = tracks.size() * 4;
		StringBuilder sb = new StringBuilder();
		byte[] bytes = new byte[size];
		for (CustomTrack t : tracks) {
			try {
				int intValue = (int) t.getId();
				String binValue = Integer.toBinaryString(intValue);
				binValue = padLeft(binValue, 32, '0');
				sb.append(binValue);
			} catch (Exception e) {
				log.error(e);
			}
		}
		// Now we have a big long string of binary, chop it up and get the
		// bytes for the byte array..
		String myBytes = sb.toString();
		int numOfBytes = myBytes.length() / 8;
		bytes = new byte[numOfBytes];
		try

		{
			for (int i = 0; i < numOfBytes; ++i) {
				int index = 8 * i;
				String sByte = myBytes.substring(index, index + 8);
				// try {
				// log.debug("Byte: " + sByte);
				Integer x = Integer.parseInt(sByte, 2);
				Byte sens = (byte) x.intValue();
				// byte b = Byte.parseByte(sByte, 2);
				bytes[i] = sens;
				// } catch (Exception e) {
				// log.error("Error parseByte: " + sByte , e);
				// }

			}
			setPropertyIdArray(bytes);
			if (bUpdateFile) {
				plw.trigger(tracks);
			}
		} catch (Exception e) {
			log.error("Error Writing Bytes: " + sb.toString(), e);
		}

	}

	/***
	 * Itarate all tracks, and create a 32 bit binary number from the track Id.
	 * Add the 32 bit binary string to a long string Split the 32 bit binary
	 * long string 4 bytes (8bits) And add to a byte array
	 */
	private synchronized void UpdateIdArray() {
		UpdateIdArray(true);
	}

	private String padLeft(String str, int length, char padChar) {
		StringBuilder sb = new StringBuilder();

		for (int toPrepend = length - str.length(); toPrepend > 0; toPrepend--) {
			sb.append(padChar);
		}
		sb.append(str);
		return sb.toString();
	}

	private boolean PauseTrack() {
		iPlayer.pause(true);
		return true;
	}

	public void PlayingTrack(int iD) {
		setPropertyId(iD);
	}

	public synchronized void setTracks(CopyOnWriteArrayList<CustomTrack> tracks) {
		this.tracks = tracks;
		iPlayer.setTracks(tracks);
		UpdateIdArray(false);
	}

	public void SetStatus(String status) {
		setPropertyTransportState(status);
	}

	private String getList() {
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("<TrackList>");
		for (CustomTrack t : tracks) {
			i++;
			sb.append(t.getFullString());
		}
		sb.append("</TrackList>");
		log.debug("ReadList Contains : " + i);
		return sb.toString();
	}

	@Override
	public void dispose() {
		plw = null;
		super.dispose();
	}

	public void updateShuffle(boolean shuffle) {
		setPropertyShuffle(shuffle);		
	}


}