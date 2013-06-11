package org.rpi.mplayer;

import org.rpi.player.events.EventUpdateTrackInfo;


public class TrackInfo {

	private boolean bSentUpdate = false;
	private MPlayer mPlayer = null;
	public TrackInfo(MPlayer mPlayer)
	{
		this.mPlayer = mPlayer;
	}
	
	private String codec = null;
	private long bitRate = -99;
	private long sampleRate = -99;
	private long duration = -99;
	/**
	 * @return the codec
	 */
	public String getCodec() {
		return codec;
	}
	/**
	 * @param codec the codec to set
	 */
	public void setCodec(String codec) {
		this.codec = codec;
	}
	/**
	 * @return the bitrate
	 */
	public long getBitrate() {
		return bitRate;
	}
	/**
	 * @param bitrate the bitrate to set
	 */
	public void setBitrate(long bitrate) {
		this.bitRate = bitrate;
	}
	/**
	 * @return the sample_rate
	 */
	public long getSampleRate() {
		return sampleRate;
	}
	/**
	 * @param sample_rate the sample_rate to set
	 */
	public void setSampleRate(long sample_rate) {
		this.sampleRate = sample_rate;
	}
	

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public boolean isSet()
	{
		if(bitRate != -99 && sampleRate !=-99 && codec !=null && duration != -99)
		{
			if(!bSentUpdate)
			{
				EventUpdateTrackInfo ev = new EventUpdateTrackInfo(this);
				mPlayer.fireEvent(ev);
				bSentUpdate = true;
			}
			return true;
		}
		return false;
	}

}