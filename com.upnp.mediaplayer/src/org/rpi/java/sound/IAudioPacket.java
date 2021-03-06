package org.rpi.java.sound;


public interface IAudioPacket {	

	public AudioInformation getAudioInformation() ;

	public byte[] getAudio(); 

	public int getFrameNumber() ;

	public long getTimeToPlay() ;

	public void incAttempts() ;
	
	public boolean expired() ;
	
	public int getLength();
	
	public void release();

}
