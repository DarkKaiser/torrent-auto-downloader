package kr.co.darkkaiser.knight;

public class HolyGrailQuest implements Quest {

	public HolyGrailQuest() {
	}

	public Object embark() throws QuestException {
		return new HolyGrail();
	}
	
}
