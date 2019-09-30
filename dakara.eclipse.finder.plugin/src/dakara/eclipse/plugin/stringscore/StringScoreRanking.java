package dakara.eclipse.plugin.stringscore;

import java.util.function.BiFunction;
import java.util.function.Function;

public class StringScoreRanking {
	
	public static BiFunction<String, StringCursor, Integer> standardContiguousSequenceRanking() {
		return StringScoreRanking::rankContiguousSequence;
	}
	
	private static int rankContiguousSequence(String match, StringCursor targetCursor) {
		int rank = 0;
		final boolean fullMatch = targetCursor.partialWordAtCursor().equals(match);  // did we match full word
		if ( fullMatch ) {
			rank = 3;
		} else {
			if (targetCursor.cursorAtPartialWordStart())
				rank = 2;
			else if (match.length() > 2)  // 1 or 2 character matches are very weak when not at beginning of word, lets not show them at all.
				rank = 1;
		}
		
		// bonuses
		if (targetCursor.indexOfCursor() == 0) {
			// Our match is at the very beginning
			rank += 3;
		}
		String asString = targetCursor.text.asString();
		if(!asString.contains("[class]") && !asString.contains(".class ")) {
			rank += 4;
		}
		return rank;
	}	
	
	public static Function<StringCursor, Integer> standardAcronymRanking() {
		return StringScoreRanking::rankAcronymMatches;
	}
	
	private static int rankAcronymMatches(StringCursor targetCursor) {
		int rank = 3;
		// apply bonus for first character match
		if (targetCursor.setFirstMarkCurrent().indexOfCurrentMark() == 0) 
			rank += 1;
		// subtract for weak matches.  If there are more than 1 gap reduce ranking
		// if there are a large number of gaps, remove entirely
		int countUnMarkedWords = targetCursor.countUnMarkedWordsBetweenMarkers(0, targetCursor.markers().size() - 1);
		if (countUnMarkedWords > 0) {
			rank -=1;
		}
		if (countUnMarkedWords > 2) {
			rank = 0;
		}
		String asString = targetCursor.text.asString();
		if(!asString.contains("[class]") && !asString.contains(".class ")) {
			rank += 2;
		}
		return rank;
	}
	
	public static Function<StringCursor, Integer> standardNonContiguousSequenceRanking() {
		return StringScoreRanking::rankNonContiguousSequence;
	}
	
	private static int rankNonContiguousSequence(StringCursor targetCursor) {
		int rank = 2;
		
		int totalGaps = 0;
		while(!targetCursor.markerPositionTerminal()) {
			int partialBetween = targetCursor.countWordsBetween(targetCursor.indexOfCurrentMark(), targetCursor.indexOfNextMark());
			totalGaps += partialBetween;
			
			targetCursor.setNextMarkCurrent();
		}

		if (targetCursor.setFirstMarkCurrent().indexOfCurrentMark() == 0) 
			rank += 1;
		
		// are gaps too wide to be valuable?
		if (totalGaps > 1) {
			rank -= 1;
		}
		
		// if we have acronym matches.  All are single char.
		int previousMarkerIndex = -2;
		boolean acronymMatching = true;
		for (int markerIndex : targetCursor.markers()) {
			if (markerIndex - previousMarkerIndex == 1) {  // 2 markers are next to each other.  Not pure acronym match
				acronymMatching = false;
				break;
			}
			previousMarkerIndex = markerIndex;
		}
		// A good acryonym match should have been matched by the acryonym score and ranker.
		// Assume acronyms here are weak or out of order and discard
		if (acronymMatching) rank -= 1;
		String asString = targetCursor.text.asString();
		if(!asString.contains("[class]") && !asString.contains(".class ")) {
			rank += 2;
		}
		return rank;
	}
}
