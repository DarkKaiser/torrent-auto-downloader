package kr.co.darkkaiser.knight;

public class KnightOfTheRoundTable implements Knight {
	
	private Quest quest;
	
	public KnightOfTheRoundTable(String name) {
	}
	
	public HolyGrail embarkOnQuest() throws QuestException {
		return (HolyGrail) quest.embark();
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}
	
}
