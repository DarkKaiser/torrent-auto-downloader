package com.darkkaiser.torrentad.net.torrent.transmission.methodresult;

import java.util.List;

public final class TorrentGetMethodResult extends AbstractMethodResult {

	public final class Argument {

		public List<Torrent> torrents;

	}

	public final class Torrent {

		private long id;
		private String name;
		private int status;		// 0:Stopped, 1:Check waiting, 2:Checking, 3:Download waiting, 4:Downloading, 5:Seed waiting, 6:Seeding
		private boolean isStalled;
		private boolean isFinished;
		private double percentDone;
		private int error;
		private String errorString;

		public long getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}
		
		public int status() {
			return this.status;
		}
		
		public String getStatusString() {
			if (this.status == 1)
				return "확인 대기중";

			if (this.status == 2)
				return "확인중";
			
			if (this.status == 3)
				return "다운로드 대기중";
			
			if (this.status == 4)
				return "다운로드 중";
			
			if (this.status == 5)
				return "배포 대기중";
			
			if (this.status == 6)
				return "배포 중";
			
			if (this.isFinished == true && this.error == 0)
				return "완료됨";

			return "정지됨";
		}
		
		public boolean isStalled() {
			return this.isStalled;
		}
		
		public boolean isFinished() {
			return this.isFinished;
		}
		
		public double getPercentDone() {
			return this.percentDone;
		}
		
		public int error() {
			return this.error;
		}
		
		public String getErrorString() {
			return this.errorString;
		}
		
	}

	public Argument arguments;
	
}
